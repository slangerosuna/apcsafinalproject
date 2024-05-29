package io.github.slangerosuna.engine.audio;

import java.util.HashMap;

import java.nio.*;

import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import io.github.slangerosuna.engine.utils.FileUtils;
import io.github.slangerosuna.engine.utils.WAVLoader;

public class Sound {
    private static HashMap<String, Sound> sounds = new HashMap<String, Sound>();
    public static Sound loadWaveFile(String file) {
        if (sounds.containsKey(file)) {
            var sound = sounds.get(file);
            sound.incrementRefCount();
            return sound;
        }

        int buffer = AL10.alGenBuffers();

        int[] chan = new int[1];
        int[] samplerate = new int[1];
        int[] bps = new int[1];
        int[] size = new int[1];
        byte[] data = WAVLoader.loadWAV(file, chan, samplerate, bps, size);
        if (data == null)
            return null;

        int format = -1;

        if (chan[0] == 1)
            if (bps[0] == 8)
                format = AL10.AL_FORMAT_MONO8;
            else if (bps[0] == 16)
                format = AL10.AL_FORMAT_MONO16;
        else if (chan[0] == 2)
            if (bps[0] == 8)
                format = AL10.AL_FORMAT_STEREO8;
            else if (bps[0] == 16)
                format = AL10.AL_FORMAT_STEREO16;

        if (format == -1)
            return null;

        float[] floatData = null;
        if (bps[0] == 8) {
            floatData = new float[data.length];
            for (int i = 0; i < data.length; i++) {
                floatData[i] = data[i] / 127.0f;
            }
        }
        else if (bps[0] == 16) {
            floatData = new float[data.length / 2];
            for (int i = 0; i < data.length; i += 2) {
                short value = (short) ((data[i] & 0xFF) | (data[i + 1] << 8));
                floatData[i / 2] = value / 32767.0f;
            }
        }

        AL10.alBufferData(buffer, format, floatData, samplerate[0]);

        Sound sound = new Sound(buffer, file);
        sounds.put(file, sound);

        return sound;
    }

    public Sound(String file) {
        this.file = file;
        bufferID = AL10.alGenBuffers();
        STBVorbisInfo info = STBVorbisInfo.malloc();
        ShortBuffer pcm = readVorbis(file, info);

        // Copy to buffer
        AL10.alBufferData(bufferID, info.channels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, pcm, info.sample_rate());
    }

    private final int bufferID;
    private int refCount = 1;
    private final String file;

    public void incrementRefCount() { refCount++; }

    public Sound(int bufferID, String file) {
        this.bufferID = bufferID;
        this.file = file;
    }

    public int getBufferID() { return bufferID; }

    public void kill() {
        if (--refCount > 0) return;

        sounds.remove(file);
        AL10.alDeleteBuffers(bufferID);
    }

    private ShortBuffer readVorbis(String filePath, STBVorbisInfo info) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer error = stack.mallocInt(1);

            var path = FileUtils.getNonRelativePath(filePath);
            long decoder = STBVorbis.stb_vorbis_open_filename(path, error, null);
            if (decoder == MemoryUtil.NULL) {
                throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }

            STBVorbis.stb_vorbis_get_info(decoder, info);

            int channels = info.channels();

            int lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);

            ShortBuffer result = MemoryUtil.memAllocShort(lengthSamples * channels);

            result.limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, result) * channels);
            STBVorbis.stb_vorbis_close(decoder);

            return result;
        }
    }
}

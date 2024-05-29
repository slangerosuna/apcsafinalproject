package io.github.slangerosuna.engine.audio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;

import io.github.slangerosuna.engine.core.ecs.Resource;

public class Audio implements Resource {
    public static final int type = Resource.registerResource("Audio");
    @Override public int getType() { return type; }
    @Override public void kill() {
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(device);
    }

    private long device;
    private long context;

    public Audio() {
        this.device = ALC10.alcOpenDevice((String)null);
        if (device == 0)
            throw new IllegalStateException("Failed to open the default device.");

        this.context = ALC10.alcCreateContext(device, new int[] { 0 });
        if (context == 0)
            throw new IllegalStateException("Failed to create an OpenAL context.");

        ALC10.alcMakeContextCurrent(context);

        AL.createCapabilities(ALC.createCapabilities(context));

        AL10.alEnable(AL10.AL_SOURCE_RELATIVE);
        AL10.alEnable(AL10.AL_DOPPLER_FACTOR);
        AL10.alDopplerFactor(1.0f);
        AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
        AL10.alDopplerVelocity(343.3f);
    }
}

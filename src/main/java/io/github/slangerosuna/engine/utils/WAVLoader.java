package io.github.slangerosuna.engine.utils;

import java.io.*;
import java.nio.*;

public class WAVLoader {
    private static boolean isBigEndian() {
        return ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
    }

    private static int convertToInt(byte[] buffer, int len) {
        int a = 0;
        if (!isBigEndian()) {
            for (int i = 0; i < len; i++)
                a |= (buffer[i] & 0xFF) << (i * 8);
        } else {
            for (int i = 0; i < len; i++)
                a |= (buffer[i] & 0xFF) << ((3 - i) * 8);
        }
        return a;
    }

    public static byte[] loadWAV(String fn, int[] chan, int[] samplerate, int[] bps, int[] size) {
        byte[] buffer = new byte[4];

        var file = FileUtils.loadAsString(fn);
        try (InputStream in = new ByteArrayInputStream(file.getBytes("UTF-8"))) {
            in.read(buffer, 0, 4);
            if (!new String(buffer, "UTF-8").equals("RIFF")) {
                System.out.println("this is not a valid WAVE file");
                return null;
            }
            in.read(buffer, 0, 4);
            in.read(buffer, 0, 4);      //WAVE
            in.read(buffer, 0, 4);      //fmt
            in.read(buffer, 0, 4);      //16
            in.read(buffer, 0, 2);      //1
            in.read(buffer, 0, 2);
            chan[0] = convertToInt(buffer, 2);
            in.read(buffer, 0, 4);
            samplerate[0] = convertToInt(buffer, 4);
            in.read(buffer, 0, 4);
            in.read(buffer, 0, 2);
            in.read(buffer, 0, 2);
            bps[0] = convertToInt(buffer, 2);
            in.read(buffer, 0, 4);      //data
            in.read(buffer, 0, 4);
            size[0] = convertToInt(buffer, 4);
            byte[] data = new byte[size[0]];
            in.read(data, 0, size[0]);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

package io.github.slangerosuna.engine.audio;

import org.lwjgl.openal.AL10;

import io.github.slangerosuna.engine.core.ecs.Component;

public class Source implements Component {
    public static final int type = Component.registerComponent("Source");
    @Override public int getType() { return type; }
    @Override public void kill() { 
        sound.kill();
        AL10.alDeleteSources(sourceID);
    }

    private int sourceID;
    private Sound sound;

    public Source(Sound sound) {
        sourceID = AL10.alGenSources();
        this.sound = sound;
        AL10.alSourcei(sourceID, AL10.AL_BUFFER, sound.getBufferID());
    }

    public void play() {
        AL10.alSourcePlay(sourceID);
    }
}

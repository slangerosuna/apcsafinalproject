package io.github.slangerosuna.engine.audio;

import org.lwjgl.openal.AL10;

import io.github.slangerosuna.engine.core.ecs.Component;
import io.github.slangerosuna.engine.physics.RigidBody;
import io.github.slangerosuna.engine.render.Transform;

public class Source implements Component {
    public static final int type = Component.registerComponent("Source");
    @Override public int getType() { return type; }
    @Override public void kill() { 
        sound.kill();
        AL10.alDeleteSources(sourceID);
    }

    private int sourceID;
    private Sound sound;
    private Transform transform;
    private RigidBody rigidBody;

    public Source(Sound sound, Transform transform, RigidBody rigidBody) {
        sourceID = AL10.alGenSources();
        this.sound = sound;
        AL10.alSourcei(sourceID, AL10.AL_BUFFER, sound.getBufferID());

        this.transform = transform;
        this.rigidBody = rigidBody;
    }

    public void update() {
        AL10.alSource3f(sourceID, AL10.AL_POSITION, transform.position.x, transform.position.y, transform.position.z);

        if (rigidBody == null) return;

        AL10.alSource3f(sourceID, AL10.AL_VELOCITY, rigidBody.velocity.x, rigidBody.velocity.y, rigidBody.velocity.z);
    }

    public void play() {
        AL10.alSourcePlay(sourceID);
    }

    public boolean isPlaying() {
        return AL10.alGetSourcei(sourceID, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }
}

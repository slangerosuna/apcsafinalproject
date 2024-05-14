package io.github.slangerosuna.engine.audio;

import org.lwjgl.openal.AL10;

import io.github.slangerosuna.engine.core.ecs.Component;
import io.github.slangerosuna.engine.math.vector.Vector3;
import io.github.slangerosuna.engine.physics.RigidBody;
import io.github.slangerosuna.engine.render.Transform;

public class Listener implements Component {
    public static final int type = Component.registerComponent("Listener");
    @Override public int getType() { return type; }
    @Override public void kill() { }
    
    private Transform transform;
    private RigidBody rigidBody;

    public void update() {
        AL10.alListener3f(AL10.AL_POSITION, transform.position.x, transform.position.y, transform.position.z);

        var forward = transform.forward();
        var right = forward.cross(new Vector3(0, 1, 0));
        var up = right.cross(forward).normalized();
        AL10.alListenerfv(AL10.AL_ORIENTATION, new float[] {forward.x, forward.y, forward.z, up.x, up.y, up.z } );

        if (rigidBody == null) return;

        AL10.alListener3f(AL10.AL_VELOCITY, rigidBody.velocity.x, rigidBody.velocity.y, rigidBody.velocity.z);
    }

    public Listener(Transform transform, RigidBody rigidBody) {
        this.transform = transform;
        this.rigidBody = rigidBody;
    }

    public Listener(Transform transform) {
        this(transform, null);
    }
}

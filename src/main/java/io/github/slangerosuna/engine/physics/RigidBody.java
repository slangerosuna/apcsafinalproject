package io.github.slangerosuna.engine.physics;

import io.github.slangerosuna.engine.core.ecs.Component;
import io.github.slangerosuna.engine.math.vector.Vector3;

public class RigidBody implements Component {
    public static final int type = Component.registerComponent("RigidBody");
    public int getType() { return type; }
    public void kill() { }
 
    private float mass;
    public float getMass() { return this.mass; }
    public boolean useGravity;
    private boolean grounded = false;
    public boolean isGrounded() { return this.grounded; }
    public void setGrounded(boolean grounded) { this.grounded = grounded; }

    public Vector3 velocity;

    public RigidBody(float mass, boolean useGravity) {
        this.mass = mass;
        this.useGravity = useGravity;

        this.velocity = new Vector3(0, 0, 0);
    }

    public void applyImpulse(Vector3 impulse) {
        this.velocity = this.velocity.add(impulse.divide(this.mass));
    }

    public float getKineticEnergy() {
        return 0.5f * this.mass * this.velocity.dot(this.velocity);
    }

    public void applyGravity(float gravity) {
        if (this.useGravity)
            this.velocity = this.velocity.add(new Vector3(0, -gravity, 0));
    }
}

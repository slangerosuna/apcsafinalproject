package io.github.slangerosuna.engine.render;

import io.github.slangerosuna.engine.core.ecs.Component;
import io.github.slangerosuna.engine.math.vector.Vector3;

public class Transform implements Component {
    public static final int type = Component.registerComponent("Transform");
    public int getType() { return type; }
    public void kill() { }

    public Vector3 position;
    public Vector3 rotation;
    public Vector3 scale;

    public Transform(Vector3 position, Vector3 rotation, Vector3 scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Vector3 forward() {
        var cosX = (float)Math.cos(Math.toRadians(rotation.x));
        return new Vector3((float)Math.sin(Math.toRadians(rotation.y)) * cosX,(float)Math.sin(Math.toRadians(rotation.x)), (float)Math.cos(Math.toRadians(rotation.y)) * cosX);
    }

    public String toString() {
        return "Position: " + position + "\nRotation: " + rotation + "\nScale: " + scale;
    }
}

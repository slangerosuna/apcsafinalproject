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

    public String toString() {
        return "Position: " + position + "\nRotation: " + rotation + "\nScale: " + scale;
    }
}

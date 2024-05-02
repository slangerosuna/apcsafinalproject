package io.github.slangerosuna.engine.render;

import io.github.slangerosuna.engine.core.ecs.Component;

public class Camera implements Component {
    public static final int type = Component.registerComponent("Camera");
    public int getType() { return type; }
    public void kill() { }

    private float fov, near, far;

    public float getFov() { return fov; }
    public float getNear() { return near; }
    public float getFar() { return far; }

    public Camera(float fov, float near, float far) {
        this.fov = fov;
        this.near = near;
        this.far = far;
    }
}

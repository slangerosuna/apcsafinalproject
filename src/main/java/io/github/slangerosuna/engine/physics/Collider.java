package io.github.slangerosuna.engine.physics;

import io.github.slangerosuna.engine.core.ecs.Component;
import io.github.slangerosuna.engine.math.vector.Vector3;
import io.github.slangerosuna.engine.render.Transform;

public class Collider implements Component {
    public static final int type = Component.registerComponent("Collider");
    public int getType() {
        return type;
    }
    public void kill() {}
    private float w, h, d; // width, height, depth
    public float getWidth() { return this.w; }
    public float getHeight() { return this.h; }
    public float getDepth() { return this.d; }
    
    private Transform transform;

    public Collider(float w, float h, float d, Transform transform) {
        this.w = w;
        this.h = h;
        this.d = d;

        this.transform = transform;
    }

    public boolean intersects(Collider other) {
        return widthIntersects(other) && heightIntersects(other) && depthIntersects(other);
    }

    public Vector3 getIntersectionDirection(Collider other) {
        float x = widthIntersects(other) ? 1 : 0;
        float y = heightIntersects(other) ? 1 : 0;
        float z = depthIntersects(other) ? 1 : 0;

        x = this.transform.position.x > other.transform.position.x ? x : -x;
        y = this.transform.position.y > other.transform.position.y ? y : -y;
        z = this.transform.position.z > other.transform.position.z ? z : -z;

        return new Vector3(x, y, z);
    }

    public boolean widthIntersects(Collider other) {
        return Math.abs(this.transform.position.x - other.transform.position.x) < (this.w + other.w) / 2;
    }

    public boolean heightIntersects(Collider other) {
        return Math.abs(this.transform.position.y - other.transform.position.y) < (this.h + other.h) / 2;
    }

    public boolean depthIntersects(Collider other) {
        return Math.abs(this.transform.position.z - other.transform.position.z) < (this.d + other.d) / 2;
    }
}

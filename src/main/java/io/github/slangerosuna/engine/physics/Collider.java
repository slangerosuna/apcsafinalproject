package io.github.slangerosuna.engine.physics;

import io.github.slangerosuna.engine.core.ecs.Component;
import io.github.slangerosuna.engine.math.vector.Vector3;
import io.github.slangerosuna.engine.render.Transform;

public class Collider implements Component {
    public static final int type = Component.registerComponent("Collider");
    public int getType() { return type; }
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

    public Collider(Vector3 cornerA, Vector3 cornerB) {
        Vector3 dimensions = cornerB.sub(cornerA);
        this.w = Math.abs(dimensions.x);
        this.h = Math.abs(dimensions.y);
        this.d = Math.abs(dimensions.z);

        Vector3 center = cornerA.add(dimensions.divide(2.0f));

        this.transform = new Transform(center, Vector3.zero(), new Vector3(1, 1, 1));
    }

    public boolean intersects(Collider other) {
        return widthIntersects(other) && heightIntersects(other) && depthIntersects(other);
    }

    /*
     * @returns the normal of the face that other is intersecting with
     */
    public Vector3 getIntersectionDirection(Collider other, Vector3 velocity) {
        var displacement = this.transform.position.sub(other.transform.position);
        displacement = displacement.multiply(new Vector3(1 / (w + other.w), 1 / (h + other.h), 1 / (d + other.d)));
        var axis = new Vector3(0, 0, 0);

        if (Math.abs(displacement.x) >= Math.abs(displacement.y) && Math.abs(displacement.x) >= Math.abs(displacement.z)) {
            axis = new Vector3(displacement.x, 0, 0);
        } else if (Math.abs(displacement.y) >= Math.abs(displacement.x) && Math.abs(displacement.y) >= Math.abs(displacement.z)) {
            axis = new Vector3(0, displacement.y, 0);
        } else {
            axis = new Vector3(0, 0, displacement.z);
        }

        return axis.normalized();
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

package io.github.slangerosuna.engine.math.vector;

public class Vector4 {
	public float x, y, z, w;

	public Vector4(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

    public float dot(Vector4 other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public Vector4 add(Vector4 other) {
        return new Vector4(x + other.x, y + other.y, z + other.z, w + other.w);
    }

    public Vector4 sub(Vector4 other) {
        return new Vector4(x - other.x, y - other.y, z - other.z, w - other.w);
    }

    public Vector4 cross(Vector4 other) {
        return new Vector4(y * other.z - z * other.y,
                           z * other.x - x * other.z,
                           x * other.y - y * other.x,
                           0);
    }

	public static Vector4 zero() {
		return new Vector4(0, 0, 0, 0);
	}
}

package io.github.slangerosuna.engine.math.vector;

public class Vector3 {
	public float x, y, z;

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3 add(Vector3 other) {
		return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 sub(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
	}

    public float dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3 cross(Vector3 other) {
        return new Vector3(y * other.z - z * other.y,
                           z * other.x - x * other.z,
                           x * other.y - y * other.x);
    }

	public static Vector3 zero() {
		return new Vector3(0, 0, 0);
	}

	public static Vector3 add(Vector3 a, Vector3 b){
		return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
	}

	public boolean equals(Object other) {
		if (other instanceof Vector3) {
			return equals((Vector3)other);
		}
		return false;
	}

	public boolean equals(Vector3 other) {
		return x == other.x && y == other.y && z == other.z;
	}

	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}

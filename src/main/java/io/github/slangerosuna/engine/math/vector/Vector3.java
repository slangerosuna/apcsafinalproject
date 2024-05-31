package io.github.slangerosuna.engine.math.vector;

public class Vector3 {
	public float x, y, z;

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float length() { return (float)Math.sqrt(x * x + y * y + z * z); }
	public Vector3 normalized() {
		float length = length();
		return new Vector3(x / length, y / length, z / length);
	}

	public Vector3 add(Vector3 other) { return new Vector3(x + other.x, y + other.y, z + other.z); }
    public Vector3 sub(Vector3 other) { return new Vector3(x - other.x, y - other.y, z - other.z); }
    public float dot(Vector3 other) { return x * other.x + y * other.y + z * other.z; }
	public Vector3 multiply(float scalar) { return new Vector3(x * scalar, y * scalar, z * scalar); }
	public Vector3 divide(float scalar) { return new Vector3(x / scalar, y / scalar, z / scalar); }

    public Vector3 cross(Vector3 other) {
        return new Vector3(y * other.z - z * other.y,
                           z * other.x - x * other.z,
                           x * other.y - y * other.x);
    }

	public static Vector3 zero() { return new Vector3(0, 0, 0); }
	public static Vector3 one() { return new Vector3(1, 1, 1); }
	public Vector3 multiply(Vector3 other) { return new Vector3(x * other.x, y * other.y, z * other.z); }
	public static Vector3 add(Vector3 a, Vector3 b) { return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z); }

	public boolean equals(Object other) {
		if (other instanceof Vector3) {
			return equals((Vector3)other);
		}
		return false;
	}

	public boolean equals(Vector3 other) { return x == other.x && y == other.y && z == other.z; }
	public String toString() { return "(" + x + ", " + y + ", " + z + ")"; }
	@Override public int hashCode() { return Float.hashCode(x) + Float.hashCode(y) + Float.hashCode(z); }
}

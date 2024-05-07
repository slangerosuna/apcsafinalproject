package io.github.slangerosuna.engine.math.vector;

public class Vector2 {
	public float x, y;

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

    public float dot(Vector2 other) {
        return x * other.x + y * other.y;
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }

	public static Vector2 zero() {
		return new Vector2(0, 0);
	}

	@Override
	public int hashCode() {
		return Float.hashCode(x) + Float.hashCode(y);
	}
}

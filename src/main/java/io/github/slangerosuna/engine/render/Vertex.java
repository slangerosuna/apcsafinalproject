package io.github.slangerosuna.engine.render;

import io.github.slangerosuna.engine.math.vector.*;

public class Vertex {
	public Vector3 position;
	public Vector2 UV;
	public Vector3 normal;

	public Vertex(Vector3 position, Vector2 UV, Vector3 normal) {
		this.position = position;
		this.UV = UV;
		this.normal = normal;
	}

	@Override
	public int hashCode() {
		return position.hashCode() + UV.hashCode() + normal.hashCode();
	}
}

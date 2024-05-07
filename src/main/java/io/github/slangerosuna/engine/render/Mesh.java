package io.github.slangerosuna.engine.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import io.github.slangerosuna.engine.core.ecs.Component;
import io.github.slangerosuna.engine.math.vector.*;

public class Mesh implements Component {
    public static final int type = Component.registerComponent("Mesh");
    public int getType() {
        return type;
    }
    public void kill() {
		if (--refCount > 0) return;
        destroy();
    }

	private int refCount = 1;
	public void incrementRefCount() { refCount++; }

	private Vertex[] vertices;
	private int[] indices;
	private int vao, pbo, ibo, nbo, uvbo;

	public static Mesh getRectMesh() {
		return new Mesh(new Vertex[] {
				new Vertex(new Vector3(-0.5f, 0.5f, 0.0f), new Vector2(0, 0), new Vector3(1, 0, 0)),
				new Vertex(new Vector3(0.5f, 0.5f, 0.0f), new Vector2(1.0f, 0), new Vector3(0, 0, 1)),
				new Vertex(new Vector3(0.5f, -0.5f, 0.0f), new Vector2(1.0f, 1.0f), new Vector3(0, 1, 0)),
				new Vertex(new Vector3(-0.5f, -0.5f, 0.0f), new Vector2(0, 1.0f), new Vector3(1, 1, 0)) },
			new int[] {
				0, 1, 2,
				0, 3, 2,
				2, 1, 0, // allows it to be seen from the other side if backface culling is enabled
				2, 3, 0 
			});
	}

	public Mesh(Vertex[] vertices, int[] indices) {
		this.vertices = vertices;
		this.indices = indices;
	}

	public void create() {
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);

		FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] positionData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			positionData[i * 3] = vertices[i].position.x;
			positionData[i * 3 + 1] = vertices[i].position.y;
			positionData[i * 3 + 2] = vertices[i].position.z;
		}
		positionBuffer.put(positionData).flip();

		pbo = storeData(positionBuffer, 0, 3);

		FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] normalData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			normalData[i * 3] = vertices[i].normal.x;
			normalData[i * 3 + 1] = vertices[i].normal.y;
			normalData[i * 3 + 2] = vertices[i].normal.z;
		}
		normalBuffer.put(normalData).flip();

		nbo = storeData(normalBuffer, 1, 3);

		FloatBuffer uvBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
		float[] uvData = new float[vertices.length * 2];
		for (int i = 0; i < vertices.length; i++) {
			uvData[i * 2] = vertices[i].UV.x;
			uvData[i * 2 + 1] = vertices[i].UV.y;
		}
		uvBuffer.put(uvData).flip();

		uvbo = storeData(uvBuffer, 2, 2);

		IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
		indicesBuffer.put(indices).flip();

		ibo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	private int storeData(FloatBuffer buffer, int index, int size) {
		int bufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return bufferID;
	}

	public void destroy() {
		GL15.glDeleteBuffers(nbo);
		GL15.glDeleteBuffers(pbo);
		GL15.glDeleteBuffers(ibo);
		GL15.glDeleteBuffers(uvbo);

		GL30.glDeleteVertexArrays(vao);
	}

	public Vertex[] getVertices() {
		return vertices;
	}

	public int getNBO() {
		return nbo;
	}

	public int getUVBO() {
		return uvbo;
	}

	public int[] getIndices() {
		return indices;
	}

	public int getVAO() {
		return vao;
	}

	public int getPBO() {
		return pbo;
	}

	public int getIBO() {
		return ibo;
	}
}

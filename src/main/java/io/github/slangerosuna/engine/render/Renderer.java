package io.github.slangerosuna.engine.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import java.util.Arrays;

import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.io.Window;
import io.github.slangerosuna.engine.math.matrix.Matrix4;
import io.github.slangerosuna.engine.math.vector.Vector3;

public class Renderer extends System {
	private Shader shader;
	private Matrix4 projection;

	public Renderer(Shader shader, Window window, Camera camera) {
        super(
            SystemType.RENDER,
            "ENTITY AND ( HAS Mesh HAS Transform HAS Material )",
			"ENTITY AND ( HAS Camera HAS Transform )",
            "RESOURCE Window",
			"SYNC"
        );
		this.shader = shader;
		projection = Matrix4.projection(
			camera.getFov(),
			window.getWidth() / window.getHeight(), 
			camera.getNear(), 
			camera.getFar()
		);
	}

    @Override
    public void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime) {
		var cameraEntity = Arrays.stream(queriedEntities).filter(e -> e.hasComponent(Camera.type)).findFirst().get();
		var camera = (Camera)cameraEntity.getComponent(Camera.type);
		var cameraTransform = (Transform)cameraEntity.getComponent(Transform.type);

		shader.bind();
		var err = GL11.glGetError();
		if (err != GL11.GL_NO_ERROR) {
			java.lang.System.out.println("OpenGL Error on shader bind: " + err);
		}

		if (queriedResources.length == 0) return;

		if (((Window)queriedResources[0]).justResized) {
			projection = Matrix4.projection(
				camera.getFov(),
				((float)((Window)queriedResources[0]).getWidth()) / ((Window)queriedResources[0]).getHeight(), 
				camera.getNear(), 
				camera.getFar()
			);
			((Window)queriedResources[0]).justResized = false;
		}

		shader.setUniform(
			"projection",
			projection
		);

		var view = Matrix4.view(
			cameraTransform.position,
			cameraTransform.rotation
		);

		shader.setUniform(
			"view",
			view
		);

		var lightDir = new Vector3(0, 0, 1);
		shader.setUniform(
			"lightDir",
			lightDir
		);

		var dirLightColor = new Vector3(0.7f, 0.7f, 0.7f);
		shader.setUniform(
			"dirLightColor",
			dirLightColor
		);

		var ambient = new Vector3(0.3f, 0.3f, 0.3f);

		shader.setUniform(
			"ambient",
			ambient
		);

        for(Entity entity : queriedEntities) {
			if (entity.hasComponent(Camera.type)) continue;

            Mesh mesh = (Mesh)entity.getComponent(Mesh.type);
            Transform transform = (Transform)entity.getComponent(Transform.type);
            Material material = (Material)entity.getComponent(Material.type);
            renderObject(transform, mesh, material);
        }
		shader.unBind();
		err = GL11.glGetError();
		if (err != GL11.GL_NO_ERROR) {
			java.lang.System.out.println("OpenGL Error on shader unbind: " + err);
		}

		err = GL11.glGetError();
		if (err != GL11.GL_NO_ERROR) {
			java.lang.System.out.println("OpenGL Error: " + err);
		}
    }

	public void renderObject(Transform transform, Mesh mesh, Material material) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, material.getTextureID());
		var err = GL11.glGetError();
		if (err != GL11.GL_NO_ERROR) {
			java.lang.System.out.println("OpenGL Error on texture: " + err);
		}

		GL30.glBindVertexArray(mesh.getVAO());
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		GL30.glEnableVertexAttribArray(2);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.getIBO());
		err = GL11.glGetError();
		if (err != GL11.GL_NO_ERROR) {
			java.lang.System.out.println("OpenGL Error on buffer binding: " + err);
		}

		var model = Matrix4.transform(transform.position, transform.rotation, transform.scale);
		shader.setUniform("model", model);
		err = GL11.glGetError();
		if (err != GL11.GL_NO_ERROR) {
			java.lang.System.out.println("OpenGL Error on uniform setting: " + err);
		}

		GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndices().length, GL11.GL_UNSIGNED_INT, 0);
		err = GL11.glGetError();
		if (err != GL11.GL_NO_ERROR) {
			java.lang.System.out.println("OpenGL Error on draw: " + err);
		}

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glDisableVertexAttribArray(0);
		GL30.glDisableVertexAttribArray(1);
		GL30.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		err = GL11.glGetError();
		if (err != GL11.GL_NO_ERROR) {
			java.lang.System.out.println("OpenGL Error on buffer unbind: " + err);
		}
	}
}

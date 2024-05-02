package io.github.slangerosuna.engine.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.io.Window;
import io.github.slangerosuna.engine.math.matrix.Matrix4;

public class Renderer extends System {
	private Shader shader;

	public Renderer(Shader shader) {
        super(
            SystemType.RENDER,
            "ENTITY AND ( HAS Mesh HAS Transform HAS Material )",
            "RESOURCE Window",
			"SYNC"
        );
		this.shader = shader;
	}

    @Override
    public void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime) {
        for(Entity entity : queriedEntities) {
            Mesh mesh = (Mesh)entity.getComponent(Mesh.type);
            Transform transform = (Transform)entity.getComponent(Transform.type);
            Material material = (Material)entity.getComponent(Material.type);
            renderObject(transform, mesh, material);
            ((Window)queriedResources[0]).swapBuffers();
        }
    }

	public void renderObject(Transform transform, Mesh mesh, Material material) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, material.getTextureID());

		shader.bind();

		GL30.glBindVertexArray(mesh.getVAO());
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		GL30.glEnableVertexAttribArray(2);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.getIBO());
		shader.setUniform("model", Matrix4.transform(transform.position, transform.rotation, transform.scale));

		GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndices().length, GL11.GL_UNSIGNED_INT, 0);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glDisableVertexAttribArray(0);
		GL30.glDisableVertexAttribArray(1);
		GL30.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		shader.unBind();
	}
}

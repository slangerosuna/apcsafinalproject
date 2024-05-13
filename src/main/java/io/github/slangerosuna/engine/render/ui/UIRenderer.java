package io.github.slangerosuna.engine.render.ui;

import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.math.vector.Vector3;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.render.Material;
import io.github.slangerosuna.engine.render.Mesh;
import io.github.slangerosuna.engine.render.Shader;

public class UIRenderer extends System {
	private Shader shader;
    private Mesh rect;
    private int indicesLength;
    public UIRenderer(Shader shader) {
        super(
            SystemType.RENDER,
            "ENTITY AND ( HAS UIElement HAS Material )",
            "RESOURCE Window",
            "SYNC"
        );
        rect = Mesh.getRectMesh();
        rect.create();
        indicesLength = rect.getIndices().length;
        this.shader = shader;
    }

    @Override
    public void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime) {
        if (queriedResources.length == 0 || queriedEntities.length == 0) return;
        shader.bind();
		GL30.glBindVertexArray(rect.getVAO());
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		GL30.glEnableVertexAttribArray(2);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, rect.getIBO());

        for (var entity : queriedEntities) {
            var uiElement = (UIElement)entity.getComponent(UIElement.type);
            var material = (Material)entity.getComponent(Material.type);
            renderUIObject(uiElement, material);
        }

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glDisableVertexAttribArray(0);
		GL30.glDisableVertexAttribArray(1);
		GL30.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
        shader.unBind();
    }

    private void renderUIObject(UIElement uiElement, Material material) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, material.getTextureID());

        shader.setUniform("pos", new Vector3(uiElement.x, uiElement.y, 1));
        shader.setUniform("scale", new Vector3(uiElement.width, uiElement.height, 1));

        GL11.glDrawElements(GL11.GL_TRIANGLES, indicesLength, GL11.GL_UNSIGNED_INT, 0);
    }
}

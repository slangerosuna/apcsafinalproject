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

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
    }

    @Override
    public void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime) {
        var err = GL11.glGetError();
        if (err != GL11.GL_NO_ERROR) {
            java.lang.System.out.println("Error in normal rendering: " + err);
        }
        if (queriedResources.length == 0 || queriedEntities.length == 0) return;
        shader.bind();

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

		GL30.glBindVertexArray(rect.getVAO());
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		GL30.glEnableVertexAttribArray(2);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, rect.getIBO());
        err = GL11.glGetError();
        if (err != GL11.GL_NO_ERROR) {
            java.lang.System.out.println("Error on mesh binding in UI Renderer: " + err);
        }

        for (var entity : queriedEntities) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            var uiElement = (UIElement)entity.getComponent(UIElement.type);
            var material = (Material)entity.getComponent(Material.type);
            renderUIObject(uiElement, material);
        }

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glDisableVertexAttribArray(0);
		GL30.glDisableVertexAttribArray(1);
		GL30.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
        err = GL11.glGetError();
        if (err != GL11.GL_NO_ERROR) {
            java.lang.System.out.println("Error on mesh unbinding in UI Renderer: " + err);
        }
        shader.unBind();
        err = GL11.glGetError();
        if (err != GL11.GL_NO_ERROR) {
            java.lang.System.out.println("Error on shader unbinding in UI Renderer: " + err);
        }
    }

    private void renderUIObject(UIElement uiElement, Material material) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, material.getTextureID());
        var err = GL11.glGetError();
        if (err != GL11.GL_NO_ERROR) {
            java.lang.System.out.println("Error on texture binding in UI Renderer: " + err);
        }

        shader.setUniform("pos", new Vector3(uiElement.x, uiElement.y, 1));
        err = GL11.glGetError();
        if (err != GL11.GL_NO_ERROR) {
            java.lang.System.out.println("Error on uniform setting in UI Renderer: " + err);
        }
        shader.setUniform("scale", new Vector3(uiElement.width, uiElement.height, 1));
        err = GL11.glGetError();
        if (err != GL11.GL_NO_ERROR) {
            java.lang.System.out.println("Error on uniform setting in UI Renderer: " + err);
        }

        GL11.glDrawElements(GL11.GL_TRIANGLES, indicesLength, GL11.GL_UNSIGNED_INT, 0);
        err = GL11.glGetError();
        if (err != GL11.GL_NO_ERROR) {
            java.lang.System.out.println("Error on draw in UI Renderer: " + err);
        }
    }
}

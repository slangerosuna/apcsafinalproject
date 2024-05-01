package io.github.slangerosuna.engine.render;

import org.lwjgl.opengl.GL13;

import io.github.slangerosuna.engine.core.ecs.Component;

public class Material implements Component {
    public static final int type = Component.registerComponent("Material");
    public int getType() {
        return type;
    }
    public void kill() {
        destroy();
    }
	private Texture texture;
	private float width, height;
	private int textureID;

	public Material(String path) {
		try {
			texture = Texture.loadTexture(path);
		} catch (Exception e){
			System.err.println("could not find file at " + path);
		}

		create();
	}

	public void create() {
		width = texture.getWidth();
		height = texture.getHeight();
		textureID = texture.getTextureID();
	}

	public void destroy() {
		GL13.glDeleteTextures(textureID);
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public int getTextureID() {
		return textureID;
	}
}

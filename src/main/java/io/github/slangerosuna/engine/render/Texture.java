package io.github.slangerosuna.engine.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class Texture {
    private int id, width, height;

    private int refCount;
    private String path;

    private static HashMap<String, Texture> idMap = new HashMap<String, Texture>();

    private Texture(int id, int width, int height, String path){
        this.id = id;
        this.width = width;
        this.height = height;

        refCount = 1;
        this.path = path;
    }

    public int getTextureID(){
        return id;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public static Texture loadTexture(String texture){
        int width;
        int height;
        ByteBuffer buffer;

        try (MemoryStack stack = MemoryStack.stackPush()){
            if(idMap.containsKey(texture)){
                var tex = idMap.get(texture);
                tex.refCount++;
                return tex;
            }
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            var filePath = Texture.class.getResource(texture).getPath();
            buffer = STBImage.stbi_load(filePath, w, h, channels, 4);
            if(buffer ==null) {
                throw new Exception("Can't load file "+texture+" "+STBImage.stbi_failure_reason());
            }
            width = w.get();
            height = h.get();

            int id = GL11.glGenTextures();

            var tex = new Texture(id, width, height, texture);

            idMap.put(texture, tex);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            STBImage.stbi_image_free(buffer);

            return tex;
        }catch(Exception e){
            System.err.println("Can't load file "+texture);
            e.printStackTrace();
        }

        return null;
    }

    public void kill() {
        if (--refCount > 0) return;
        
        idMap.remove(path);
		GL11.glDeleteTextures(id);
    }
}

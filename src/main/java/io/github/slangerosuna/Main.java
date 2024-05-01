package io.github.slangerosuna;

import io.github.slangerosuna.engine.core.ecs.*;
import io.github.slangerosuna.engine.render.*;
import io.github.slangerosuna.engine.io.*;

public class Main {
    private static final String windowTitle = "Hello, World!";
    private static final int windowWidth = 300;
    private static final int windowHeight = 300;

    private static final String vertexPath = "/io/github/slangerosuna/engine/render/shaders/MainVertex.glsl";
    private static final String fragmentPath = "/io/github/slangerosuna/engine/render/shaders/MainFrag.glsl";

    private static final String texturePath = "/io/github/slangerosuna/resources/textures/randomAsset.png";

    private static final int workerThreads = 8;

    public static void main(String[] args) {
        var scene = new Scene(workerThreads);
        var entity = new Entity(scene, new Transform());
        scene.addEntity(entity);

        var window = new Window(windowWidth, windowHeight, windowTitle);
        window.setBackgroundColor(0.05f, 0.045f, 0.06f);


        var windowUpdate = new WindowUpdate();
        scene.addSystem(windowUpdate);

        window.create();

        var mesh = Mesh.getRectMesh();
        mesh.create();
        entity.addComponent(mesh);

        var mat = new Material(texturePath);
        entity.addComponent(mat);

        var shader = new Shader(vertexPath, fragmentPath);
        var renderer = new Renderer(shader);
        scene.addSystem(renderer);

        shader.create();

        var input = new Input();

        scene.addResource(window);
        scene.addResource(input);
        scene.init();

        while (!window.shouldClose()) {
            scene.update();
        }

        scene.deInit();
    }
}

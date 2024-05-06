package io.github.slangerosuna;

import io.github.slangerosuna.engine.core.ecs.*;
import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.render.*;
import io.github.slangerosuna.engine.io.*;
import io.github.slangerosuna.engine.math.vector.Vector3;

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
        var entity = new Entity(scene, new Transform(new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 1)));
        var camera = new Entity(scene, new Camera(90, 0.1f, 1000), new Transform(new Vector3(0, 0, -5f), new Vector3(0, 0, 0), new Vector3(1, 1, 1)));
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
        shader.create();
        var renderer = new Renderer(shader, window, (Camera)camera.getComponent(Camera.type));
        scene.addSystem(renderer);
        var rotateCamera = new RotateCamera();
        scene.addSystem(rotateCamera);

        var input = new Input();

        scene.addResource(window);
        scene.addResource(input);
        scene.init();


        while (!window.shouldClose()) {
            scene.update();
        }

        scene.deInit();

        java.lang.System.exit(0); // Kills worker threads
    }
}

class RotateCamera extends System {
    public RotateCamera() {
        super(
            SystemType.UPDATE,
            "ENTITY AND ( NOT HAS Camera HAS Transform )"
        );
    }

    @Override
    public void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime) {
        var cameraEntity = queriedEntities[0];
        var cameraTransform = (Transform)cameraEntity.getComponent(Transform.type);

        cameraTransform.position.y += deltaTime;
     }
}
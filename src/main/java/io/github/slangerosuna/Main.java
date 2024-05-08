package io.github.slangerosuna;

import io.github.slangerosuna.engine.core.ecs.*;
import io.github.slangerosuna.engine.render.*;
import io.github.slangerosuna.engine.io.*;
import io.github.slangerosuna.engine.math.vector.Vector3;
import io.github.slangerosuna.engine.physics.Collider;
import io.github.slangerosuna.engine.physics.RigidBody;
import io.github.slangerosuna.engine.physics.PhysicsUpdate;
import io.github.slangerosuna.game.RotateCamera;
import io.github.slangerosuna.game.Player;
import io.github.slangerosuna.game.PlayerController;
import io.github.slangerosuna.engine.utils.ObjLoader;

public class Main {
    private static final String windowTitle = "Hello, World!";
    private static final int windowWidth = 300;
    private static final int windowHeight = 300;

    private static final String vertexPath = "/io/github/slangerosuna/engine/render/shaders/MainVertex.glsl";
    private static final String fragmentPath = "/io/github/slangerosuna/engine/render/shaders/MainFrag.glsl";

    private static final String modelPath = "/io/github/slangerosuna/resources/models/amoogus.obj";
    private static final String texturePath = "/io/github/slangerosuna/resources/textures/amoogus.png";

    private static final int workerThreads = 8;

    public static void main(String[] args) {
        var scene = new Scene(workerThreads);
        var entity = new Entity(scene, new Transform(new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 1)));
        var entity1 = new Entity(scene, new Transform(new Vector3(3, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 1)));
        var entity2 = new Entity(scene, new Transform(new Vector3(-3, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 1)));
        var entity3 = new Entity(scene, new Transform(new Vector3(0, 0, 3), new Vector3(0, 0, 0), new Vector3(1, 1, 1)));
        
        var cameraTransform = new Transform(new Vector3(0, 0, -5f), new Vector3(0, 180, 0), new Vector3(1, 1, 1));
        var camera = new Entity(scene,
            new Camera(90, 0.1f, 1000),
            cameraTransform,
            new Player(1.0f),
            new Collider(1.0f, 1.0f, 1.0f, cameraTransform),
            new RigidBody(1.0f, false)
        );
        var window = new Window(windowWidth, windowHeight, windowTitle);
        window.setBackgroundColor(0.05f, 0.045f, 0.06f);

        var windowUpdate = new WindowUpdate();
        scene.addSystem(windowUpdate);

        window.create();

        var mesh = ObjLoader.loadObj(modelPath);
        entity.addComponent(mesh);
        entity1.addComponent(mesh);
        entity2.addComponent(mesh);
        entity3.addComponent(mesh);

        var mat = new Material(texturePath);
        entity.addComponent(mat);
        entity1.addComponent(mat);
        entity2.addComponent(mat);
        entity3.addComponent(mat);

        var shader = new Shader(vertexPath, fragmentPath);
        shader.create();
        var renderer = new Renderer(shader, window, (Camera)camera.getComponent(Camera.type));
        scene.addSystem(renderer);
        var rotateCamera = new RotateCamera();
        scene.addSystem(rotateCamera);
        var playerController = new PlayerController();
        scene.addSystem(playerController);
        var physicsUpdate = new PhysicsUpdate();
        scene.addSystem(physicsUpdate);

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

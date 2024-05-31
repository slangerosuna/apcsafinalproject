package io.github.slangerosuna;

import io.github.slangerosuna.engine.audio.Audio;
import io.github.slangerosuna.engine.audio.Listener;
import io.github.slangerosuna.engine.audio.LoopAudio;
import io.github.slangerosuna.engine.audio.Sound;
import io.github.slangerosuna.engine.audio.Source;
import io.github.slangerosuna.engine.audio.UpdateListeners;
import io.github.slangerosuna.engine.audio.UpdateSources;
import io.github.slangerosuna.engine.core.ecs.*;
import io.github.slangerosuna.engine.render.*;
import io.github.slangerosuna.engine.render.ui.UIElement;
import io.github.slangerosuna.engine.render.ui.UIRenderer;
import io.github.slangerosuna.engine.io.*;
import io.github.slangerosuna.engine.math.vector.Vector3;
import io.github.slangerosuna.engine.physics.Collider;
import io.github.slangerosuna.engine.physics.RigidBody;
import io.github.slangerosuna.engine.physics.PhysicsUpdate;
import io.github.slangerosuna.game.RotateCamera;
import io.github.slangerosuna.game.Player;
import io.github.slangerosuna.game.PlayerController;
import io.github.slangerosuna.engine.utils.ObjLoader;
import io.github.slangerosuna.game.dungeon_generation.*;
import io.github.slangerosuna.game.enemy.Enemy;
import io.github.slangerosuna.game.enemy.EnemyController;
import io.github.slangerosuna.game.enemy.Projectile;
import io.github.slangerosuna.game.enemy.ProjectileController;

public class Main {
    private static final String windowTitle = "Hello, World!";
    private static final int windowWidth = 300;
    private static final int windowHeight = 300;

    private static final String vertexPath = "/io/github/slangerosuna/engine/render/shaders/MainVertex.glsl";
    private static final String fragmentPath = "/io/github/slangerosuna/engine/render/shaders/MainFrag.glsl";

    private static final String modelPath = "/io/github/slangerosuna/resources/models/eye.obj";
    private static final String texturePath = "/io/github/slangerosuna/resources/textures/eye.jpg";

    private static final int workerThreads = 8;

    public static void main(String[] args) {
        var scene = new Scene(workerThreads);

        new Projectile(1.0f); // to register component

        var audio = new Audio();
        scene.addResource(audio);


        var cameraTransform = new Transform(new Vector3(0, 10, 0f), new Vector3(0, 180, 0), new Vector3(1, 1, 1));
        var camera = new Entity(scene,
            new Camera(90, 0.1f, 1000),
            cameraTransform,
            new Player(3.0f),
            new Collider(1.0f, 1.0f, 1.0f, cameraTransform),
            new RigidBody(1.0f, true)
        );
        var listener = new Listener(cameraTransform, (RigidBody)camera.getComponent(RigidBody.type));
        camera.addComponent(listener);
        camera.addComponent(new Source(new Sound("/io/github/slangerosuna/resources/audio/pigstep.ogg"), (Transform)camera.getComponent(Transform.type), (RigidBody)camera.getComponent(RigidBody.type)));

        ((Source)camera.getComponent(Source.type)).play();

        var window = new Window(windowWidth, windowHeight, windowTitle);
        window.setBackgroundColor(0.05f, 0.045f, 0.06f);

        var windowUpdate = new WindowUpdate();
        scene.addSystem(windowUpdate);

        window.create();

        RoomPrefab[] prefabs = DungeonGenerator.defaultRoomPrefabs();
        Vector3 startPos = new Vector3(0f, 10f, 0f);
        int pathLen = 20;
        int maxSidePathLength = 5;
        float sparseness = 1.5f;
        float recursion = 1.5f;
        Room[] dungeon = DungeonGenerator.generateDungeon(scene, prefabs[0], prefabs, startPos, pathLen, maxSidePathLength, sparseness, recursion);

        java.util.ArrayList<Entity> enemies = new java.util.ArrayList<Entity>();
        float enemyScale = 0.5f;
        float enemyMass = 1.0f;
        float enemySpeed = 0.9f;

        Room curRoom;
        Entity curEnemy;
        for (int i = 3; i < dungeon.length; i += 5) {
            curRoom = dungeon[i];
            curEnemy = new Entity(scene);
            curEnemy.addComponent( new Transform(curRoom.transform.position, Vector3.zero(), Vector3.one().divide(1f / enemyScale)) );
            curEnemy.addComponent( new Collider(1f, 2f, 1f, (Transform)curEnemy.getComponent(Transform.type)) );
            curEnemy.addComponent( new RigidBody(enemyMass, true) );
            curEnemy.addComponent( new Enemy(enemySpeed, false) );
            curEnemy.addComponent( new Material(texturePath) );
            curEnemy.addComponent( ObjLoader.loadObj(modelPath) );

            enemies.add(curEnemy);
        }

        new UIElement(0.5f, 0.5f, 0.5f, 0.5f); // to register component

        var shader = new Shader(vertexPath, fragmentPath);
        shader.create();
        var renderer = new Renderer(shader, window, (Camera)camera.getComponent(Camera.type));
        scene.addSystem(renderer);

        var uiShader = new Shader("/io/github/slangerosuna/engine/render/shaders/UIVertex.glsl", "/io/github/slangerosuna/engine/render/shaders/UIFrag.glsl");
        uiShader.create();
        var uiRenderer = new UIRenderer(uiShader);
        scene.addSystem(uiRenderer);

        var bufferSwapper = new BufferSwapper();
        scene.addSystem(bufferSwapper);
        
        var rotateCamera = new RotateCamera();
        scene.addSystem(rotateCamera);
        var playerController = new PlayerController();
        scene.addSystem(playerController);
        var physicsUpdate = new PhysicsUpdate();
        scene.addSystem(physicsUpdate);

        scene.addSystem(new EnemyController());
        scene.addSystem(new ProjectileController());
        scene.addSystem(new UpdateListeners());
        scene.addSystem(new UpdateSources());
        scene.addSystem(new LoopAudio());

        var input = new Input();

        scene.addResource(window);
        scene.addResource(input);
        scene.init();


        while (!window.shouldClose())
            scene.update();

        scene.deInit();

        java.lang.System.exit(0); // Kills worker threads
    }
}

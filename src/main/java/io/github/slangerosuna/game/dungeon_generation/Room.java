package io.github.slangerosuna.game.dungeon_generation;

import io.github.slangerosuna.engine.core.ecs.Scene;
import io.github.slangerosuna.engine.physics.Collider;
import io.github.slangerosuna.engine.render.Material;
import io.github.slangerosuna.engine.render.Transform;
import io.github.slangerosuna.engine.utils.ObjLoader;
import io.github.slangerosuna.engine.core.ecs.Entity;

public abstract class Room {
    private Scene scene;
    private String modelPath;
    private String texturePath;
    public Transform transform;
    public Collider generationCollider;
    public Door[] doors;
    public Entity[] colliders;
    public Entity roomEntity;

    public void kill() {
        transform.kill();
        for (Entity collider : colliders) {
            collider.kill();
        }
    }

    public Room(Scene scene, String modelPath, String texturePath, Transform transform, Collider collider, Entity[] colliders, Door... doors) {
        this.scene = scene;
        this.modelPath = modelPath;
        this.texturePath = texturePath;
        this.transform = transform;
        this.colliders = colliders;
        this.doors = doors;
        adoptDoors();
    }

    private void adoptDoors() {
        for (Door door : doors) { door.setParent(this); }
    }

    public void create() {
        roomEntity = new Entity(scene, transform, ObjLoader.loadObj(modelPath), new Material(texturePath));
    }
}
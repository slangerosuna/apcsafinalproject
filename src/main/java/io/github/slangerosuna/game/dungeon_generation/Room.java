package io.github.slangerosuna.game.dungeon_generation;

import io.github.slangerosuna.engine.core.ecs.Scene;
import io.github.slangerosuna.engine.math.vector.Vector3;
import io.github.slangerosuna.engine.physics.Collider;
import io.github.slangerosuna.engine.render.Material;
import io.github.slangerosuna.engine.render.Transform;
import io.github.slangerosuna.engine.utils.ObjLoader;

import java.util.ArrayList;

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

    public Room(Scene scene, String modelPath, String texturePath, Transform transform, Collider collider, Entity[] colliders, Door... doors) {
        this.scene = scene;
        this.modelPath = modelPath;
        this.texturePath = texturePath;
        this.transform = transform;
        this.generationCollider = collider;
        this.colliders = colliders;
        this.doors = doors;
        adoptDoors();
    }

    public ArrayList<Door> getUnconnectedDoors() {
        ArrayList<Door> unconnectedDoors = new ArrayList<Door>();
        for (Door door : doors) if (!door.isConnected()) unconnectedDoors.add(door);

        return unconnectedDoors;
    }

    private void adoptDoors() {
        for (Door door : doors) door.setParent(this);
    }

    public void create() {
        transform.scale = new Vector3(0.5f, 0.5f, 0.5f);
        roomEntity = new Entity(scene, transform, ObjLoader.loadObj(modelPath), new Material(texturePath));
    }
}
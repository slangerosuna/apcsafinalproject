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
    private RoomPrefab prefab;
    private String modelPath;
    private String texturePath;
    public Transform transform;
    public Collider generationCollider;
    public Door[] doors;
    public Entity[] colliders;
    public Entity roomEntity;

    public Room(Scene scene, RoomPrefab prefab, String modelPath, String texturePath, Transform transform, Collider collider, Door... doors) {
        this.scene = scene;
        this.prefab = prefab;
        this.modelPath = modelPath;
        this.texturePath = texturePath;
        this.transform = transform;
        this.generationCollider = collider;
        this.doors = doors;
        adoptDoors();
    }

    public RoomPrefab getPrefab() { return prefab; }

    public void kill() {
        if (colliders != null) {
            for (Entity collider : colliders) {
                collider.kill();
            }
        }
        if (roomEntity != null) roomEntity.kill();
    }

    public ArrayList<Door> getUnconnectedDoors() {
        ArrayList<Door> unconnectedDoors = new ArrayList<Door>();
        for (Door door : doors) if (door.getConnectedRoom() == null) unconnectedDoors.add(door);

        return unconnectedDoors;
    }

    private void adoptDoors() {
        for (Door door : doors) door.setParent(this);
    }

    public void create() {
        transform.scale = new Vector3(.5f, .5f, .5f);
        colliders = prefab.genColliders(scene, transform.position);
        roomEntity = new Entity(scene, transform, ObjLoader.loadObj(modelPath), new Material(texturePath, 10.0f));
        new Entity(scene, new Transform(transform.position.sub(new Vector3(0, 4.95f, 0)), Vector3.zero(), new Vector3(1, 1, 1)), ObjLoader.loadObj("/io/github/slangerosuna/resources/models/quad.obj"), new Material("/io/github/slangerosuna/resources/textures/floor.png", 1.0f));
    }
}
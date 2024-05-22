package io.github.slangerosuna.game.dungeon_generation;

import io.github.slangerosuna.engine.core.ecs.Scene;
import io.github.slangerosuna.engine.physics.Collider;
import io.github.slangerosuna.engine.render.Transform;

public abstract class Room {
    private String modelPath;
    private String texturePath;
    public Transform transform;
    public Collider[] colliders;
    public Door[] doors;

    public void kill() {
        transform.kill();
        for (Collider collider : colliders) {
            collider.kill();
        }
    }

    public Room(String modelPath, String texturePath, Transform transform, Collider[] colliders, Door... doors) {
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

    public abstract void create(Scene scene);
}
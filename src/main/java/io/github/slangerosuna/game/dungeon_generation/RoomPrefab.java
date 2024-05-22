package io.github.slangerosuna.game.dungeon_generation;

import io.github.slangerosuna.engine.math.vector.Vector3;
import io.github.slangerosuna.engine.render.Transform;
import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Scene;
import io.github.slangerosuna.engine.physics.Collider;


public abstract class RoomPrefab {
    public String modelPath;
    public String texturePath;
    public Vector3[][] colliderPositions;
    public Vector3[] doorPositions;
    private int numDoors;

    public RoomPrefab(String modelPath, String texturePath, Vector3[][] colliderPositions, Vector3... doorPositions) {
        this.modelPath = modelPath;
        this.texturePath = texturePath;
        this.colliderPositions = colliderPositions;
        this.doorPositions = doorPositions;
        this.numDoors = doorPositions.length;
    }

    public int getNumDoors() {return numDoors;}

    public Entity[] genColliders(Scene scene, Vector3 roomPosition) {
        Entity[] colliderEntities = new Entity[colliderPositions.length];
        Vector3 cornerA;
        Vector3 cornerB;
        Vector3 dimensions;
        Vector3 center;
        Transform transform;
        Collider collider;
        for (int i = 0; i < colliderPositions.length; i++) {
            cornerA = colliderPositions[i][0];
            cornerB = colliderPositions[i][1];
            dimensions = cornerB.sub(cornerA);
            center = cornerA.add(dimensions.divide(2));
            transform = new Transform(center, Vector3.zero(), new Vector3(1, 1, 1));
            collider = new Collider(dimensions.x, dimensions.y, dimensions.z, transform);
            colliderEntities[i] = new Entity(scene, transform, collider);
        }
        return colliderEntities;
    }
    
    public Door[] genDoors(Vector3 roomPosition) {
        Door[] doors = new Door[numDoors];
        for (int i = 0; i < numDoors; i++) {
            Vector3 doorPos = roomPosition.add(doorPositions[i]);
            Transform doorTransform = new Transform(doorPos, new Vector3(0, 0, 0), new Vector3(1, 1, 1));
            doors[i] = new Door(doorTransform);
        }
        return doors;
    }

    public abstract Room genRoomFromDoor(Scene scene, Door otherDoor, int connectedDoorIndex);
}
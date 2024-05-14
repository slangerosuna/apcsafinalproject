package io.github.slangerosuna.game.dungeon_generation;

import io.github.slangerosuna.engine.math.vector.Vector3;
import io.github.slangerosuno.engine.render.Transform;

public abstract class RoomPrefab {
    public Vector3[] doorPositions;
    private int numDoors;

    public RoomPrefab(Vector3... doorPositions) {
        this.doorPositions = doorPositions;
        this.numDoors = doorPositions.length;
    }

    public int getNumDoors() {return numDoors;}
    
    public Door[] genDoors(Vector3 roomPosition) {
        Door[] doors = new Door[numDoors];
        for (int i = 0; i < numDoors; i++) {
            Vector3 doorPos = roomPosition.add(doorPositions[i]);
            Transform doorTransform = new Transform(doorPos, new Vector3(0, 0, 0), new Vector3(1, 1, 1));
            doors[i] = new Door(doorTransform);
        }
        return doors;
    }

    public abstract Room genRoomFromDoor(Door otherDoor, int connectedDoorIndex);
}
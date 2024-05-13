package io.github.slangerosuna.game.dungeon_generation;

import io.github.slangerosuna.engine.math.vector.Vector3;

public abstract class RoomPrefab {
    public Vector3[] doorPositions;
    private int numDoors;
    public RoomPrefab(Vector3... doorPositions) {
        this.doorPositions = doorPositions;
        this.numDoors = doorPositions.length;
    }
    public int getNumDoors() {return numDoors;}
    public abstract Room genRoomFromDoor(Door otherDoor, int connectedDoorIndex);
}
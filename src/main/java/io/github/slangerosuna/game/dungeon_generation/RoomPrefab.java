
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
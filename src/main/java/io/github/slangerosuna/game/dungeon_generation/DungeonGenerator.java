import java.util.ArrayList;

public class DungeonGenerator {
    Scene scene;
    RoomPrefab[] rooms;
    RoomPrefab startRoom;
    ArrayList<Room> generatedRooms;

    public DungeonGenerator(Scene scene, RoomPrefab startRoom, RoomPrefab... rooms) {
        this.scene = scene;
        this.startRoom = startRoom;
        this.rooms = rooms;
        this.generatedRooms = new ArrayList<Room>();
    }

    public void startDungeon() {
        generatedRooms.add(startRoom.gen(-1));
    }

    public Door[] getUnconnectedDoors() {
        ArrayList<Door> unconnectedDoors = new ArrayList<Door>();
        for (Room room : generatedRooms) {
            for (Door door : room) {
                if (!door.isConnected()) {unconnectedDoors.add(door);}
            }
        }
        return unconnectedDoors.toArray();
    }

    public boolean doesRoomIntersect(Room room) {
        for (Room existingRoom : generatedRooms) {
            if (room.collider.intersects(existingRoom.collider)) {
                return true;
            }
        }
        return false;
    }

    public boolean genRoomAtDoor(RoomPrefab prefab, Door door) {
        Room room;
        for (int i = 0; i < prefab.getNumDoors(); i++) {
            room = prefab.genRoomFromDoor(door, i);
            if (!roomIntersects(room)) {return true;}
            room.kill();
        }
        return false;
    }

    public int genRandRoomAtDoor(Door door) {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < rooms.length; i++) {indices.add(i);}

        RoomPrefab prefab;
        int prefabIndex;
        boolean couldGenerate;
        Room room;
        while (room == null && indices.size() > 0) {
            prefabIndex = indices.remove((int) Math.random() * indices.size());
            prefab = rooms.get(prefabIndex);
            couldGenerate = genRoomAtDoor(door, prefab);
            if (couldGenerate) {
                room = door.getConnectedRoom();
            }
        }
        if (room == null) { return -1; }
        else { return prefabIndex; }
    }
}
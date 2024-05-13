package io.github.slangerosuna.game.dungeon_generation;

import java.util.ArrayList;

import io.github.slangerosuna.engine.core.ecs.Scene;

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
        generatedRooms.add(startRoom.genRoomFromDoor(null, -1));
    }

    public Door[] getUnconnectedDoors() {
        ArrayList<Door> unconnectedDoors = new ArrayList<Door>();
        for (Room room : generatedRooms) {
            for (Door door : room.doors) {
                if (!door.isConnected()) {unconnectedDoors.add(door);}
            }
        }
        return unconnectedDoors.toArray(Door[]::new);
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
            if (!doesRoomIntersect(room)) {return true;}
            room.kill();
        }
        return false;
    }

    public int genRandRoomAtDoor(Door door) {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < rooms.length; i++) {indices.add(i);}

        RoomPrefab prefab;
        int prefabIndex = -1;
        boolean couldGenerate;
        Room room = null;
        while (room == null && indices.size() > 0) {
            prefabIndex = indices.remove((int) Math.random() * indices.size());
            prefab = rooms[prefabIndex];
            couldGenerate = genRoomAtDoor(prefab, door);
            if (couldGenerate) {
                room = door.getConnectedRoom();
            }
        }
        return prefabIndex;
    }
}
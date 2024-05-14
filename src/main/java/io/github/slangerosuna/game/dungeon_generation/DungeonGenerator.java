package io.github.slangerosuna.game.dungeon_generation;

import java.util.ArrayList;

import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Scene;
import io.github.slangerosuna.engine.math.vector.Vector3;
import io.github.slangerosuna.engine.physics.Collider;
import io.github.slangerosuna.engine.render.Transform;
import io.github.slangerosuna.engine.utils.ObjLoader;

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
            if (!doesRoomIntersect(room)) { return true; }
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
        if (room == null) prefabIndex = -1;
        return prefabIndex;
    }

    public static RoomPrefab[] defaultRoomPrefabs() {
        ArrayList<RoomPrefab> prefabs = new ArrayList<RoomPrefab>();

        float width1 = 10f;
        float height1 = 10f;
        float depth1 = 10f;
        Vector3[] doorPositions1 = new Vector3[2];
        doorPositions1[0] = new Vector3(0, -height1/2+1, -depth1/2);
        doorPositions1[1] = new Vector3(0, -height1/2+1, depth1/2);
        RoomPrefab cubeRoom = new RoomPrefab(doorPositions1) {
            public Room genRoomFromDoor(Door otherDoor, int connectedDoorIndex) {
                if (otherDoor == null) {
                    Transform transform = new Transform(new Vector3(0, -height1/2+1, -depth1/2), Vector3.zero(), new Vector3(0, 0, 0));
                    otherDoor = new Door(transform);
                }
                if (connectedDoorIndex == -1) connectedDoorIndex = 1;
                Vector3 position = otherDoor.getTransform().position.sub(doorPositions1[connectedDoorIndex]);
                Transform transform = new Transform(position, new Vector3(0, 0, 0), new Vector3(1, 1, 1));
                Collider collider = new Collider(width1, height1, depth1, transform);
                Door[] doors = genDoors(position);
                doors[connectedDoorIndex].setConnectedRoom(otherDoor.getParent());

                return new Room(transform, collider, doors) {
                    @Override
                    public void create(Scene scene) {
                        new Entity(scene, transform, ObjLoader.loadObj("/io/github/slangerosuna/resources/models/room1.obj"));
                    }
                };
            }
        };
        prefabs.add(cubeRoom);

        return prefabs.toArray(RoomPrefab[]::new);
    }
}
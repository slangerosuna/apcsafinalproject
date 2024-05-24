package io.github.slangerosuna.game.dungeon_generation;

import java.util.ArrayList;

import io.github.slangerosuna.engine.core.ecs.Scene;
import io.github.slangerosuna.engine.math.vector.Vector3;
import io.github.slangerosuna.engine.physics.Collider;
import io.github.slangerosuna.engine.render.Transform;

public class DungeonGenerator {
    Scene scene;
    RoomPrefab[] rooms;
    RoomPrefab startRoom;
    ArrayList<Room> generatedRooms;
    ArrayList<Door> unconnectedDoors;

    public DungeonGenerator(Scene scene, RoomPrefab startRoom, RoomPrefab... rooms) {
        this.scene = scene;
        this.startRoom = startRoom;
        this.rooms = rooms;
        this.generatedRooms = new ArrayList<Room>();
        this.unconnectedDoors = new ArrayList<Door>();
    }

    public ArrayList<Room> getGeneratedRooms() { return generatedRooms; }

    public void generateDungeon(Scene scene, RoomPrefab[] prefabs, int minRooms, int maxAttempts, int maxSidePathLength) {
        this.rooms = prefabs;
        this.startRoom = rooms[0];
        startDungeon();
        Room[] pathGenerated = new Room[0];
        int attempts = 0;
        while (pathGenerated.length < minRooms && attempts < maxAttempts) {
            pathGenerated = genRoomSequenceFromRoom(generatedRooms.get(0), minRooms);
        }
        if (pathGenerated.length < minRooms && attempts >= maxAttempts) {System.out.println("Unable to generate path to exit of sufficient length");}
        genSidePaths(pathGenerated, maxSidePathLength);
    }

    public void addRoom(Room room) {
        generatedRooms.add(room);
        for (Door door : room.doors) {
            if (!door.isConnected()) unconnectedDoors.add(door);
        }
    }

    public void startDungeon() {
        addRoom(startRoom.genRoomFromDoor(scene, null, -1));
    }

    public boolean doesRoomIntersect(Room room) {
        for (Room existingRoom : generatedRooms) {
            if (room.generationCollider.intersects(existingRoom.generationCollider)) {
                return true;
            }
        }
        return false;
    }

    public boolean genRoomAtDoor(RoomPrefab prefab, Door door) {
        Room room;
        for (int i = 0; i < prefab.getNumDoors(); i++) {
            room = prefab.genRoomFromDoor(scene, door, i);
            if (!doesRoomIntersect(room)) { 
                addRoom(room);
                return true; 
            }
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

    public boolean genRandRoomAtRoom(Room room) {
        ArrayList<Door> availableDoors = new ArrayList<Door>();
        for (Door door : room.doors) {
            if (!door.isConnected()) availableDoors.add(door);
        }
        Door curDoor;
        boolean couldGenerate = false;
        while (!couldGenerate && availableDoors.size() > 0) {
            curDoor = availableDoors.remove((int)(Math.random() * availableDoors.size()));
            couldGenerate = (genRandRoomAtDoor(curDoor) != -1);
        }
        return couldGenerate;
    }

    public Room[] genRoomSequenceFromRoom(Room startRoom, int numRooms) {
        ArrayList<Room> rooms = new ArrayList<Room>();
        int curNumRooms = 0;
        Room curRoom = startRoom;
        boolean couldGenerate = true;
        while (curNumRooms < numRooms && couldGenerate) {
            couldGenerate = genRandRoomAtRoom(curRoom);
            if (couldGenerate) {
                curNumRooms++;
                rooms.add(generatedRooms.get(generatedRooms.size()-1));
            }
        }
        return (Room[]) rooms.toArray();
    }

    public void genSidePaths(Room[] mainPath, int maxSidePathLength) {
        ArrayList<ArrayList<Room>> roomTree = new ArrayList<ArrayList<Room>>(maxSidePathLength+1);
        for (Room room : mainPath) {roomTree.get(0).add(room);}

        Room curRoom;
        Room[] pathGenerated;
        for (int i = 0; i < maxSidePathLength; i++) {
            while (roomTree.get(i).size() > 0) {
                curRoom = roomTree.get(i).remove((int)(Math.random() * roomTree.get(i).size()));
                for (int j = 0; j < curRoom.getUnconnectedDoors().length; j++) {
                    pathGenerated = genRoomSequenceFromRoom(curRoom, maxSidePathLength-j);
                    for (int k = 0; k < pathGenerated.length; k++) {
                        roomTree.get(i+k+1).add(pathGenerated[k]);
                    }
                }
            }
        }
    }

    public static RoomPrefab[] defaultRoomPrefabs() {
        ArrayList<RoomPrefab> prefabs = new ArrayList<RoomPrefab>();

        String modelPath1 = "/io/github/slangerosuna/resources/models/room1.obj";
        String texturePath1 = "/io/github/slangerosuna/resources/textures/wall.png";
        Vector3 dimensions1 = new Vector3(10, 10, 10);
        float colliderThickness = 0.5f;
        Vector3[][] colliderPositions = new Vector3[6][2];
        colliderPositions[0][0] = new Vector3(-dimensions1.x/2, -dimensions1.y/2-colliderThickness/2, -dimensions1.z/2);
        colliderPositions[0][1] = new Vector3(dimensions1.x/2, -dimensions1.y/2+colliderThickness/2, dimensions1.z/2);
        colliderPositions[1][0] = new Vector3(-dimensions1.x/2, -dimensions1.y/2, dimensions1.z/2-colliderThickness/2);
        colliderPositions[1][1] = new Vector3(dimensions1.x/2, dimensions1.y/2, dimensions1.z/2+colliderThickness/2);
        colliderPositions[2][0] = new Vector3(-dimensions1.x/2, dimensions1.y/2-colliderThickness/2, -dimensions1.z/2);
        colliderPositions[2][1] = new Vector3(dimensions1.x/2, dimensions1.y/2+colliderThickness/2, dimensions1.z/2);
        colliderPositions[3][0] = new Vector3(-dimensions1.x/2, -dimensions1.y/2, -dimensions1.z/2-colliderThickness/2);
        colliderPositions[3][1] = new Vector3(dimensions1.x/2, dimensions1.y/2, -dimensions1.z/2+colliderThickness/2);
        colliderPositions[4][0] = new Vector3(-dimensions1.x/2-colliderThickness/2, -dimensions1.y/2, -dimensions1.z/2);
        colliderPositions[4][1] = new Vector3(-dimensions1.x/2+colliderThickness/2, dimensions1.y/2, dimensions1.z/2);
        colliderPositions[5][0] = new Vector3(dimensions1.x/2-colliderThickness/2, -dimensions1.y/2, -dimensions1.z/2);
        colliderPositions[5][1] = new Vector3(dimensions1.x/2+colliderThickness/2, dimensions1.y/2, dimensions1.z/2);
        Vector3[] doorPositions1 = new Vector3[2];
        doorPositions1[0] = new Vector3(0, -dimensions1.y/2+1, -dimensions1.z/2);
        doorPositions1[1] = new Vector3(0, -dimensions1.y/2+1, dimensions1.z/2);
        RoomPrefab cubeRoom = new RoomPrefab(modelPath1, texturePath1, colliderPositions, doorPositions1) {
            public Room genRoomFromDoor(Scene scene, Door otherDoor, int connectedDoorIndex) {
                if (otherDoor == null) {
                    Transform transform = new Transform(new Vector3(0, 1, -dimensions1.z/2), Vector3.zero(), new Vector3(1, 1, 1));
                    otherDoor = new Door(transform);
                }
                if (connectedDoorIndex == -1) connectedDoorIndex = 1;
                Vector3 position = otherDoor.getTransform().position.sub(doorPositions1[connectedDoorIndex]);
                Transform transform = new Transform(position, new Vector3(0, 0, 0), new Vector3(1, 1, 1));
                Collider collider = new Collider(dimensions1.x, dimensions1.y, dimensions1.z, transform);
                Door[] doors = genDoors(position);
                doors[connectedDoorIndex].setConnectedRoom(otherDoor.getParent());

                Room room = new Room(scene, modelPath, texturePath, transform, collider, genColliders(scene, position), doors) {};
                room.create();
                return room;
            }
        };
        prefabs.add(cubeRoom);

        return prefabs.toArray(RoomPrefab[]::new);
    }
}
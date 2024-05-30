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

    public DungeonGenerator(Scene scene, RoomPrefab startRoom, RoomPrefab... rooms) {
        this.scene = scene;
        this.startRoom = startRoom;
        this.rooms = rooms;
        this.generatedRooms = new ArrayList<Room>();
    }

    public ArrayList<Room> getGeneratedRooms() { return generatedRooms; }
    private ArrayList<Door> getUnconnectedDoors() {
        ArrayList<Door> unconnectedDoors = new ArrayList<Door>();
        for (Room room : generatedRooms) {
            for (Door door : room.getUnconnectedDoors()) {
                unconnectedDoors.add(door);
            }
        }
        return unconnectedDoors;
    }

    public void generateDungeon(Scene scene, RoomPrefab[] prefabs, int minRooms, int maxAttempts, int maxSidePathLength) {
        this.rooms = prefabs;
        this.startRoom = rooms[0];
        startDungeon();
        Room[] pathGenerated = new Room[0];
        int attempts = 0;
        while (pathGenerated.length < minRooms && attempts < maxAttempts)
            pathGenerated = genRoomSequenceFromRoom(generatedRooms.get(0), minRooms);
        
        if (pathGenerated.length < minRooms && attempts >= maxAttempts) {System.out.println("Unable to generate path to exit of sufficient length");}
        genSidePaths(pathGenerated, maxSidePathLength);
    }

    public void create() {
        for (Room room : generatedRooms) room.create();
    }

    public void clear() {
        generatedRooms.clear();
    }

    public void clear(int startIndex, int endIndex) {
        if (startIndex > generatedRooms.size()-1) return;
        if (endIndex > generatedRooms.size()) endIndex = generatedRooms.size();
        for (int i = 0; i < endIndex-startIndex; i++) {
            generatedRooms.remove(startIndex);
        }
    }
    public void clear(int startIndex) {
        clear(startIndex, generatedRooms.size());
    }

    public void addRoom(Room room) {
        generatedRooms.add(room);
    }

    public void startDungeon(Vector3 startPos) {
        addRoom(startRoom.genRoomAtCoord(scene, startPos));
    }

    public void startDungeon() {
        startDungeon(Vector3.zero());
    }

    public boolean doesRoomIntersect(Room room) {
        for (Room existingRoom : generatedRooms)
            if (room.generationCollider.intersects(existingRoom.generationCollider))
                return true;

        return false;
    }

    public boolean genRoomAtDoor(RoomPrefab prefab, Door door) {
        Room room;
        for (int i = 0; i < prefab.getNumDoors(); i++) {
            room = prefab.genRoomFromDoor(scene, door, i);
            if (!doesRoomIntersect(room)) { 
                addRoom(room);
                return true; 
            } else {
                door.removeConnectedRoom();
                room.kill();
            }
        }
        return false;
    }

    public Room genRandRoomAtDoor(Door door) {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < rooms.length; i++) indices.add(i);

        RoomPrefab prefab;
        int prefabIndex = -1;
        boolean couldGenerate;
        Room room = null;
        while (room == null && indices.size() > 0) {
            prefabIndex = indices.remove((int) Math.random() * indices.size());
            prefab = rooms[prefabIndex];
            couldGenerate = genRoomAtDoor(prefab, door);
            if (couldGenerate)
                room = door.getConnectedRoom();
                break;
        }
        return room;
    }

    public boolean genRandRoomAtRoom(Room room) {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < room.doors.length; i++) indices.add(i);

        Door curDoor;
        int curIndex;
        Room generatedDoor;
        boolean couldGenerate = false;
        while (indices.size() > 0) {
            curIndex = (int) (Math.random() * indices.size());
            curDoor = room.doors[indices.remove(curIndex)];
            if (curDoor.getConnectedRoom() == null) {
                generatedDoor = genRandRoomAtDoor(curDoor);
                if (generatedDoor != null) return true;
            }
        }
        return false;
    }

    public Room[] genRoomSequenceFromRoom(Room startRoom, int numRooms) {
        ArrayList<Room> rooms = new ArrayList<Room>();
        int curNumRooms = 0;
        Room curRoom = startRoom;
        boolean couldGenerate = true;
        int attemptCounter = 0;
        while (curNumRooms < numRooms && attemptCounter < curRoom.doors.length) {
            couldGenerate = genRandRoomAtRoom(curRoom);
            attemptCounter++;
            if (couldGenerate) {
                attemptCounter = 0;
                curNumRooms++;
                curRoom = generatedRooms.get(generatedRooms.size()-1);
                rooms.add(curRoom);
            }
        }
        return rooms.toArray(Room[]::new);
    }

    public void genSidePaths(Room[] mainPath, int maxSidePathLength) {
        ArrayList<ArrayList<Room>> roomTree = new ArrayList<ArrayList<Room>>();
        for (int i = 0; i < maxSidePathLength+1; i++) roomTree.add(new ArrayList<Room>());
        for (Room room : mainPath) {
            roomTree.get(0).add(room);
            roomTree.get(0).add(room);
        }

        Room curRoom;
        Room[] pathGenerated;
        int curIndex;
        for (int i = 0; i < roomTree.size(); i++) {
            while (roomTree.get(i).size() > 0) {
                curIndex = (int)(Math.random() * roomTree.get(i).size());
                curRoom = roomTree.get(i).remove(curIndex);
                for (int j = 0; j < curRoom.getUnconnectedDoors().size()-1; j++) {
                    pathGenerated = genRoomSequenceFromRoom(curRoom, maxSidePathLength-i);
                    for (int k = 0; k < pathGenerated.length-1; k++)
                        roomTree.get(i+k+1).add(pathGenerated[k]);
                }
            }
        }
    }

    public static RoomPrefab[] defaultRoomPrefabs() {
        ArrayList<RoomPrefab> prefabs = new ArrayList<RoomPrefab>();


        //Room 1
        String modelPath1 = "/io/github/slangerosuna/resources/models/room1.obj";
        String texturePath1 = "/io/github/slangerosuna/resources/textures/wall.png";
        Vector3 dimensions1 = new Vector3(10, 10, 10);
        float colliderThickness = 0.5f;
        /*Vector3[][] colliderPositions = new Vector3[6][2];
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
        */
        Vector3[][] floorColliderPositions = new Vector3[1][2];
        floorColliderPositions[0][0] = new Vector3(-dimensions1.x/2, -dimensions1.y/2-colliderThickness/2, -dimensions1.z/2);
        floorColliderPositions[0][1] = new Vector3(dimensions1.x/2, -dimensions1.y/2+colliderThickness/2, dimensions1.z/2);

        Vector3[] doorPositions1 = new Vector3[2];
        doorPositions1[0] = new Vector3(-dimensions1.x/2, -dimensions1.y/2+1, 0);
        doorPositions1[1] = new Vector3(dimensions1.x/2, -dimensions1.y/2+1, 0);
        RoomPrefab cubeRoom = new RoomPrefab(modelPath1, texturePath1, floorColliderPositions, doorPositions1) {
            public Room genRoomAtCoord(Scene scene, Vector3 coord) {
                Vector3 position = new Vector3(coord.x, coord.y, coord.z);
                Transform transform = new Transform(position, Vector3.zero(), new Vector3(1, 1, 1));
                Collider collider = new Collider(dimensions1.x, dimensions1.y, dimensions1.z, transform);
                Door[] doors = genDoors(position);

                Room room = new Room(scene, this, modelPath, texturePath, transform, collider, doors) {};
                return room;
            }
            public Room genRoomFromDoor(Scene scene, Door otherDoor, int connectedDoorIndex) {
                if (connectedDoorIndex == -1) connectedDoorIndex = 1;
                Vector3 position = otherDoor.getTransform().position.sub(doorPositions1[connectedDoorIndex]);
                Room room = genRoomAtCoord(scene, position);

                room.doors[connectedDoorIndex].setConnectedRoom(otherDoor.getParent());
                otherDoor.setConnectedRoom(room);
                return room;
            }
        };
        prefabs.add(cubeRoom);


        //Room 2
        String modelPath2 = "/io/github/slangerosuna/resources/models/room2.obj";
        String texturePath2 = "/io/github/slangerosuna/resources/textures/wall.png";
        Vector3[][] floorColliderPositions2 = new Vector3[1][2];
        floorColliderPositions2[0][0] = new Vector3(-dimensions1.x/2f, -dimensions1.y/2-2f, -dimensions1.z/2f);
        floorColliderPositions2[0][1] = new Vector3(dimensions1.x/2f, -dimensions1.y/2f, dimensions1.z/2f);
        Vector3 dimensions2 = new Vector3(10, 10, 10);
        Vector3[] doorPositions2 = new Vector3[4];
        doorPositions2[0] = new Vector3(-dimensions2.x/2, -dimensions2.y/2+dimensions2.y/8*3, 0);
        doorPositions2[1] = new Vector3(dimensions2.x/2, -dimensions2.y/2+dimensions2.y/8*3, 0);
        doorPositions2[2] = new Vector3(0, -dimensions2.y/2+dimensions2.y/8*3, -dimensions2.z/2);
        doorPositions2[3] = new Vector3(0, -dimensions2.y/2+dimensions2.y/8*3, dimensions2.z/2);
        RoomPrefab fourDoorCubeRoom = new RoomPrefab(modelPath2, texturePath2, floorColliderPositions2, doorPositions2) {
            public Room genRoomAtCoord(Scene scene, Vector3 coord) {
                Vector3 position = new Vector3(coord.x, coord.y, coord.z);
                Transform transform = new Transform(position, Vector3.zero(), new Vector3(1, 1, 1));
                Collider collider = new Collider(dimensions2.x, dimensions2.y, dimensions2.z, transform);
                Door[] doors = genDoors(position);

                Room room = new Room(scene, this, modelPath, texturePath, transform, collider, doors) {};
                return room;
            }
            public Room genRoomFromDoor(Scene scene, Door otherDoor, int connectedDoorIndex) {
                if (connectedDoorIndex == -1) connectedDoorIndex = 1;
                Vector3 position = otherDoor.getTransform().position.sub(doorPositions2[connectedDoorIndex]);
                Room room = genRoomAtCoord(scene, position);

                room.doors[connectedDoorIndex].setConnectedRoom(otherDoor.getParent());
                otherDoor.setConnectedRoom(room);
                return room;
            }
        };
        prefabs.add(fourDoorCubeRoom);

        return prefabs.toArray(RoomPrefab[]::new);
    }
}
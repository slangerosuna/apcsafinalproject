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

    public static Room[] generateDungeon(Scene scene, RoomPrefab startRoom, RoomPrefab[] prefabs, Vector3 startPos, int pathLen, int maxSidePathLen, float sparseness, float recursion) {
        DungeonGenerator generator = new DungeonGenerator(scene, startRoom, prefabs);
        generator.startDungeon(startPos);
        while (generator.generatedRooms.size() < pathLen) {
            generator.clear(1);
            generator.genRoomSequenceFromRoom(generator.generatedRooms.get(0), pathLen);
        }
        generator.genSidePaths(generator.generatedRooms, maxSidePathLen, sparseness, recursion);
        generator.create();
        return generator.generatedRooms.toArray(Room[]::new);
    }

    public ArrayList<Room> getGeneratedRooms() { return generatedRooms; }

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

    public Room genRoomAtDoor(RoomPrefab prefab, Door door) {
        Room room;
        for (int i = 0; i < prefab.getNumDoors(); i++) {
            room = prefab.genRoomFromDoor(scene, door, i);
            if (!doesRoomIntersect(room)) { 
                addRoom(room);
                return room; 
            } else {
                door.removeConnectedRoom();
                room.kill();
            }
        }
        return null;
    }

    public Room genRandRoomAtDoor(Door door) {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < rooms.length; i++) indices.add(i);

        RoomPrefab prefab;
        int prefabIndex = -1;
        Room room = null;
        int temp;
        while (room == null && indices.size() > 0) {
            temp = (int) (Math.random() * indices.size());
            prefabIndex = indices.remove(temp);
            prefab = rooms[prefabIndex];
            room = genRoomAtDoor(prefab, door);
            if (room != null) break;
        }
        return room;
    }

    public Room genRandRoomAtRoom(Room room) {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < room.doors.length; i++) indices.add(i);

        Door curDoor;
        int curIndex;
        Room generatedDoor;
        while (indices.size() > 0) {
            curIndex = indices.remove((int) (Math.random() * indices.size()));
            curDoor = room.doors[curIndex];
            if (curDoor.getConnectedRoom() == null) {
                generatedDoor = genRandRoomAtDoor(curDoor);
                if (generatedDoor != null) return generatedDoor;
            }
        }
        return null;
    }

    public ArrayList<Room> genStraightRoomSequenceFromRoom(Room startRoom, int doorIndex, int numRooms) {
        ArrayList<Room> rooms = new ArrayList<Room>();
        rooms.add(startRoom);

        Room curRoom = startRoom;
        Door door;
        for (int i = 1; i <= numRooms; i++) {
            door = curRoom.doors[doorIndex];
            curRoom = genRoomAtDoor(startRoom.getPrefab(), door);
            rooms.add(curRoom);
        }
        return rooms;
    }

    public ArrayList<Room> genRoomSequenceFromRoom(Room startRoom, int numRooms) {
        ArrayList<Room> rooms = new ArrayList<Room>();
        rooms.add(startRoom);
        int curNumRooms = 0;
        Room oldRoom;
        Room curRoom = startRoom;
        int attemptCounter = 0;
        while (curNumRooms < numRooms && attemptCounter < curRoom.doors.length) {
            oldRoom = curRoom;
            curRoom = genRandRoomAtRoom(curRoom);
            attemptCounter++;
            if (curRoom != null) {
                attemptCounter = 0;
                curNumRooms++;
                rooms.add(curRoom);
            } else {
                curRoom = oldRoom;
            }
        }
        return rooms;
    }

    public ArrayList<Room> genSporadicListFromSequence(ArrayList<Room> sequence, float sparseness) {
        ArrayList<Room> result = new ArrayList<Room>();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < sequence.size(); i++) indices.add(i);

        int curIndex;
        for(int i = 0; i < (int)(sequence.size() / sparseness); i++) {
            curIndex = indices.remove((int)(Math.random() * indices.size()));
            result.add(sequence.get(curIndex));
            if (indices.size() == 0) {
                for (int j = 0; j < sequence.size(); j++) indices.add(j);
            }
        }
        return result;
    }

    private void genSidePathsHelper(ArrayList<ArrayList<Room>> roomTree, int roomTreeIndex, float sparseness, boolean recurse) {
        ArrayList<Room> rootList = genSporadicListFromSequence(roomTree.get(roomTreeIndex), sparseness);

        Room curRoom;
        for (Room rootRoom : rootList) {
            curRoom = genRandRoomAtRoom(rootRoom);
            if (curRoom != null)
                roomTree.get(roomTreeIndex+1).add(curRoom);
            else
                break;
        }
        if (recurse && roomTreeIndex < roomTree.size()-2) {
            genSidePathsHelper(roomTree, roomTreeIndex+1, sparseness, true);
        }
    }

    //sparseness is how often it will branch from the main path
    //recursion is how often it will branch from a side path
    public void genSidePaths(ArrayList<Room> mainPath, int maxSidePathLength, float sparseness, float recursion) {
        ArrayList<ArrayList<Room>> roomTree = new ArrayList<ArrayList<Room>>();
        for (int i = 0; i < maxSidePathLength+1; i++) roomTree.add(new ArrayList<Room>());

        for (int i = 0; i < mainPath.size(); i++) roomTree.get(0).add(mainPath.get(i));
        genSidePathsHelper(roomTree, 0, sparseness, false);
        if (roomTree.size() > 2) {
            genSidePathsHelper(roomTree, 1, recursion, true);
        }
    }

    public static RoomPrefab[] defaultRoomPrefabs() {
        ArrayList<RoomPrefab> prefabs = new ArrayList<RoomPrefab>();


        //Room 1
        String modelPath1 = "/io/github/slangerosuna/resources/models/room1.obj";
        String texturePath1 = "/io/github/slangerosuna/resources/textures/wall.png";
        Vector3 dimensions1 = new Vector3(10, 10, 10);
        float colliderThickness = 1f;
        Vector3[][] colliderPositions = new Vector3[10][2];
        //floor
        colliderPositions[0][0] = new Vector3(-dimensions1.x/2, -dimensions1.y/2-2, -dimensions1.z/2);
        colliderPositions[0][1] = new Vector3(dimensions1.x/2, -dimensions1.y/2, dimensions1.z/2);
        //front
        colliderPositions[1][0] = new Vector3(-dimensions1.x/2, -dimensions1.y/2, dimensions1.z/2-colliderThickness/2);
        colliderPositions[1][1] = new Vector3(dimensions1.x/2, dimensions1.y/2, dimensions1.z/2+colliderThickness/2);
        //top
        colliderPositions[2][0] = new Vector3(-dimensions1.x/2, dimensions1.y/2-colliderThickness/2, -dimensions1.z/2);
        colliderPositions[2][1] = new Vector3(dimensions1.x/2, dimensions1.y/2+colliderThickness/2, dimensions1.z/2);
        //back
        colliderPositions[3][0] = new Vector3(-dimensions1.x/2, -dimensions1.y/2, -dimensions1.z/2-colliderThickness/2);
        colliderPositions[3][1] = new Vector3(dimensions1.x/2, dimensions1.y/2, -dimensions1.z/2+colliderThickness/2);
        //left
        colliderPositions[4][0] = new Vector3(-dimensions1.x/2-colliderThickness/2, dimensions1.y/2, -dimensions1.z/2);
        colliderPositions[4][1] = new Vector3(-dimensions1.x/2+colliderThickness/2, -dimensions1.y/2, -dimensions1.z/6);
        colliderPositions[5][0] = new Vector3(-dimensions1.x/2-colliderThickness/2, dimensions1.y/2, dimensions1.z/6);
        colliderPositions[5][1] = new Vector3(-dimensions1.x/2+colliderThickness/2, -dimensions1.y/2, dimensions1.z/2);
        colliderPositions[6][0] = new Vector3(-dimensions1.x/2-colliderThickness/2, dimensions1.y/2, -dimensions1.z/2);
        colliderPositions[6][1] = new Vector3(-dimensions1.x/2+colliderThickness/2, 0, dimensions1.z/2);
        //right
        colliderPositions[7][0] = new Vector3(dimensions1.x/2-colliderThickness/2, dimensions1.y/2, -dimensions1.z/2);
        colliderPositions[7][1] = new Vector3(dimensions1.x/2+colliderThickness/2, -dimensions1.y/2, -dimensions1.z/6);
        colliderPositions[8][0] = new Vector3(dimensions1.x/2-colliderThickness/2, dimensions1.y/2, dimensions1.z/6);
        colliderPositions[8][1] = new Vector3(dimensions1.x/2+colliderThickness/2, -dimensions1.y/2, dimensions1.z/2);
        colliderPositions[9][0] = new Vector3(dimensions1.x/2-colliderThickness/2, dimensions1.y/2, -dimensions1.z/2);
        colliderPositions[9][1] = new Vector3(dimensions1.x/2+colliderThickness/2, 0, dimensions1.z/2);

        Vector3[] doorPositions1 = new Vector3[2];
        doorPositions1[0] = new Vector3(-dimensions1.x/2, 0, 0);
        doorPositions1[1] = new Vector3(dimensions1.x/2, 0, 0);
        RoomPrefab cubeRoom = new RoomPrefab(modelPath1, texturePath1, colliderPositions, doorPositions1) {
            public Room genRoomAtCoord(Scene scene, Vector3 coord) {
                Vector3 position = new Vector3(coord.x, coord.y, coord.z);
                Transform transform = new Transform(position, Vector3.zero(), new Vector3(1, 1, 1));
                Collider collider = new Collider(dimensions1.x*0.9f, dimensions1.y*0.9f, dimensions1.z*0.9f, transform);
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

        Vector3[][] colliderPositions2 = new Vector3[3*4+2][2];
        //floor
        colliderPositions2[0][0] = new Vector3(-dimensions1.x/2, -dimensions1.y/2-2, -dimensions1.z/2);
        colliderPositions2[0][1] = new Vector3(dimensions1.x/2, -dimensions1.y/2, dimensions1.z/2);
        //top
        colliderPositions2[1][0] = new Vector3(-dimensions1.x/2, dimensions1.y/2-colliderThickness/2, -dimensions1.z/2);
        colliderPositions2[1][1] = new Vector3(dimensions1.x/2, dimensions1.y/2+colliderThickness/2, dimensions1.z/2);
        //front
        colliderPositions2[2][0] = new Vector3(-dimensions1.x/2, dimensions1.y/2, dimensions1.z/2-colliderThickness/2);
        colliderPositions2[2][1] = new Vector3(-dimensions1.x/8, -dimensions1.y/2, dimensions1.z/2+colliderThickness/2);
        colliderPositions2[3][0] = new Vector3(dimensions1.x/8, dimensions1.y/2, dimensions1.z/2-colliderThickness/2);
        colliderPositions2[3][1] = new Vector3(dimensions1.x/2, -dimensions1.y/2, dimensions1.z/2+colliderThickness/2);
        colliderPositions2[4][0] = new Vector3(-dimensions1.x/2, dimensions1.y/2, dimensions1.z/2-colliderThickness/2);
        colliderPositions2[4][1] = new Vector3(dimensions1.x/2, 0, dimensions1.z/2+colliderThickness/2);
        //back
        colliderPositions2[5][0] = new Vector3(-dimensions1.x/2, dimensions1.y/2, -dimensions1.z/2-colliderThickness/2);
        colliderPositions2[5][1] = new Vector3(-dimensions1.x/8, -dimensions1.y/2, -dimensions1.z/2+colliderThickness/2);
        colliderPositions2[6][0] = new Vector3(dimensions1.x/8, dimensions1.y/2, -dimensions1.z/2-colliderThickness/2);
        colliderPositions2[6][1] = new Vector3(dimensions1.x/2, -dimensions1.y/2, -dimensions1.z/2+colliderThickness/2);
        colliderPositions2[7][0] = new Vector3(-dimensions1.x/2, dimensions1.y/2, -dimensions1.z/2-colliderThickness/2);
        colliderPositions2[7][1] = new Vector3(dimensions1.x/2, 0, -dimensions1.z/2+colliderThickness/2);
        //left
        colliderPositions2[8][0] = new Vector3(-dimensions1.x/2-colliderThickness/2, dimensions1.y/2, -dimensions1.z/2);
        colliderPositions2[8][1] = new Vector3(-dimensions1.x/2+colliderThickness/2, -dimensions1.y/2, -dimensions1.z/6);
        colliderPositions2[9][0] = new Vector3(-dimensions1.x/2-colliderThickness/2, dimensions1.y/2, dimensions1.z/6);
        colliderPositions2[9][1] = new Vector3(-dimensions1.x/2+colliderThickness/2, -dimensions1.y/2, dimensions1.z/2);
        colliderPositions2[10][0] = new Vector3(-dimensions1.x/2-colliderThickness/2, dimensions1.y/2, -dimensions1.z/2);
        colliderPositions2[10][1] = new Vector3(-dimensions1.x/2+colliderThickness/2, 0, dimensions1.z/2);
        //right
        colliderPositions2[11][0] = new Vector3(dimensions1.x/2-colliderThickness/2, dimensions1.y/2, -dimensions1.z/2);
        colliderPositions2[11][1] = new Vector3(dimensions1.x/2+colliderThickness/2, -dimensions1.y/2, -dimensions1.z/6);
        colliderPositions2[12][0] = new Vector3(dimensions1.x/2-colliderThickness/2, dimensions1.y/2, dimensions1.z/6);
        colliderPositions2[12][1] = new Vector3(dimensions1.x/2+colliderThickness/2, -dimensions1.y/2, dimensions1.z/2);
        colliderPositions2[13][0] = new Vector3(dimensions1.x/2-colliderThickness/2, dimensions1.y/2, -dimensions1.z/2);
        colliderPositions2[13][1] = new Vector3(dimensions1.x/2+colliderThickness/2, 0, dimensions1.z/2);


        Vector3[][] floorColliderPositions2 = new Vector3[1][2];
        floorColliderPositions2[0][0] = new Vector3(-dimensions1.x/2f, -dimensions1.y/2-2f, -dimensions1.z/2f);
        floorColliderPositions2[0][1] = new Vector3(dimensions1.x/2f, -dimensions1.y/2f, dimensions1.z/2f);
        Vector3 dimensions2 = new Vector3(10, 10, 10);
        Vector3[] doorPositions2 = new Vector3[4];
        doorPositions2[0] = new Vector3(-dimensions2.x/2, 0, 0);
        doorPositions2[1] = new Vector3(dimensions2.x/2, 0, 0);
        doorPositions2[2] = new Vector3(0, 0, -dimensions2.z/2);
        doorPositions2[3] = new Vector3(0, 0, dimensions2.z/2);
        RoomPrefab fourDoorCubeRoom = new RoomPrefab(modelPath2, texturePath2, colliderPositions2, doorPositions2) {
            public Room genRoomAtCoord(Scene scene, Vector3 coord) {
                Vector3 position = new Vector3(coord.x, coord.y, coord.z);
                Transform transform = new Transform(position, Vector3.zero(), new Vector3(1, 1, 1));
                Collider collider = new Collider(dimensions2.x*0.9f, dimensions2.y*0.9f, dimensions2.z*0.9f, transform);
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
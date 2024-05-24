package io.github.slangerosuna.game.dungeon_generation;

import io.github.slangerosuna.engine.render.Transform;

public class Door {
    private Transform transform;
    private Room parent;
    private boolean connected;
    private Room connectedRoom;

    public Door(Transform transform) {
        this.transform = transform;
        this.connected = false;
    }

    public Door(Transform transform, Room parent) {
        this.transform = transform;
        this.parent = parent;
        this.connected = false;
    }  

    public Door(Transform transform, Room parent, Room connectedRoom) {
        this.transform = transform;
        this.parent = parent;
        this.connected = true;
        this.connectedRoom = connectedRoom;
    }

    public void setParent(Room room) {
        this.parent = room;
    }

    public void setConnectedRoom(Room room) {
        if (connected)
            throw new IllegalArgumentException("Door is already connected");
        connected = true;
        connectedRoom = room;
    }

    public Transform getTransform() {return transform;}
    public Room getParent() {return parent;}
    public boolean isConnected() {return connected;}
    public Room getConnectedRoom() {return connectedRoom;}
}
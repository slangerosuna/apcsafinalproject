package io.github.slangerosuna.game.dungeon_generation;

import io.github.slangerosuna.engine.render.Transform;

public class Door {
    private Transform transform;
    private Room parent;
    private boolean connected;
    private Room connectedRoom;

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

    //prec: Door is connected equals false
    public void setConnectedRoom(Room room) {
        if (connected) {return;}
        connected = true;
        connectedRoom = room;
    }

    public Transform getTransform() {return transform;}
    public Room getParent() {return parent;}
    public boolean isConnected() {return connected;}
    public Room getConnectedRoom() {return connectedRoom;}
}
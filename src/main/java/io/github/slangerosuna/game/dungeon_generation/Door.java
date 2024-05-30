package io.github.slangerosuna.game.dungeon_generation;

import io.github.slangerosuna.engine.render.Transform;

public class Door {
    private Transform transform;
    private Room parent;
    private Room connectedRoom;

    public Door(Transform transform) {
        this.transform = transform;
    }

    public Door(Transform transform, Room parent) {
        this.transform = transform;
        this.parent = parent;
    }  

    public Door(Transform transform, Room parent, Room connectedRoom) {
        this.transform = transform;
        this.parent = parent;
        this.connectedRoom = connectedRoom;
    }

    public void setParent(Room room) {
        this.parent = room;
    }

    public void setConnectedRoom(Room room) {
        if (connectedRoom != null)
            throw new IllegalArgumentException("Door ( " + this + " ) is already connected");
        connectedRoom = room;
    }

    public Transform getTransform() {return transform;}
    public Room getParent() {return parent;}
    public Room getConnectedRoom() {return connectedRoom;}

    public void removeConnectedRoom() {connectedRoom = null;}
}
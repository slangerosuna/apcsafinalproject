package io.github.slangerosuna.engine.core.ecs;

import java.util.ArrayList;

public class Entity {
    private ArrayList<Component> components;
    private int id;
    private boolean alive = true;

    public boolean isAlive(){
        return alive;
    }

    public Entity(Scene scene){
        components = new ArrayList<Component>();
        id = scene.addEntity(this);
    }

    public Entity(Scene scene, Component... components){
        this(scene);
        for(var component : components)
            addComponent(component);
    }

    public void kill() {
        killAllChildren();
        alive = false;
    }

    public void killAllChildren() {
        for(var component : components)
            component.kill();
    }

    public int getId(){
        return id;
    }

    public void decrementId(){
        id--;
    }

    public void addComponent(Component component){
        components.add(component);
    }

    public Component getComponent(int type){
        for(var component : components)
            if(component.getType() == type)
                return component;
        return null;
    }

    public boolean hasComponent(int id){
        for(var component : components)
            if(component.getType() == id)
                return true;
        return false;
    }
}

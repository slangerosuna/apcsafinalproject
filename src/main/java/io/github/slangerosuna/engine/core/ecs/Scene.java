package io.github.slangerosuna.engine.core.ecs;

import java.util.ArrayList;

import io.github.slangerosuna.engine.core.scheduler.Scheduler;

public class Scene {
    private ArrayList<Entity> entities;
    private ArrayList<System> systems;
    private ArrayList<Resource> resources;

    private Scheduler scheduler;

    public Scene(int numThreads){
        entities = new ArrayList<Entity>();
        systems = new ArrayList<System>();
        resources = new ArrayList<Resource>();
        scheduler = new Scheduler(this, numThreads);
    }

    public void init() {
        scheduler.genExecutionOrders(systems);
        scheduler.execute(SystemType.INIT);
    }

    public void update() {
        scheduler.execute(SystemType.UPDATE);
        removeDeadEntities();
    }

    public void fixedUpdate() {
        scheduler.execute(SystemType.FIXEDUPDATE);
    }

    public void deInit() {
        scheduler.execute(SystemType.DEINIT);
        
        while(entities.size() > 0)
            entities.remove(0).kill();
        while(resources.size() > 0)
            resources.remove(0).kill();
    }

    public void removeDeadEntities() {
        for (int i = 0; i < entities.size(); i++)
            if (!entities.get(i).isAlive())
                removeEntity(entities.get(i--));
    }

    // All ArrayList mutation must be in critical sections of code to avoid race conditions
    public int addEntity(Entity entity){
        entities.add(entity);
        return entities.size() - 1;
    }

    public void removeEntity(Entity entity){
        entities.remove(entity.getId());
        for (int i = entity.getId(); i < entities.size(); i++)
            entities.get(i).decrementId();
    }

    public void addSystem(System system){
        systems.add(system);
    }

    public void addResource(Resource resource){
        resources.add(resource);
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public ArrayList<Resource> getResources() {
        return resources;
    }
}

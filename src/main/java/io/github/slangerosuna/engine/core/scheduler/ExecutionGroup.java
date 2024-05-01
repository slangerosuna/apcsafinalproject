package io.github.slangerosuna.engine.core.scheduler;

import java.util.ArrayList;

import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.ecs.System;

public class ExecutionGroup {
    private System[] systems;

    public ExecutionGroup(System[] systems){
        this.systems = systems;
    }

    public ArrayList<Task> genTasks(ArrayList<Entity> entities, ArrayList<Resource> resources, float deltaTime){
        var tasks = new ArrayList<Task>();

        for (System system : systems){
            var queries = system.getQueries();
            var entitiesToPass = entities.stream()
                .filter(entity -> {
                    for (var query : queries) 
                        if (query.matches(entity)) 
                            return true;
                    return false; 
                })
                .toArray(Entity[]::new);
            var resourcesToPass = resources.stream()
                .filter(resource -> {
                    for (var query: queries)
                        if (query.matches(resource))
                            return true;
                    return false;
                })
                .toArray(Resource[]::new);

            tasks.add(new Task(() -> { system.execute(entitiesToPass, resourcesToPass, deltaTime); return true; }));
        }
        return tasks;
    }
}

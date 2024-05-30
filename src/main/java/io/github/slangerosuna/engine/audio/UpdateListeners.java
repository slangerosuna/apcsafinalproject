package io.github.slangerosuna.engine.audio;

import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.core.ecs.SystemType;

public class UpdateListeners extends System {
    public UpdateListeners() {
        super(SystemType.UPDATE, "ENTITY AND ( HAS Listener )", "SYNC");
    }

    @Override
    public void execute(Entity[] entities, Resource[] resources, float deltaTime) {
        for (var entity : entities) {
            var listener = (Listener)entity.getComponent(Listener.type);
            listener.update();
        }
    }
}

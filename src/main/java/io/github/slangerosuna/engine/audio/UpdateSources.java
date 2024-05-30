package io.github.slangerosuna.engine.audio;

import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.core.ecs.SystemType;

public class UpdateSources extends System {
    public UpdateSources() {
        super(SystemType.UPDATE, "ENTITY AND ( HAS Source )", "SYNC");
    }

    @Override
    public void execute(Entity[] entities, Resource[] resources, float deltaTime) {
        for (var entity : entities) {
            var source = (Source)entity.getComponent(Source.type);
            source.update();
        }
    }
}

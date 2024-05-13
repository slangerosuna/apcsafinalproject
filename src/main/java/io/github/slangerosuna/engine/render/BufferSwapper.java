package io.github.slangerosuna.engine.render;

import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.io.Window;

public class BufferSwapper extends System {
    public BufferSwapper() {
        super(
            SystemType.RENDER,
            "RESOURCE Window",
            "SYNC"
        );
    }   

    @Override
    public void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime) {
        if (queriedResources.length == 0) return;
        ((Window)queriedResources[0]).swapBuffers();
    }
}

package io.github.slangerosuna.engine.io;

import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.ecs.Entity;

public class WindowUpdate extends System {
    public WindowUpdate() {
        super(SystemType.LATEUPDATE,
            "RESOURCE Window",
			"SYNC"
        );
    }

    public void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime) {
        ((Window)queriedResources[0]).update();
    }
}

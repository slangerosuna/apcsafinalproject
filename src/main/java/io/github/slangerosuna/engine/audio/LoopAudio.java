package io.github.slangerosuna.engine.audio;

import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.core.ecs.SystemType;

public class LoopAudio extends System {
    public LoopAudio() {
        super(SystemType.UPDATE,
            "ENTITY HAS Source",
            "SYNC"
        );
    }

    public void execute(Entity[] entities, Resource[] resources, float dt) {
        for (var entity : entities) {
            var source = (Source)entity.getComponent(Source.type);
            if (!source.isPlaying())
                source.play();
        }
    }
}

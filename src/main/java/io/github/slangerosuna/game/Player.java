package io.github.slangerosuna.game;

import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.core.ecs.Component;
import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.io.Input;
import io.github.slangerosuna.engine.render.Transform;

public class Player implements Component {
    public static final int type = Component.registerComponent("Player");
    public int getType() {
        return type;
    }
    public void kill() {}

    private float speed;

    public Player() {
        speed = 1;
    }
    public Player(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {return speed;}
}
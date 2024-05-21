package io.github.slangerosuna.game.enemy;

import io.github.slangerosuna.engine.core.ecs.Component;

public class Enemy implements Component {
    public static final int type = Component.registerComponent("Enemy");
    public int getType() { return type; }
    public void kill() {}

    private float speed = 1.0f;
    private boolean routesToPlayer = false;

    public Enemy(float speed) { this.speed = speed; }
    public Enemy(float speed, boolean routesToPlayer) { this(speed); this.routesToPlayer = routesToPlayer; }

    public boolean routesToPlayer() {return routesToPlayer;}    
    public float getSpeed() {return speed;}
}
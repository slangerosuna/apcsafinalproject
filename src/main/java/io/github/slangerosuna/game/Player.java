package io.github.slangerosuna.game;

import io.github.slangerosuna.engine.core.ecs.Component;

public class Player implements Component {
    public static final int type = Component.registerComponent("Player");
    public int getType() { return type; }
    public void kill() {}

    private float speed;
    private float health = 10.0f;

    public Player(float speed) { this.speed = speed; }
    public Player() { this(1.0f); }

    public float getSpeed() {return speed;}
    public void damage(float amount) { health -= amount; }
}
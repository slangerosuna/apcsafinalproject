package io.github.slangerosuna.game.enemy;

import io.github.slangerosuna.engine.core.ecs.Component;

public class Projectile implements Component {
    public static final int type = Component.registerComponent("Projectile");
    public int getType() { return type; }
    public void kill() {}
    
    private float speed = 1.0f;
    private float damage = 1.0f;
    private float lifetime = 5.0f;
    public float timeAlive;

    public Projectile(float speed) { this.speed = speed; this.timeAlive = 0.0f; }
    public Projectile(float speed, float damage) { this(speed); this.damage = damage; this.timeAlive = 0.0f; }

    public float getSpeed() { return speed; }
    public float getDamage() { return damage; }
    public float getLifetime() { return lifetime; }
}

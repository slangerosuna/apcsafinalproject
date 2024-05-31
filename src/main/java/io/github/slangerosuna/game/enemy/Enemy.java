package io.github.slangerosuna.game.enemy;

import io.github.slangerosuna.engine.core.ecs.Component;

public class Enemy implements Component {
    public static final int type = Component.registerComponent("Enemy");
    public int getType() { return type; }
    public void kill() {}

    private float speed = 1.0f;
    private boolean routesToPlayer = false;
    private boolean attacksPlayer = true;
    private float attackInterval = 1.0f;
    private float lastAttack = 0.0f;
    private float startDelay = 5.0f;

    public Enemy(float speed) { this.speed = speed; }
    public Enemy(float speed, boolean routesToPlayer) { this(speed); this.routesToPlayer = routesToPlayer; }
    public Enemy(float speed, boolean routesToPlayer, boolean attacksPlayer) { this(speed, routesToPlayer); this.attacksPlayer = attacksPlayer; }
    public Enemy(float speed, boolean routesToPlayer, boolean attacksPlayer, float attackInterval) { this(speed, routesToPlayer, attacksPlayer); this.attackInterval = attackInterval; }

    public Enemy(float speed, boolean routesToPlayer, boolean attacksPlayer, float attackInterval, float startDelay) { this(speed, routesToPlayer, attacksPlayer, attackInterval); this.startDelay = startDelay; }
    
    public boolean routesToPlayer() {return routesToPlayer;}
    public boolean attacksPlayer() {return attacksPlayer;}
    public boolean canAttack(float time) {
        if (time > startDelay && (time - lastAttack >= attackInterval)) {
            lastAttack = time;
            return true;
        }
        return false;
    }
    public float getSpeed() {return speed;}
}
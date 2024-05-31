package io.github.slangerosuna.game.enemy;

import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.physics.Collider;
import io.github.slangerosuna.engine.render.Transform;
import io.github.slangerosuna.game.Player;

import java.util.Arrays;

import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.ecs.System;

public class ProjectileController extends System {
    public ProjectileController() {
        super(SystemType.UPDATE,
            "ENTITY AND ( HAS Transform HAS Collider HAS Projectile )",
            "ENTITY AND ( HAS Transform HAS Collider HAS Player )",
            "SYNC" // needs to kill projectiles
        );
    }

    public void execute(Entity[] entities, Resource[] resources, float deltaTime) {
        var player = Arrays.stream(entities)
            .filter(e -> e.hasComponent(Player.type))
            .findFirst()
            .get();
        var playerCollider = (Collider)player.getComponent(Collider.type);
        var projectiles = Arrays.stream(entities)
            .filter(e -> e.hasComponent(Projectile.type));

        projectiles.forEach(projectile -> {
            var projectileTransform = (Transform)projectile.getComponent(Transform.type);
            var projectileProjectile = (Projectile)projectile.getComponent(Projectile.type);

            projectileProjectile.timeAlive += deltaTime;
            if (projectileProjectile.timeAlive >= projectileProjectile.getLifetime()) projectile.kill();

            projectileTransform.position = projectileTransform.position.add(projectileTransform.forward().multiply(projectileProjectile.getSpeed() * deltaTime));

            if (((Collider)projectile.getComponent(Collider.type)).intersects(playerCollider)) {
                ((Player)player.getComponent(Player.type)).damage(((Projectile)projectile.getComponent(Projectile.type)).getDamage());
                projectile.kill();
            }
        });
    }
}

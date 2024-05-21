package io.github.slangerosuna.game.enemy;

import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.math.vector.Vector3;
import io.github.slangerosuna.engine.physics.RigidBody;
import io.github.slangerosuna.engine.render.Transform;
import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.game.Player;

import java.util.Arrays;
import java.util.stream.Stream;

public class EnemyController extends System {
    public EnemyController() {
        super(
            SystemType.UPDATE,
            "ENTITY AND ( HAS Transform HAS RigidBody HAS Enemy )",
            "ENTITY AND ( HAS Transform HAS RigidBody HAS Player )"
        );
    }

    @Override
    public void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime) {
        Entity player =
            Arrays.stream(queriedEntities)
                .filter(e -> e.hasComponent(Player.type))
                .findFirst()
                .get();
        Stream<Entity> enemies =
            Arrays.stream(queriedEntities)
                .filter(e -> e.hasComponent(Enemy.type));

        enemies.forEach( enemy -> {
            var enemyTransform = (Transform)enemy.getComponent(Transform.type);
            var enemyRigidbody = (RigidBody)enemy.getComponent(RigidBody.type);
            var playerTransform = (Transform)player.getComponent(Transform.type);

            Vector3 playerPos = playerTransform.position;
            Vector3 enemyPos = enemyTransform.position;

            // may want to make a sight check to prevent enemies from routing to player without seeing them

            Vector3 direction = playerPos.sub(enemyPos).normalized();
            float targetYRot = (float)Math.toDegrees(Math.atan2(direction.x, direction.z));

            // rotate enemy towards player at a constant rate (keeping track of velocity may be better in the future)
            float currentYRot = enemyTransform.rotation.y;
            float diff = targetYRot - currentYRot;
            if (diff > 180) diff -= 360;
            if (diff < -180) diff += 360;

            float rotSpeed = 180.0f;
            float maxRot = rotSpeed * deltaTime;
            float newRot = currentYRot + Math.min(Math.max(diff, -maxRot), maxRot);

            enemyTransform.rotation.y = newRot;
            if (!((Enemy)enemy.getComponent(Enemy.type)).routesToPlayer()) 
                return;

            // temporarily not using pathfinding, just moving towards player
            float speed = ((Enemy)enemy.getComponent(Enemy.type)).getSpeed();
            
            Vector3 horzMovement = direction.multiply(speed * deltaTime);

            Vector3 horzVelocityOp = new Vector3(enemyRigidbody.velocity.x, 0, enemyRigidbody.velocity.z).multiply(-0.2f * (float)Math.exp(deltaTime));

            enemyRigidbody.applyImpulse(horzVelocityOp);
            enemyRigidbody.applyImpulse(horzMovement);
        });
    }
}
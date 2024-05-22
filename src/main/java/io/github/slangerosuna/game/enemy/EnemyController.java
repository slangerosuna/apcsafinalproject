package io.github.slangerosuna.game.enemy;

import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.math.vector.Vector3;
import io.github.slangerosuna.engine.physics.Collider;
import io.github.slangerosuna.engine.physics.RigidBody;
import io.github.slangerosuna.engine.render.Material;
import io.github.slangerosuna.engine.render.Transform;
import io.github.slangerosuna.engine.utils.ObjLoader;
import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.ecs.Scene;
import io.github.slangerosuna.game.Player;

import java.util.Arrays;
import java.util.stream.Stream;

public class EnemyController extends System {
    private float time = 0;
    private String projectileModelPath = "/io/github/slangerosuna/resources/models/eye.obj";
    private String projectileTexturePath = "/io/github/slangerosuna/resources/textures/eye.jpg";

    public EnemyController() {
        super(
            SystemType.UPDATE,
            "ENTITY AND ( HAS Transform HAS RigidBody HAS Enemy )",
            "ENTITY AND ( HAS Transform HAS RigidBody HAS Player )",
            "SYNC" // needs to be sync to access Scene
        );
    }

    @Override
    public void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime) {
        time += deltaTime;
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
            targetYRot += 90;

            // rotate enemy towards player at a constant rate (keeping track of velocity may be better in the future)
            float currentYRot = enemyTransform.rotation.y;
            float diff = targetYRot - currentYRot;

            // make sure diff is in [-180, 180] so that the enemy rotates the shortest way
            diff %= 360;
            if (diff > 180) diff -= 360;
            if (diff < -180) diff += 360;

            diff += 90;

            float rotSpeed = 180.0f;
            float maxRot = rotSpeed * deltaTime;
            float newRot = currentYRot + Math.min(Math.max(diff, -maxRot), maxRot);

            enemyTransform.rotation.y = newRot;
            enemyTransform.rotation.x = 90;

            if (((Enemy)enemy.getComponent(Enemy.type)).attacksPlayer()
            &&  ((Enemy)enemy.getComponent(Enemy.type)).canAttack(time)) {
                var projectile = new Entity(Scene.curScene, new Projectile(1.0f, 1.0f));
                var mesh = ObjLoader.loadObj(projectileModelPath);
                var mat = new Material(projectileTexturePath);

                projectile.addComponent(mesh);
                projectile.addComponent(mat);

                Vector3 projPos = enemyPos.add(new Vector3(0, 0, 0));
                Transform projTransform = new Transform(projPos, new Vector3(0, targetYRot - 90f, 0), new Vector3(0.1f, 0.1f, 0.1f));

                Collider projCollider = new Collider(1.0f, 1.0f, 1.0f, projTransform);

                projectile.addComponent(projCollider);
                projectile.addComponent(projTransform);
            }

            if (((Enemy)enemy.getComponent(Enemy.type)).routesToPlayer()){
                // temporarily not using pathfinding, just moving towards player
                float speed = ((Enemy)enemy.getComponent(Enemy.type)).getSpeed();
                
                Vector3 horzMovement = direction.multiply(speed * deltaTime);

                enemyRigidbody.applyImpulse(horzMovement);
            }
            Vector3 horzVelocityOp = new Vector3(enemyRigidbody.velocity.x, 0, enemyRigidbody.velocity.z).multiply(-0.2f * (float)Math.exp(deltaTime));
            enemyRigidbody.applyImpulse(horzVelocityOp);
        });
    }
}
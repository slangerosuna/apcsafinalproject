package io.github.slangerosuna.engine.physics;

import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.math.vector.Vector3;
import io.github.slangerosuna.engine.render.Transform;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.ecs.Entity;


public class PhysicsUpdate extends System {
    float gravity = 1.0f;

    public PhysicsUpdate() {
        super(
            SystemType.UPDATE,
        "ENTITY AND ( HAS RigidBody HAS Transform HAS Collider )",
            "ENTITY AND ( HAS Transform HAS Collider )"
        );
    }

    public void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime) {
        for (var entity : queriedEntities) {
            if (!(entity.hasComponent(RigidBody.type))) continue;
            var rigidBody = (RigidBody)entity.getComponent(RigidBody.type);
            var transform = (Transform)entity.getComponent(Transform.type);

            rigidBody.applyGravity(gravity * deltaTime);
            transform.position = transform.position.add(rigidBody.velocity.multiply(0.5f));
        }

        for (int i = 0; i < queriedEntities.length; i++) {
            if (!(queriedEntities[i].hasComponent(RigidBody.type))) continue;
            for (int j = i + 1; j < queriedEntities.length; j++) {
                if (!(queriedEntities[j].hasComponent(RigidBody.type))) continue;
                var entityA = queriedEntities[i];
                var entityB = queriedEntities[j];

                var rigidBodyA = (RigidBody)entityA.getComponent(RigidBody.type);
                var rigidBodyB = (RigidBody)entityB.getComponent(RigidBody.type);

                var transformA = (Transform)entityA.getComponent(Transform.type);
                var transformB = (Transform)entityB.getComponent(Transform.type);

                var colliderA = (Collider)entityA.getComponent(Collider.type);
                var colliderB = (Collider)entityB.getComponent(Collider.type);

                if (colliderA.intersects(colliderB)) {
                    var axis = colliderA.getIntersectionDirection(colliderB, rigidBodyA.velocity.sub(rigidBodyB.velocity));

                    var velocityAOnAxis = rigidBodyA.velocity.dot(axis);
                    var velocityBOnAxis = rigidBodyB.velocity.dot(axis);

                    var displacement = transformA.position.sub(transformB.position);

                    // verify they're going towards eachother
                    if (axis.multiply(velocityBOnAxis - velocityAOnAxis).dot(displacement) < 0)
                        continue;
                    
                    handleCollision(rigidBodyA, rigidBodyB, axis, 0.3f);
                }
            }

            for (int j = 0; j < queriedEntities.length; j++) {
                if (queriedEntities[j].hasComponent(RigidBody.type)) continue;
                
                var entityA = queriedEntities[i];
                var entityB = queriedEntities[j];

                var transformA = (Transform)entityA.getComponent(Transform.type);
                var transformB = (Transform)entityB.getComponent(Transform.type);

                var rigidBodyA = (RigidBody)entityA.getComponent(RigidBody.type);

                var colliderA = (Collider)entityA.getComponent(Collider.type);
                var colliderB = (Collider)entityB.getComponent(Collider.type);
                
                if (colliderA.intersects(colliderB)) {
                    var axis = colliderA.getIntersectionDirection(colliderB, rigidBodyA.velocity);
                    var velocityAOnAxis = axis.multiply(rigidBodyA.velocity.dot(axis));
                    java.lang.System.out.println(velocityAOnAxis);
                    rigidBodyA.velocity = rigidBodyA.velocity.sub(velocityAOnAxis);
                    java.lang.System.out.println(rigidBodyA.velocity);

                    java.lang.System.out.println(axis);

                    // teleport the object out of the collision
                    if (axis.x != 0)
                        transformA.position.x = transformB.position.x + ((colliderA.getWidth() + colliderB.getWidth()) / 2 * -Math.signum(axis.x));
                    if (axis.y != 0)
                        transformA.position.y = transformB.position.y - ((colliderA.getHeight() + colliderB.getHeight()) / 2 * -Math.signum(axis.y));
                    if (axis.z != 0)
                        transformA.position.z = transformB.position.z + ((colliderA.getDepth() + colliderB.getDepth()) / 2 * -Math.signum(axis.z));
                }
            }
        }
        for (var entity : queriedEntities) {
            if (!(entity.hasComponent(RigidBody.type))) continue;
            var rigidBody = (RigidBody)entity.getComponent(RigidBody.type);
            var transform = (Transform)entity.getComponent(Transform.type);

            transform.position = transform.position.add(rigidBody.velocity.multiply(0.5f));
        }
    }

    private void handleCollision(RigidBody a, RigidBody b, Vector3 axis, float elasticity) {
        var momentumA = a.velocity.multiply(a.getMass());
        var momentumB = b.velocity.multiply(b.getMass());

        var velocityAOnAxis = a.velocity.dot(axis);
        var velocityBOnAxis = b.velocity.dot(axis);

        var momentumAOnAxis = axis.multiply(momentumA.dot(axis));
        var momentumBOnAxis = axis.multiply(momentumB.dot(axis));
        var totalMomentumOnAxis = momentumAOnAxis.add(momentumBOnAxis);
        var totalMass = a.getMass() + b.getMass();
        var inelasticVelocityOnAxis = totalMomentumOnAxis.divide(totalMass);

        var elasticVelocityAOnAxis = (momentumAOnAxis.multiply(a.getMass() - b.getMass()).add(momentumBOnAxis.multiply(2 * b.getMass()))).divide(totalMass);
        var elasticVelocityBOnAxis = (momentumBOnAxis.multiply(b.getMass() - a.getMass()).add(momentumAOnAxis.multiply(2 * a.getMass()))).divide(totalMass);

        var velocityAOnAxisAfterCollision = elasticVelocityAOnAxis.multiply(elasticity).add(inelasticVelocityOnAxis.multiply(1 - elasticity));
        var velocityBOnAxisAfterCollision = elasticVelocityBOnAxis.multiply(elasticity).add(inelasticVelocityOnAxis.multiply(1 - elasticity));

        var velocityAPerpendicular = a.velocity.sub(axis.multiply(velocityAOnAxis));
        var velocityBPerpendicular = b.velocity.sub(axis.multiply(velocityBOnAxis));

        a.velocity = velocityAPerpendicular.add(velocityAOnAxisAfterCollision);
        b.velocity = velocityBPerpendicular.add(velocityBOnAxisAfterCollision);
    }
}

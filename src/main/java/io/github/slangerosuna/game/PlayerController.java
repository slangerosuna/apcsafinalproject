package io.github.slangerosuna.game;

import org.lwjgl.glfw.GLFW;
import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.io.Input;
import io.github.slangerosuna.engine.render.Transform;
import io.github.slangerosuna.engine.physics.RigidBody;
import io.github.slangerosuna.engine.math.vector.Vector3;

public class PlayerController extends System {

    public PlayerController() {
        super(
            SystemType.UPDATE,
            "ENTITY AND ( HAS Camera HAS Transform HAS RigidBody HAS Player )"
        );
    }

    @Override
    public void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime) {
        Entity player = queriedEntities[0];
        RigidBody playerRB = (RigidBody)player.getComponent(RigidBody.type);
        float playerSpeed = ((Player)player.getComponent(Player.type)).getSpeed();
        Transform camTransform = (Transform)player.getComponent(Transform.type);
        Vector3 camRotation = camTransform.rotation;

        float angle = camRotation.y;
        float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));

        Vector3 forward = new Vector3(-sin, 0, -cos);
        Vector3 backward = forward.multiply(-1);
        Vector3 up = new Vector3(0, 1, 0);
        Vector3 right = forward.cross(up);
        Vector3 left = right.multiply(-1);

        int forwardKey = GLFW.GLFW_KEY_W;
        int backKey = GLFW.GLFW_KEY_S;
        int leftKey = GLFW.GLFW_KEY_A;
        int rightKey = GLFW.GLFW_KEY_D;

        Vector3 horzMovementUnit = new Vector3(0, 0, 0);
        
        if (Input.isKeyDown(forwardKey))
            horzMovementUnit = horzMovementUnit.add(forward);
        if (Input.isKeyDown(backKey))
            horzMovementUnit = horzMovementUnit.add(backward);
        if (Input.isKeyDown(leftKey))
            horzMovementUnit = horzMovementUnit.add(left);
        if (Input.isKeyDown(rightKey))
            horzMovementUnit = horzMovementUnit.add(right);

        Vector3 horzMovement = horzMovementUnit.multiply(playerSpeed * deltaTime);

        Vector3 horzVelocityOp = new Vector3(playerRB.velocity.x, 0, playerRB.velocity.z).multiply(-10.0f*deltaTime);

        playerRB.applyImpulse(horzVelocityOp);
        playerRB.applyImpulse(horzMovement);
    }
}
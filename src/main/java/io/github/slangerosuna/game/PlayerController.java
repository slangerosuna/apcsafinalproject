package io.github.slangerosuna.game;

import java.util.function.BooleanSupplier;

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
    private boolean prevSpace = false;

    private static BooleanSupplier playTpNoise;
    public static void setTpNoise(BooleanSupplier tpNoise) { playTpNoise = tpNoise; }

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
        Player playerInfo = (Player)player.getComponent(Player.type);
        float playerSpeed = playerInfo.getSpeed();
        Transform camTransform = (Transform)player.getComponent(Transform.type);
        Vector3 camRotation = camTransform.rotation;

        float angle = camRotation.y;
        float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));

        Vector3 forward = new Vector3(-sin, 0, -cos);
        Vector3 backward = forward.multiply(-1);
        Vector3 up = new Vector3(0, 1, 0);
        Vector3 down = up.multiply(-1);
        Vector3 right = forward.cross(up);
        Vector3 left = right.multiply(-1);

        int forwardKey = GLFW.GLFW_KEY_W;
        int backKey = GLFW.GLFW_KEY_S;
        int upKey = GLFW.GLFW_KEY_E;
        int downKey = GLFW.GLFW_KEY_Q;
        int leftKey = GLFW.GLFW_KEY_A;
        int rightKey = GLFW.GLFW_KEY_D;

        int flightOnKey = GLFW.GLFW_KEY_G;
        int flightOffKey = GLFW.GLFW_KEY_V;
        if (Input.isKeyDown(flightOnKey)) {
            playerInfo.flying = true;
            playerRB.useGravity = false;
        }
        if (Input.isKeyDown(flightOffKey)) {
            playerInfo.flying = false;
            playerRB.useGravity = true;
        }

        Vector3 vertMovement = new Vector3(0, 0, 0);
        if (playerInfo.flying) {
            Vector3 vertMovementUnit = new Vector3(0, 0, 0);
            if (Input.isKeyDown(upKey)) {
                vertMovementUnit = vertMovementUnit.add(up);
            }
            if (Input.isKeyDown(downKey)) {
                vertMovementUnit = vertMovementUnit.add(down);
            }
            vertMovement = vertMovementUnit.multiply(playerSpeed * 2.5f * deltaTime).add(new Vector3(0, playerRB.velocity.y, 0).multiply(-0.2f * (float)Math.exp(deltaTime)));
        }

        Vector3 horzMovementUnit = new Vector3(0, 0, 0);
        
        if (Input.isKeyDown(forwardKey))
            horzMovementUnit = horzMovementUnit.add(forward);
        if (Input.isKeyDown(backKey))
            horzMovementUnit = horzMovementUnit.add(backward);
        if (Input.isKeyDown(leftKey))
            horzMovementUnit = horzMovementUnit.add(left);
        if (Input.isKeyDown(rightKey))
            horzMovementUnit = horzMovementUnit.add(right);

        if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE) && !prevSpace && playerRB.velocity.y == 0) {
            playerRB.applyImpulse(new Vector3(0, 0.3f, 0));
            prevSpace = true;
        } else if (!Input.isKeyDown(GLFW.GLFW_KEY_SPACE))
            prevSpace = false;

        Vector3 horzMovement = horzMovementUnit.multiply(playerSpeed * deltaTime);

        Vector3 horzVelocityOp = new Vector3(playerRB.velocity.x, 0, playerRB.velocity.z).multiply(-0.2f * (float)Math.exp(deltaTime));

        playerRB.applyImpulse(horzVelocityOp);
        playerRB.applyImpulse(horzMovement.add(vertMovement));

        if (camTransform.position.y <= -10f) {
            playerRB.velocity = Vector3.zero();
            camTransform.position = new Vector3(0f, 10f, 0f);
            playTpNoise.getAsBoolean();
        }
    }
}
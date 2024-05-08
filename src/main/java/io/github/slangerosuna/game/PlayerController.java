package io.github.slangerosuna.game;

import org.lwjgl.glfw.GLFW;
import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.io.Input;
import io.github.slangerosuna.engine.render.Transform;
import io.github.slangerosuna.engine.physics.RigidBody;

public class PlayerController extends System {

    public PlayerController() {
        super(
            SystemType.UPDATE,
            "ENTITY AND ( HAS RigidBody HAS Player )",
            "ENTITY AND ( HAS Camera HAS Transform HAS Player)"
        );
    }

    @Override
    public void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime) {
        Entity player = queriedEntities[0];
        Entity camera = queriedEntities[1];
        Rigidbody playerRB = (RigidBody)player.getComponent(RigidBody.type);
        Transform camTransform = (Transform)camera.getComponent(Transform.type);
        Vector3 camRotation = camTransform.rotation();
        
        int forwardKey = GLFW.GLFW_KEY_W;

        //WIP: Move in the direction the camera is facing when W key pressed
        if (Input.isKeyDown(forwardKey)) {
            playerRB.applyImpulse(new Vector3());
        }
    }
}
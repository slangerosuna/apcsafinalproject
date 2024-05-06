package io.github.slangerosuna.game;

import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.io.Input;
import io.github.slangerosuna.engine.render.Transform;

public class RotateCamera extends System {
    private float sens = 0.45f;
    private double prevX, prevY;
    private boolean prevFocused = false;

    public RotateCamera() {
        super(
            SystemType.UPDATE,
            "ENTITY AND ( HAS Camera HAS Transform )"
        );

        prevX = 0;
        prevY = 0;
    }

    @Override
    public void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime) {
        if (!Input.isFocused()) {
            prevFocused = false;
            return;
        }
        if (!prevFocused) {
            prevX = Input.getMouseX();
            prevY = Input.getMouseY();
            prevFocused = true;
        }
        var cameraEntity = queriedEntities[0];
        var cameraTransform = (Transform)cameraEntity.getComponent(Transform.type);

        var mouseX = Input.getMouseX();
        var mouseY = Input.getMouseY();

        var deltaX = mouseX - prevX;
        var deltaY = mouseY - prevY;

        prevX = mouseX;
        prevY = mouseY;

        cameraTransform.rotation.y -= deltaX * sens;
        cameraTransform.rotation.x -= deltaY * sens;
    }
}
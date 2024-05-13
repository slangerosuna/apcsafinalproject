package io.github.slangerosuna.engine.render.ui;

import io.github.slangerosuna.engine.core.ecs.Component;

public class UIElement implements Component {
    public static final int type = Component.registerComponent("UIElement");
    @Override public int getType() { return type; }
    @Override public void kill() { }

    public float x, y, width, height;

    public UIElement(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}

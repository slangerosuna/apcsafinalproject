package io.github.slangerosuna.game;

import io.github.slangerosuna.engine.core.ecs.Component;
import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Scene;
import io.github.slangerosuna.engine.render.Material;
import io.github.slangerosuna.engine.render.ui.UIElement;

import java.util.function.BooleanSupplier;

public class Player implements Component {
    public static final int type = Component.registerComponent("Player");
    public int getType() { return type; }
    public void kill() {}

    private static BooleanSupplier displayRedVignette = () -> {
        var scene = Scene.curScene;

        var vignette = new Entity(scene, new UIElement(0, 0, 2, 2));
        vignette.addComponent(new Material("/io/github/slangerosuna/resources/textures/redVignette.png"));

        return false;
    };
    public static void setDisplayRedVignette(BooleanSupplier displayRedVignette) { Player.displayRedVignette = displayRedVignette; }

    private static BooleanSupplier die = () -> {
        var scene = Scene.curScene;

        var gameOver = new Entity(scene, new UIElement(0, 0, 2, 2));
        gameOver.addComponent(new Material("/io/github/slangerosuna/resources/textures/gameOver.png"));

        return false;
    };
    public static void setDie(BooleanSupplier die) { Player.die = die; }

    private float speed;
    private float health = 2.0f;
    public boolean flying;

    public Player(float speed) { this.speed = speed; }
    public Player() { this(1.0f); }

    public float getSpeed() {return speed;}
    public void damage(float amount) { 
        health -= amount;
        if (health <= 1) displayRedVignette.getAsBoolean();
        if (health <= 0) { die.getAsBoolean(); }
    }
}
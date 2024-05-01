package io.github.slangerosuna.engine.core.ecs;

import java.util.HashMap;

public interface Component {
    public static HashMap<String, Integer> componentIds = new HashMap<>();
    public static int registerComponent(String component) {
        java.lang.System.out.println("Registering component: " + component);
        componentIds.put(component, componentIds.size());
        return componentIds.size() - 1;
    }

    public int getType();
    public void kill();
}
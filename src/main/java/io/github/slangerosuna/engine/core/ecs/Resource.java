package io.github.slangerosuna.engine.core.ecs;

import java.util.HashMap;

public interface Resource {
    public static HashMap<String, Integer> resourceIds = new HashMap<String, Integer>();
    public static int registerResource(String resource) {
        resourceIds.put(resource, resourceIds.size());
        return resourceIds.size() - 1;
    }

    public int getType();
    public void kill();
}

package io.github.slangerosuna.engine.core.query.queryTypes;

import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.query.Query;

public class ResourceQuery implements Query {
    private int id;

    @Override
    public Query[] children() {
        return new Query[0];
    }

    @Override
    public boolean overlaps(Query other) {
        if (other instanceof SyncQuery)
            return true;
        if (other instanceof ResourceQuery)
            return ((ResourceQuery) other).id == id;
        else {
            if (other instanceof NotQuery)
                return false;
            for (var child : other.children())
                if (overlaps(child))
                    return true;
        }
        return false;
    }

    public ResourceQuery(String resource) {
        this.id = Resource.resourceIds.getOrDefault(resource, -1);
        if (this.id == -1)
            throw new IllegalArgumentException("Resource " + resource + " does not exist");
    }

    @Override
    public boolean matches(Entity entity) {
        return false;
    }

    @Override
    public boolean matches(Resource resource) {
        return resource.getType() == id;
    }
}

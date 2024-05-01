package io.github.slangerosuna.engine.core.query.queryTypes;

import io.github.slangerosuna.engine.core.ecs.Component;
import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.query.Query;

public class HasQuery implements Query {
    private int component;

    @Override
    public Query[] children(){
        return new Query[0];
    }

    public HasQuery(String component){
        this.component = Component.componentIds.getOrDefault(component, -1);
        if(this.component == -1)
            throw new IllegalArgumentException("Component " + component + " does not exist");
    }

    @Override
    public boolean overlaps(Query other){
        if (other instanceof HasQuery)
            return ((HasQuery) other).component == component;
        else {
            if (other instanceof NotQuery)
                return false;
            for (var child : other.children())
                if (overlaps(child))
                    return true;
        }
        if (other instanceof SyncQuery)
            return true;
        return false;
    }

    @Override
    public boolean matches(Entity entity){
        return entity.hasComponent(component);
    }

    @Override
    public boolean matches(Resource resource){
        return false;
    }
}

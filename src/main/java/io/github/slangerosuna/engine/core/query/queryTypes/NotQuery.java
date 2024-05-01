package io.github.slangerosuna.engine.core.query.queryTypes;

import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.query.Query;

public class NotQuery implements Query {
    private Query query;

    @Override
    public Query[] children(){
        return new Query[]{query};
    }

    @Override
    public boolean overlaps(Query other){
        if (other instanceof SyncQuery)
            return true;
        return false;
    }

    public NotQuery(Query query){
        this.query = query;
    }

    @Override
    public boolean matches(Entity entity){
        return !query.matches(entity);
    }

    @Override
    public boolean matches(Resource resource){
        return !query.matches(resource);
    }
}

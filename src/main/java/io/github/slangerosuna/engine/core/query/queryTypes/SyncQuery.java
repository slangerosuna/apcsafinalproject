package io.github.slangerosuna.engine.core.query.queryTypes;

import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.query.Query;

public class SyncQuery implements Query {
    @Override public Query[] children(){ return new Query[0]; }

    public SyncQuery() {}

    // sync query overlaps with any query so that its systems never execute concurrently
    @Override public boolean overlaps(Query other){ return true; }

    // it never matches anything because it is not a query
    @Override public boolean matches(Entity entity) { return false; }
    @Override public boolean matches(Resource resource){ return false;}
    
}

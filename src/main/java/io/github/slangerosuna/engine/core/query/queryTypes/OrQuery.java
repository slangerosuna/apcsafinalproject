package io.github.slangerosuna.engine.core.query.queryTypes;

import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.query.Query;

public class OrQuery implements Query {
    private Query[] queries;

    @Override
    public Query[] children(){
        return queries;
    }

    @Override
    public boolean overlaps(Query other){
        if (other instanceof SyncQuery)
            return true;
        if (other instanceof AndQuery) {
            AndQuery andQuery = (AndQuery) other;
            for (Query query : queries)
                if (andQuery.overlaps(query))
                    return true;
            return false;
        }
        if (other instanceof OrQuery) {
            OrQuery orQuery = (OrQuery) other;
            for (Query query : queries)
                for (Query otherQuery : orQuery.queries)
                    if (query.overlaps(otherQuery))
                        return true;
            return false;
        }
        for (Query query : queries)
            if (query.overlaps(other))
                return true;
        return false;

    }

    @Override
    public boolean matches(Entity entity){
        for (Query query : queries)
            if( query.matches(entity))
                return true;
        return false;
    }

    @Override
    public boolean matches(Resource resource){
        for (Query query : queries)
            if( query.matches(resource))
                return true;
        return false;
    }

    public OrQuery(Query... queries){
        this.queries = queries;
    }
}

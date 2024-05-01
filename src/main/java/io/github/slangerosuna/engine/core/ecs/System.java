package io.github.slangerosuna.engine.core.ecs;

import java.util.Arrays;

import io.github.slangerosuna.engine.core.query.Query;

import java.util.stream.Collectors;

public abstract class System {
    private Query[] queries;
    private SystemType type;

    public System(SystemType type, String... queries) {
        this(type, Arrays.asList(queries).stream().map(str -> Query.parseQuery((String)str)).collect(Collectors.toList()).toArray(new Query[0]));
    }
    public System(SystemType type, Query... queries) {
        this.queries = queries;
        this.type = type;
    }
    
    public Query[] getQueries() { return queries; }
    public SystemType getType() { return type; }

    public abstract void execute(Entity[] queriedEntities, Resource[] queriedResources, float deltaTime);
}
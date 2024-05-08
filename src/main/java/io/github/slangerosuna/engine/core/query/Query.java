package io.github.slangerosuna.engine.core.query;

import java.util.List;

import io.github.slangerosuna.engine.core.ecs.Entity;
import io.github.slangerosuna.engine.core.ecs.Resource;
import io.github.slangerosuna.engine.core.query.queryTypes.*;

import java.util.ArrayList;
import java.util.Arrays;

public interface Query {
    public boolean matches(Entity entity);
    public boolean matches(Resource resource);
    public boolean overlaps(Query other);
    public Query[] children();

    public static boolean overlaps(Query query1, Query query2){
        return query1.overlaps(query2);
    }

    public static boolean overlaps(Query query1, Query[] queries){
        for (Query query : queries)
            if (query1.overlaps(query))
                return true;
        return false;
    }

    public static boolean overlaps(Query[] queries1, Query[] queries2){
        for (Query query1 : queries1)
            for (Query query2 : queries2)
                if (query1.overlaps(query2))
                    return true;
        return false;
    }

    public static boolean overlaps(Query[] queries1, Query query2){
        for (Query query1 : queries1)
            if (query1.overlaps(query2))
                return true;
        return false;
    }

    public static boolean overlaps(Query... queries) {
        for (int i = 0; i < queries.length; i++)
            for (int j = i + 1; j < queries.length; j++)
                if (queries[i].overlaps(queries[j]))
                    return true;
        return false;
    }

    public static Query parseQuery(String queryText){
        String[] queryParts = queryText.split(" ");
        List<String> queryPartsList = new ArrayList<String>(Arrays.asList(queryParts));
        return parseQuery(queryPartsList);
    }

    public static Query parseQuery(List<String> queryParts){
        if (queryParts.size() == 0) throw new IllegalArgumentException("Query is empty");
        if (queryParts.get(0).equals("SYNC"))
            return new SyncQuery();

        if (queryParts.get(0).equals("(")) {
            queryParts.remove(0);
            var query = parseQuery(queryParts);
            if (!queryParts.get(0).equals(")")) throw new IllegalArgumentException("Expected ')'");
            queryParts.remove(0);
            return query;
        }

        if (queryParts.get(0).equals("RESOURCE")) {
            queryParts.remove(0);
            return new ResourceQuery(queryParts.remove(0));
        }

        if (queryParts.get(0).equals("ENTITY")) {
            queryParts.remove(0);
            return new EntityQuery(parseQuery(queryParts));
        }

        if (queryParts.get(0).equals("OR")) {
            queryParts.remove(0);
            if (!queryParts.remove(0).equals("(")) throw new IllegalArgumentException("Expected '(' after OR");

            var queries = new ArrayList<Query>();
            while (!queryParts.get(0).equals(")")) {
                queries.add(parseQuery(queryParts));
            }
            queryParts.remove(0);

            return new OrQuery(queries.toArray(new Query[0]));
        }

        if (queryParts.get(0).equals("AND")) {
            queryParts.remove(0);
            if (!queryParts.remove(0).equals("(")) throw new IllegalArgumentException("Expected '(' after AND");

            var queries = new ArrayList<Query>();
            while (!queryParts.get(0).equals(")")) {
                queries.add(parseQuery(queryParts));
            }
            queryParts.remove(0);

            return new AndQuery(queries.toArray(new Query[0]));
        }

        if (queryParts.get(0).equals("HAS")) {
            queryParts.remove(0);
            return new HasQuery(queryParts.remove(0));
        }

        if (queryParts.get(0).equals("NOT")) {
            queryParts.remove(0);
            return new NotQuery(parseQuery(queryParts));
        }

        throw new IllegalArgumentException("Unknown query type: " + queryParts.get(0));
    }
}

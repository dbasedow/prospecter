package de.danielbasedow.prospecter.core.query.build;

import java.util.List;

public class Clause {
    public static enum ClauseType {
        AND,
        OR,
        NOT
    }

    private final ClauseType type;
    private final List<Condition> conditions;

    public Clause(ClauseType type, List<Condition> conditions) {
        this.type = type;
        this.conditions = conditions;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public ClauseType getType() {
        return type;
    }
}

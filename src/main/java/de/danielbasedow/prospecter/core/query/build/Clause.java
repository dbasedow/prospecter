package de.danielbasedow.prospecter.core.query.build;

import java.util.List;

public class Clause implements ClauseNode {

    public static enum ClauseType {
        AND,
        OR,
        NOT
    }

    private final ClauseType type;
    private final List<ClauseNode> subClauses;

    public Clause(ClauseType type, List<ClauseNode> clauses) {
        this.type = type;
        this.subClauses = clauses;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    public List<ClauseNode> getSubClauses() {
        return subClauses;
    }

    public ClauseType getType() {
        return type;
    }
}

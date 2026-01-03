package thesis.cozy.clausedb;

import thesis.common.IntSet;
import thesis.common.ValueType;

public final class Clause extends ValueType {

    public final IntSet literals;

    public Clause(IntSet literals) {
        this.literals = literals;
    }

}

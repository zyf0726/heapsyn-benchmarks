package thesis.guava.collect.table;

import thesis.common.ValueType;

public final class Value extends ValueType {

    public final int id;

    public Value(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

}

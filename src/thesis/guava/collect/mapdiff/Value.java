package thesis.guava.collect.mapdiff;

import thesis.common.ValueType;

public final class Value extends ValueType {

    public final int id;

    public Value(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Value(" + id + ")";
    }

}

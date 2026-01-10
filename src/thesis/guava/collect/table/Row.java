package thesis.guava.collect.table;

import thesis.common.IntMap;
import thesis.common.ValueType;

public final class Row extends ValueType {

    public final IntMap<Value> columnMap;

    public Row(IntMap<Value> columnMap) {
        this.columnMap = columnMap;
    }

}

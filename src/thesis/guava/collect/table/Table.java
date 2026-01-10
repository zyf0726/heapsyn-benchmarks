package thesis.guava.collect.table;

import thesis.common.IntMap;
import thesis.common.ValueType;

public final class Table {

    private IntMap<Row> rowMap;

    private Table() {
        this.rowMap = IntMap.empty();
    }

    public static Table create() {
        return new Table();
    }

    public void put(int rowKey, int columnKey, int valueId) {
        this.ensureInLegalState();
        Row row = IntMap.get(rowMap, rowKey);
        IntMap<Value> oldColumnMap = (row != null) ? row.columnMap : IntMap.empty();
        IntMap<Value> newColumnMap = IntMap.put(oldColumnMap, columnKey, new Value(valueId));
        rowMap = IntMap.put(rowMap, rowKey, new Row(newColumnMap));
    }

    public void remove(int rowKey, int columnKey) {
        this.ensureInLegalState();
        Row row = IntMap.get(rowMap, rowKey);
        IntMap<Value> oldColumnMap = (row != null) ? row.columnMap : IntMap.empty();
        IntMap<Value> newColumnMap = IntMap.remove(oldColumnMap, columnKey);
        if (IntMap.isEmpty(newColumnMap)) {
            rowMap = IntMap.remove(rowMap, rowKey);
        } else {
            rowMap = IntMap.put(rowMap, rowKey, new Row(newColumnMap));
        }
    }

    private void print() {
        int[] possibleKeys = {-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] possibleValues = {-1, 0, 1, 101, 201, 202, 304, 305, 306, 2001, 3006};
        for (int rKey : possibleKeys) {
            assert this.wellFormed(rKey);
            for (int cKey : possibleKeys) {
                assert this.removeIdempotent(rKey, cKey);
                for (int v1 : possibleValues) {
                    assert this.putRemoveIsRemove(rKey, cKey, v1);
                    assert this.removePutIsPut(rKey, cKey, v1);
                    for (int v2 : possibleValues) {
                        assert this.putPutIsPut(rKey, cKey, v1, v2);
                    }
                }
            }
        }
        for (IntMap<Row> curr = rowMap; !IntMap.isEmpty(curr); curr = curr.next) {
            int rowKey = curr.key;
            Row row = curr.value;
            System.out.printf("Row$%d: %s%n", rowKey, IntMap.toString(row.columnMap));
        }
        System.out.println("======================");
    }

    public static void main(String[] args) {
        Table table = Table.create();
        table.put(2, 1, 201);
        table.put(2, 2, 202);
        table.put(1, 1, 101);
        table.put(3, 6, 306);
        table.put(3, 4, 304);
        table.put(3, 5, 305);
        table.print();
        table.remove(1, 1);
        table.print();
        table.remove(3, 5);
        table.print();
        table.put(2, 1, 2001);
        table.put(3, 6, 3006);
        table.print();
        table.put(2, 2, 202);
        table.put(3, 4, 304);
        table.print();
        table.remove(2, 2);
        table.remove(3, 4);
        table.print();
        table.remove(2, 1);
        table.remove(3, 6);
        table.print();
    }

    // ====================== properties to be verified =======================

    private boolean wellFormed(int rowKey) {
        Row row = IntMap.get(rowMap, rowKey);
        if (row == null) {
            return true;
        } else {
            return !IntMap.isEmpty(row.columnMap);
        }
    }

    private Table copy() {
        Table newTable = new Table();
        newTable.rowMap = this.rowMap;
        return newTable;
    }

    private boolean equalTo(Table other) {
        IntMap<Row> rowMap1 = this.rowMap, rowMap2 = other.rowMap;
        while (!IntMap.isEmpty(rowMap1) && !IntMap.isEmpty(rowMap2)) {
            if (rowMap1.key != rowMap2.key) return false;
            IntMap<Value> columnMap1 = rowMap1.value.columnMap;
            IntMap<Value> columnMap2 = rowMap2.value.columnMap;
            while (!IntMap.isEmpty(columnMap1) && !IntMap.isEmpty(columnMap2)) {
                if (columnMap1.key != columnMap2.key) return false;
                if (columnMap1.value.id != columnMap2.value.id) return false;
                columnMap1 = columnMap1.next;
                columnMap2 = columnMap2.next;
            }
            if (!IntMap.isEmpty(columnMap1) || !IntMap.isEmpty(columnMap2)) return false;
            rowMap1 = rowMap1.next;
            rowMap2 = rowMap2.next;
        }
        return IntMap.isEmpty(rowMap1) && IntMap.isEmpty(rowMap2);
    }

    private boolean putRemoveIsRemove(int rowKey, int columnKey, int valueId) {
        Table t1 = this.copy(), t2 = this.copy();
        t1.put(rowKey, columnKey, valueId);
        t1.remove(rowKey, columnKey);
        t2.remove(rowKey, columnKey);
        return t1.equalTo(t2);
    }

    private boolean removePutIsPut(int rowKey, int columnKey, int valueId) {
        Table t1 = this.copy(), t2 = this.copy();
        t1.remove(rowKey, columnKey);
        t1.put(rowKey, columnKey, valueId);
        t2.put(rowKey, columnKey, valueId);
        return t1.equalTo(t2);
    }

    private boolean putPutIsPut(int rowKey, int columnKey, int valueId1, int valueId2) {
        Table t1 = this.copy(), t2 = this.copy();
        t1.put(rowKey, columnKey, valueId1);
        t1.put(rowKey, columnKey, valueId2);
        t2.put(rowKey, columnKey, valueId2);
        return t1.equalTo(t2);
    }

    private boolean removeIdempotent(int rowKey, int columnKey) {
        Table t1 = this.copy(), t2 = this.copy();
        t1.remove(rowKey, columnKey);
        t1.remove(rowKey, columnKey);
        t2.remove(rowKey, columnKey);
        return t1.equalTo(t2);
    }

    private ValueType inIllegalState = null;

    private void ensureInLegalState() {
        if (this.inIllegalState != null)
            throw new IllegalStateException();
    }

    private void markAsIllegal() {
        this.inIllegalState = new ValueType();
        this.rowMap = null;
    }

    public void verify$wellFormed(int rowKey) {
        if (!this.wellFormed(rowKey)) markAsIllegal();
    }

    public void verify$putRemoveIsRemove(int rowKey, int columnKey, int valueId) {
        if (!this.putRemoveIsRemove(rowKey, columnKey, valueId)) markAsIllegal();
    }

    public void verify$removePutIsPut(int rowKey, int columnKey, int valueId) {
        if (!this.removePutIsPut(rowKey, columnKey, valueId)) markAsIllegal();
    }

    public void verify$putPutIsPut(int rowKey, int columnKey, int valueId1, int valueId2) {
        if (!this.putPutIsPut(rowKey, columnKey, valueId1, valueId2)) markAsIllegal();
    }

    public void verify$removeIdempotent(int rowKey, int columnKey) {
        if (!this.removeIdempotent(rowKey, columnKey)) markAsIllegal();
    }

}

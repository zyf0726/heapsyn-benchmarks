package thesis.guava.collect.mapdiff;

import thesis.common.IntMap;
import thesis.common.ValueType;

public final class MapDifference {

    private IntMap<Value> leftMap;
    private IntMap<Value> rightMap;

    private MapDifference() {
        this.leftMap = IntMap.empty();
        this.rightMap = IntMap.empty();
    }

    public static MapDifference create() {
        return new MapDifference();
    }

    public void leftPut(int keyId, int valueId) {
        this.ensureInLegalState();
        this.leftMap = IntMap.put(this.leftMap, keyId, new Value(valueId));
    }

    public void rightPut(int keyId, int valueId) {
        this.ensureInLegalState();
        this.rightMap = IntMap.put(this.rightMap, keyId, new Value(valueId));
    }

    public void leftRemove(int keyId) {
        this.ensureInLegalState();
        this.leftMap = IntMap.remove(this.leftMap, keyId);
    }

    public void rightRemove(int keyId) {
        this.ensureInLegalState();
        this.rightMap = IntMap.remove(this.rightMap, keyId);
    }

    private void print() {
        assert this.equivalentEqualityTest() : "Equivalence test failed!";
        System.out.println("leftMap: " + IntMap.toString(this.leftMap));
        System.out.println("rightMap: " + IntMap.toString(this.rightMap));
        System.out.println("======================");
    }

    public static void main(String[] args) {
        MapDifference md = MapDifference.create();
        md.leftPut(1, 10);
        md.leftPut(2, 20);
        md.rightPut(3, 30);
        md.rightPut(2, 20);
        md.print();
        md.rightPut(4, 400);
        md.leftPut(1, 100);
        md.rightPut(2, 200);
        md.leftPut(0, 0);
        md.print();
        md.leftRemove(1);
        md.leftRemove(0);
        md.print();
        md.rightRemove(3);
        md.rightRemove(4);
        md.print();
        md.leftPut(2, 200);
        md.print();
        md.leftRemove(2);
        md.rightRemove(2);
        md.print();
    }

    // ====================== properties to be verified =======================

    private static IntMap<Value> entriesInCommon(IntMap<Value> leftMap, IntMap<Value> rightMap) {
        if (IntMap.isEmpty(leftMap) || IntMap.isEmpty(rightMap)) {
            return IntMap.empty();
        } else {
            int leftKey = leftMap.key, rightKey = rightMap.key;
            if (leftKey == rightKey) {
                IntMap<Value> rest = entriesInCommon(leftMap.next, rightMap.next);
                if (leftMap.value.id == rightMap.value.id) {
                    return IntMap.put(rest, leftKey, new Value(leftMap.value.id));
                } else {
                    return rest;
                }
            } else if (leftKey < rightKey) {
                return entriesInCommon(leftMap.next, rightMap);
            } else {
                return entriesInCommon(leftMap, rightMap.next);
            }
        }
    }

    private static IntMap<Value> entriesOnlyOnRight(IntMap<Value> leftMap, IntMap<Value> rightMap) {
        if (IntMap.isEmpty(rightMap)) {
            return IntMap.empty();
        } else if (IntMap.isEmpty(leftMap)) {
            return rightMap;
        } else {
            int leftKey = leftMap.key, rightKey = rightMap.key;
            if (leftKey == rightKey) {
                return entriesOnlyOnRight(leftMap.next, rightMap.next);
            } else if (leftKey < rightKey) {
                return entriesOnlyOnRight(leftMap.next, rightMap);
            } else {
                IntMap<Value> rest = entriesOnlyOnRight(leftMap, rightMap.next);
                return IntMap.put(rest, rightKey, new Value(rightMap.value.id));
            }
        }
    }

    private static boolean areEqual(IntMap<Value> map1, IntMap<Value> map2) {
        if (IntMap.isEmpty(map1) && IntMap.isEmpty(map2)) {
            return true;
        } else if (IntMap.isEmpty(map1) || IntMap.isEmpty(map2)) {
            return false;
        } else {
            if (map1.key != map2.key || map1.value.id != map2.value.id) {
                return false;
            } else {
                return areEqual(map1.next, map2.next);
            }
        }
    }

    private boolean equivalentEqualityTest() {
        boolean areEqual1 = areEqual(this.leftMap, this.rightMap);
        IntMap<Value> inCommon = entriesInCommon(this.leftMap, this.rightMap);
        IntMap<Value> rightOnly = entriesOnlyOnRight(this.leftMap, this.rightMap);
        boolean areEqual2 = (IntMap.size(leftMap) == IntMap.size(inCommon) - IntMap.size(rightOnly));
        return areEqual1 == areEqual2;
    }

    private ValueType inIllegalState = null;

    private void ensureInLegalState() {
        if (this.inIllegalState != null)
            throw new IllegalStateException();
    }

    private void markAsIllegal() {
        this.inIllegalState = new ValueType();
        this.leftMap = null;
        this.rightMap = null;
    }

    public void verify$equivalentEqualityTest() {
        if (!this.equivalentEqualityTest()) markAsIllegal();
    }

}

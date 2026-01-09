package thesis.guava.collect.bimap;

import thesis.common.IntMap;
import thesis.common.ValueType;

public final class BiMap {

    private IntMap<Value> map;

    private BiMap() {
        this.map = IntMap.empty();
    }

    public static BiMap create() {
        return new BiMap();
    }

    private static IntMap<Value> __forcePut(IntMap<Value> map, int keyId, int valueId) {
        if (IntMap.isEmpty(map)) {
            return IntMap.put(IntMap.empty(), keyId, new Value(valueId));
        } else {
            int k = map.key, v = map.value.id;
            if (k == keyId || v == valueId) {
                return __forcePut(map.next, keyId, valueId);
            } else {
                return IntMap.put(__forcePut(map.next, keyId, valueId), k, new Value(v));
            }
        }
    }

    public void forcePut(int keyId, int valueId) {
        this.ensureInLegalState();
        this.map = __forcePut(this.map, keyId, valueId);
    }

    private static IntMap<Value> __remove(IntMap<Value> map, int keyId) {
        if (IntMap.isEmpty(map)) {
            return IntMap.empty();
        } else {
            int k = map.key, v = map.value.id;
            if (k == keyId) {
                return __remove(map.next, keyId);
            } else {
                return IntMap.put(__remove(map.next, keyId), k, new Value(v));
            }
        }
    }

    public void remove(int keyId) {
        this.ensureInLegalState();
        this.map = __remove(this.map, keyId);
    }

    private static IntMap<Value> __invRemove(IntMap<Value> map, int valueId) {
        if (IntMap.isEmpty(map)) {
            return IntMap.empty();
        } else {
            int k = map.key, v = map.value.id;
            if (v == valueId) {
                return __invRemove(map.next, valueId);
            } else {
                return IntMap.put(__invRemove(map.next, valueId), k, new Value(v));
            }
        }
    }

    public void invRemove(int valueId) {
        this.ensureInLegalState();
        this.map = __invRemove(this.map, valueId);
    }

    private void print() {
        assert this.wellFormed();
        System.out.print("contents: { ");
        for (IntMap<Value> curr = map; !IntMap.isEmpty(curr); curr = curr.next) {
            int k = curr.key, v = curr.value.id;
            System.out.print(k + " -> " + v + ", ");
        }
        System.out.println("}");
    }

    public static void main(String[] args) {
        BiMap bimap = BiMap.create();
        bimap.forcePut(2, 200);
        bimap.forcePut(1, 100);
        bimap.print(); // (1 -> 100), (2 -> 200)
        bimap.forcePut(1, -100);
        bimap.print(); // (1 -> -100), (2 -> 200)
        bimap.forcePut(-2, 200);
        bimap.print(); // (-2 -> 200), (1 -> -100)
        bimap.forcePut(3, 300);
        bimap.print(); // (-2 -> 200), (1 -> -100), (3 -> 300)
        bimap.forcePut(3, 300); // no effect
        bimap.print(); // (-2 -> 200), (1 -> -100), (3 -> 300)
        bimap.remove(1);
        bimap.print(); // (-2 -> 200), (3 -> 300)
        bimap.invRemove(300);
        bimap.print(); // (-2 -> 200)
        bimap.remove(-2);
        bimap.print(); // empty
        bimap.remove(42); // no effect
        bimap.print(); // empty
    }

    // ====================== properties to be verified =======================

    private boolean wellFormed() {
        for (IntMap<Value> curr1 = map; !IntMap.isEmpty(curr1); curr1 = curr1.next) {
            int v1 = curr1.value.id;
            for (IntMap<Value> curr2 = curr1.next; !IntMap.isEmpty(curr2); curr2 = curr2.next) {
                int v2 = curr2.value.id;
                if (v1 == v2) return false;
            }
        }
        return true;
    }

    private BiMap copy() {
        BiMap newBiMap = BiMap.create();
        newBiMap.map = this.map;
        return newBiMap;
    }

    private boolean equalTo(BiMap other) {
        IntMap<Value> curr1 = this.map, curr2 = other.map;
        while (!IntMap.isEmpty(curr1) && !IntMap.isEmpty(curr2)) {
            if (curr1.key != curr2.key) return false;
            if (curr1.value.id != curr2.value.id) return false;
            curr1 = curr1.next;
            curr2 = curr2.next;
        }
        return IntMap.isEmpty(curr1) && IntMap.isEmpty(curr2);
    }

    private boolean equivalentRemove(int keyId) {
        BiMap m1 = this.copy(), m2 = this.copy();
        m1.remove(keyId);
        m2.map = IntMap.remove(m2.map, keyId);
        return m1.equalTo(m2);
    }

    private boolean equivalentForcePut(int keyId, int valueId) {
        BiMap m1 = this.copy(), m2 = this.copy();
        m1.forcePut(keyId, valueId);
        m2.remove(keyId);
        m2.invRemove(valueId);
        m2.map = IntMap.put(m2.map, keyId, new Value(valueId));
        return m1.equalTo(m2);
    }

    private ValueType inIllegalState = null;

    private void ensureInLegalState() {
        if (this.inIllegalState != null)
            throw new IllegalStateException();
    }

    private void markAsIllegal() {
        this.inIllegalState = new ValueType();
        this.map = null;
    }

    public void verify$wellFormed() {
        if (!this.wellFormed()) markAsIllegal();
    }

    public void verify$equivalentRemove(int keyId) {
        if (!this.equivalentRemove(keyId)) markAsIllegal();
    }

    public void verify$equivalentForcePut(int keyId, int valueId) {
        if (!this.equivalentForcePut(keyId, valueId)) markAsIllegal();
    }

}

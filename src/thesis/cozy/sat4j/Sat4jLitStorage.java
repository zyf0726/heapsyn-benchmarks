package thesis.cozy.sat4j;

import thesis.common.IntMap;

import java.util.NoSuchElementException;

public final class Sat4jLitStorage {

    private IntMap<Record> records;

    private Sat4jLitStorage() {
        this.records = IntMap.empty();
    }

    public static Sat4jLitStorage create() {
        return new Sat4jLitStorage();
    }

    public void add(int recId, int var, int level, int reason,
                    int posWatches, int negWatches, int undos) {
        if (IntMap.containsKey(records, recId))
            throw new IllegalArgumentException("duplicate record: " + recId);
        for (IntMap<Record> curr = records; !IntMap.isEmpty(curr); curr = curr.next) {
            int rid = curr.key;
            Record r = curr.value;
            if (r.var == var) {
                String message = "record of var " + var + " already exists: " + rid;
                throw new IllegalArgumentException(message);
            }
        }
        Record record = new Record(var, level, reason, posWatches, negWatches, undos);
        records = IntMap.put(records, recId, record);
    }

    public void remove(int recId) {
        records = IntMap.remove(records, recId);
    }

    public void updateLevel(int recId, int newLevel) {
        Record oldRecord = IntMap.getOrThrow(records, recId);
        Record newRecord = new Record(oldRecord.var, newLevel, oldRecord.reason,
                oldRecord.posWatches, oldRecord.negWatches, oldRecord.undos);
        records = IntMap.put(records, recId, newRecord);
    }

    public void updateReason(int recId, int newReason) {
        Record oldRecord = IntMap.getOrThrow(records, recId);
        Record newRecord = new Record(oldRecord.var, oldRecord.level, newReason,
                oldRecord.posWatches, oldRecord.negWatches, oldRecord.undos);
        records = IntMap.put(records, recId, newRecord);
    }

    private void print() {
        for (IntMap<Record> curr = records; !IntMap.isEmpty(curr); curr = curr.next) {
            int recId = curr.key;
            Record r = curr.value;
            System.out.printf("Record$%d: var=%d, level=%d, reason=%d, " +
                              "posWatches=%d, negWatches=%d, undos=%d%n",
                    recId, r.var, r.level, r.reason,
                    r.posWatches, r.negWatches, r.undos);
        }
        System.out.println("==========");
    }

    public static void main(String[] args) {
        Sat4jLitStorage storage = Sat4jLitStorage.create();
        storage.add(1, 10, 0, -1, 100, 200, 0);
        storage.add(2, 20, 1, 5, 110, 210, 1);
        storage.print();
        storage.updateLevel(1, 1);
        storage.updateLevel(2, 0);
        storage.print();
        storage.updateReason(2, -5);
        storage.updateReason(1, 1);
        storage.print();
        storage.remove(1);
        storage.remove(1);
        storage.print();
        try {
            storage = Sat4jLitStorage.create();
            storage.add(1, 10, 0, -1, 100, 200, 0);
            storage.add(1, 10, 0, -1, 100, 200, 0);
            System.out.println("ERROR: duplicate record insertion not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            storage = Sat4jLitStorage.create();
            storage.add(1, 10, 0, -1, 100, 200, 0);
            storage.add(2, 10, 1, 5, 110, 210, 1);
            System.out.println("ERROR: duplicate var record insertion not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            storage = Sat4jLitStorage.create();
            storage.updateLevel(3, 5);
            System.out.println("ERROR: updating non-existing record not detected");
        } catch (NoSuchElementException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            storage = Sat4jLitStorage.create();
            storage.updateReason(4, 5);
            System.out.println("ERROR: updating non-existing record not detected");
        } catch (NoSuchElementException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
    }

}

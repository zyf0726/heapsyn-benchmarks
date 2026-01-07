package thesis.cozy.ztopo;

import thesis.common.IntMap;

import java.util.NoSuchElementException;

public final class ZTopoCache {

    private IntMap<Entry> entries;

    private ZTopoCache() {
        this.entries = IntMap.empty();
    }

    public static ZTopoCache create() {
        return new ZTopoCache();
    }

    public void add(int entryId, int key, int memSize, int diskSize,
                    int state, boolean inUse) {
        if (IntMap.containsKey(entries, entryId))
            throw new IllegalArgumentException("duplicate entry: " + entryId);
        for (IntMap<Entry> curr = entries; !IntMap.isEmpty(curr); curr = curr.next) {
            int eid = curr.key;
            Entry e = curr.value;
            if (e.key == key) {
                String message = "entry of key " + key + " already exists: " + eid;
                throw new IllegalArgumentException(message);
            }
        }
        Entry entry = new Entry(key, memSize, diskSize, state, inUse);
        entries = IntMap.put(entries, entryId, entry);
    }

    public void remove(int entryId) {
        entries = IntMap.remove(entries, entryId);
    }

    public void updateState(int entryId, int newState) {
        Entry oldEntry = IntMap.getOrThrow(entries, entryId);
        Entry newEntry = new Entry(oldEntry.key, oldEntry.memSize, oldEntry.diskSize,
                newState, oldEntry.inUse);
        entries = IntMap.put(entries, entryId, newEntry);
    }

    public void updateInUse(int entryId, boolean newInUse) {
        Entry oldEntry = IntMap.getOrThrow(entries, entryId);
        Entry newEntry = new Entry(oldEntry.key, oldEntry.memSize, oldEntry.diskSize,
                oldEntry.state, newInUse);
        entries = IntMap.put(entries, entryId, newEntry);
    }

    private void print() {
        for (IntMap<Entry> curr = entries; !IntMap.isEmpty(curr); curr = curr.next) {
            int entryId = curr.key;
            Entry e = curr.value;
            System.out.printf("Entry$%d: key=%d, memSize=%d, diskSize=%d, state=%d, inUse=%b%n",
                    entryId, e.key, e.memSize, e.diskSize, e.state, e.inUse);
        }
        System.out.println("==========");
    }

    public static void main(String[] args) {
        ZTopoCache cache = ZTopoCache.create();
        cache.add(2, 200, 1024, 2048, 1, false);
        cache.add(1, 100, 2048, 4096, 2, true);
        cache.print();
        cache.updateState(1, 3);
        cache.updateInUse(2, false);
        cache.updateInUse(2, true);
        cache.print();
        cache.updateState(1, 3);
        cache.remove(2);
        cache.print();
        try {
            cache = ZTopoCache.create();
            cache.add(1, 100, 2048, 4096, 2, true);
            cache.add(1, 200, 1024, 2048, 1, false);
            System.out.println("ERROR: duplicate entry not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            cache = ZTopoCache.create();
            cache.add(1, 100, 2048, 4096, 2, true);
            cache.add(2, 100, 1024, 2048, 1, false);
            System.out.println("ERROR: duplicate key insertion not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            cache = ZTopoCache.create();
            cache.updateState(3, 5);
            System.out.println("ERROR: updating non-existent entry not detected");
        } catch (NoSuchElementException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            cache = ZTopoCache.create();
            cache.updateInUse(4, true);
            System.out.println("ERROR: updating non-existent entry not detected");
        } catch (NoSuchElementException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
    }

}

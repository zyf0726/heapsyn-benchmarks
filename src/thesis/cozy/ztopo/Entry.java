package thesis.cozy.ztopo;

import thesis.common.ValueType;

public final class Entry extends ValueType {

    public final int key;
    public final int memSize;
    public final int diskSize;
    public final int state;
    public final boolean inUse;

    public Entry(int key, int memSize, int diskSize, int state, boolean inUse) {
        this.key = key;
        this.memSize = memSize;
        this.diskSize = diskSize;
        this.state = state;
        this.inUse = inUse;
    }

}

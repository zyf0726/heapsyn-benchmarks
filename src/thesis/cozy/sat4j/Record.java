package thesis.cozy.sat4j;

import thesis.common.ValueType;

public final class Record extends ValueType {

    public final int var;
    public final int level;
    public final int reason;
    public final int posWatches;
    public final int negWatches;
    public final int undos;

    public Record(int var, int level, int reason,
                  int posWatches, int negWatches, int undos) {
        this.var = var;
        this.level = level;
        this.reason = reason;
        this.posWatches = posWatches;
        this.negWatches = negWatches;
        this.undos = undos;
    }

}

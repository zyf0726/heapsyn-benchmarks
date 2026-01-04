package thesis.cozy.lobsters;

import thesis.common.IntSet;
import thesis.common.ValueType;

public final class Story extends ValueType {

    public final int mergedId;
    public final boolean isExpired;
    public final int createdAt;
    public final IntSet hiddenToUsers;
    public final IntSet tags;
    public final IntSet votes;

    public Story(int mergedId, boolean isExpired, int createdAt,
                 IntSet hiddenToUsers, IntSet tags, IntSet votes) {
        this.mergedId = mergedId;
        this.isExpired = isExpired;
        this.createdAt = createdAt;
        this.hiddenToUsers = hiddenToUsers;
        this.tags = tags;
        this.votes = votes;
    }

}

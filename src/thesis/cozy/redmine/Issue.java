package thesis.cozy.redmine;

import thesis.common.IntSet;
import thesis.common.ValueType;

public final class Issue extends ValueType {

    public final int project;
    public final IntSet statuses;
    public final int assignedTo;

    public Issue(int project, IntSet statuses, int assignedTo) {
        this.project = project;
        this.statuses = statuses;
        this.assignedTo = assignedTo;
    }

}

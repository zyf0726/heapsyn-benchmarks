package thesis.cozy.openfire;

import thesis.common.IntSet;
import thesis.common.ValueType;

public final class Group extends ValueType {

    public static final int ONLY_GROUP = 1, EVERYBODY = 2;

    public final int rosterMode;
    public final IntSet children;
    public final IntSet members;

    public Group(int rosterMode, IntSet children, IntSet members) {
        this.rosterMode = rosterMode;
        this.children = children;
        this.members = members;
    }

}

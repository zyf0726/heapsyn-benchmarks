package thesis.cozy.openfire;

import thesis.common.ValueType;

public final class RosterItem extends ValueType {

    public final int user;
    public final int target;

    public RosterItem(int user, int target) {
        this.user = user;
        this.target = target;
    }

}

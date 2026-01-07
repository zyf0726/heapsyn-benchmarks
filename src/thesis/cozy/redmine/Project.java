package thesis.cozy.redmine;

import thesis.common.IntSet;
import thesis.common.ValueType;

public final class Project extends ValueType {

    public final int status;
    public final IntSet modules;

    public Project(int status, IntSet modules) {
        this.status = status;
        this.modules = modules;
    }

}

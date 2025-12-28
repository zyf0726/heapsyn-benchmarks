package thesis.demo;

import thesis.common.ValueType;

public final class Node extends ValueType {

    public final int indeg, outdeg;

    public Node(int indeg, int outdeg) {
        this.indeg = indeg;
        this.outdeg = outdeg;
    }

}

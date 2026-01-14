package thesis.guava.graph.graph;

import thesis.common.IntSet;
import thesis.common.ValueType;

public final class Node extends ValueType {

    public final IntSet successors;

    public Node(IntSet successors) {
        this.successors = successors;
    }

}

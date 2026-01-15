package thesis.guava.graph.network;

import thesis.common.ValueType;

public final class Edge extends ValueType {

    public final int src;
    public final int tgt;

    public Edge(int src, int tgt) {
        this.src = src;
        this.tgt = tgt;
    }

}

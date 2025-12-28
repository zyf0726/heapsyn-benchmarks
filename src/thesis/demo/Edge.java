package thesis.demo;

import thesis.common.ValueType;

public final class Edge extends ValueType {

    public final int src, tgt;

    public Edge(int src, int tgt) {
        this.src = src;
        this.tgt = tgt;
    }

}

package thesis.demo;

import thesis.common.IntMap;
import thesis.common.ValueType;

import static thesis.common.IntMap.*;

public final class Graph1 {

    private IntMap<Node> nodes;
    private IntMap<Edge> edges;

    private Graph1() {
        this.nodes = empty();
        this.edges = empty();
    }

    public static Graph1 create() {
        return new Graph1();
    }

    public void addNode(int xid) {
        this.ensureInLegalState();
        if (containsKey(nodes, xid))
            throw new IllegalArgumentException("duplicated node: " + xid);
        nodes = put(nodes, xid, new Node(0, 0));
    }

    public void addEdge(int eid, int xid, int yid) {
        this.ensureInLegalState();
        if (xid == yid)
            throw new IllegalArgumentException("self-loop disallowed: " + xid + "->" + yid);
        if (containsKey(edges, eid))
            throw new IllegalArgumentException("duplicated edge: " + eid);
        if (!containsKey(nodes, xid)) addNode(xid);
        if (!containsKey(nodes, yid)) addNode(yid);
        Edge e = new Edge(xid, yid);
        edges = put(edges, eid, e);
        Node x = get(nodes, xid);
        nodes = put(nodes, xid, new Node(x.indeg, x.outdeg + 1));
        Node y = get(nodes, yid);
        nodes = put(nodes, yid, new Node(y.indeg + 1, y.outdeg));
    }

    private void print() {
        for (IntMap<Node> curr = nodes; curr != null; curr = curr.next) {
            int nid = curr.key;
            Node n = curr.value;
            assert this.consistentIndeg(nid);
            assert this.consistentOutdeg(nid);
            System.out.printf("Node$%d: indeg=%d, outdeg=%d\n", nid, n.indeg, n.outdeg);
        }
        for (IntMap<Edge> curr = edges; curr != null; curr = curr.next) {
            int eid = curr.key;
            Edge e = curr.value;
            System.out.printf("Edge$%d: src=%d, tgt=%d\n", eid, e.src, e.tgt);
        }
        System.out.println("==========");
    }

    public static void main(String[] args) {
        Graph1 g = Graph1.create();
        g.print();
        g.addNode(0);
        g.print();
        g.addNode(1);
        g.print();
        g.addEdge(0, 0, 1);
        g.print();
        g.addEdge(1, 0, 1);
        g.print();
        g.addEdge(2, 0, 2);
        g.print();
        g.addEdge(3, 2, 1);
        g.print();
        System.out.println();
        try {
            g = Graph1.create();
            g.addEdge(0, 0, 0);
            System.out.println("Error: self-loop not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            g = Graph1.create();
            g.addNode(0);
            g.addNode(1);
            g.addNode(0);
            System.out.println("Error: duplicated node not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            g = Graph1.create();
            g.addEdge(0, 0, 1);
            g.addEdge(0, 0, 1);
            System.out.println("Error: duplicated edge not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            g = Graph1.create();
            g.addEdge(0, 0, 1);
            g.addEdge(0, 1, 0);
            System.out.println("Error: duplicated edge not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
    }

    // ====================== properties to be verified =======================

    private boolean consistentIndeg(int xid) {
        int count = 0;
        for (IntMap<Edge> curr = edges; curr != null; curr = curr.next) {
            Edge e = curr.value;
            if (e.tgt == xid) ++count;
        }
        Node x = get(nodes, xid);
        return x.indeg == count;
    }

    private boolean consistentOutdeg(int xid) {
        int count = 0;
        for (IntMap<Edge> curr = edges; curr != null; curr = curr.next) {
            Edge e = curr.value;
            if (e.src == xid) ++count;
        }
        Node x = get(nodes, xid);
        return x.outdeg == count;
    }

    private boolean correctness(int xid, int d) {
        Node x = get(nodes, xid);
        boolean cond1 = (d == x.indeg + x.outdeg);
        int count = 0;
        for (IntMap<Edge> curr = edges; curr != null; curr = curr.next) {
            Edge e = curr.value;
            if (e.src == xid || e.tgt == xid) ++count;
        }
        boolean cond2 = (d == count);
        return cond1 == cond2;
    }

    private ValueType inIllegalState = null;

    private void ensureInLegalState() {
        if (this.inIllegalState != null)
            throw new IllegalStateException();
    }

    private void markAsIllegal() {
        this.inIllegalState = new ValueType();
        this.nodes = null;
        this.edges = null;
    }

    public void verify$consistentIndeg(int xid) {
        if (!this.consistentIndeg(xid)) markAsIllegal();
    }

    public void verify$consistentOutdeg(int xid) {
        if (!this.consistentOutdeg(xid)) markAsIllegal();
    }

    public void verify$correctness(int xid, int d) {
        if (!this.correctness(xid, d)) markAsIllegal();
    }

}

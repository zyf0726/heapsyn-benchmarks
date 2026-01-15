package thesis.guava.graph.network;

import thesis.common.IntMap;
import thesis.common.IntSet;
import thesis.common.ValueType;

public final class MutableNetwork {

    private IntSet nodes;
    private IntMap<Edge> edges;

    private MutableNetwork() {
        this.nodes = IntSet.empty();
        this.edges = IntMap.empty();
    }

    public static MutableNetwork create() {
        return new MutableNetwork();
    }

    public void addNode(int nodeId) {
        this.ensureInLegalState();
        nodes = IntSet.add(nodes, nodeId);
    }

    public void addEdge(int edgeId, int srcId, int tgtId) {
        this.ensureInLegalState();
        if (IntMap.containsKey(edges, edgeId))
            throw new IllegalArgumentException("duplicate edge: " + edgeId);
        nodes = IntSet.add(nodes, srcId);
        nodes = IntSet.add(nodes, tgtId);
        edges = IntMap.put(edges, edgeId, new Edge(srcId, tgtId));
    }

    private static IntMap<Edge> removeIncidentEdges(IntMap<Edge> edges, int nodeId) {
        if (IntMap.isEmpty(edges)) return IntMap.empty();
        IntMap<Edge> next = removeIncidentEdges(edges.next, nodeId);
        int edgeId = edges.key;
        Edge edge = edges.value;
        if (edge.src == nodeId || edge.tgt == nodeId) {
            return next;
        } else {
            return IntMap.put(next, edgeId, edge);
        }
    }

    public void removeNode(int nodeId) {
        this.ensureInLegalState();
        nodes = IntSet.remove(nodes, nodeId);
        edges = removeIncidentEdges(edges, nodeId);
    }

    public void removeEdge(int edgeId) {
        this.ensureInLegalState();
        edges = IntMap.remove(edges, edgeId);
    }

    private void print() {
        System.out.println("nodes: " + IntSet.toString(nodes));
        for (IntMap<Edge> curr = edges; !IntMap.isEmpty(curr); curr = curr.next) {
            int edgeId = curr.key;
            Edge edge = curr.value;
            assert this.validSource(edgeId);
            assert this.validTarget(edgeId);
            System.out.println("Edge$" + edgeId + ": " + edge.src + " -> " + edge.tgt);
        }
        System.out.println("======================");
    }

    public static void main(String[] args) {
        MutableNetwork network = MutableNetwork.create();
        network.addNode(1);
        network.addNode(2);
        network.addNode(3);
        network.print();
        network.addEdge(10, 1, 2);
        network.addEdge(11, 2, 1);
        network.addEdge(12, 2, 1);
        network.addEdge(13, 1, 3);
        network.addEdge(14, 1, 3);
        network.addEdge(15, 2, 3);
        network.addEdge(16, 3, 3);
        network.print();
        network.addNode(2);
        network.print();
        network.removeEdge(12);
        network.removeEdge(100);
        network.addEdge(17, 1, 4);
        network.addEdge(100, 5, 2);
        network.print();
        network.removeNode(3);
        network.print();
        network.removeNode(5);
        network.print();
        network.removeEdge(11);
        network.print();
        network.removeNode(2);
        network.print();
        network.removeNode(1);
        network.print();
        try {
            network = MutableNetwork.create();
            network.addEdge(10, 1, 2);
            network.addEdge(10, 2, 3);
            System.out.println("ERROR: expected exception for duplicate edge ID");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
    }

    // ====================== properties to be verified =======================

    private boolean validSource(int edgeId) {
        Edge edge = IntMap.get(edges, edgeId);
        if (edge == null) return true;
        return IntSet.contains(nodes, edge.src);
    }

    private boolean validTarget(int edgeId) {
        Edge edge = IntMap.get(edges, edgeId);
        if (edge == null) return true;
        return IntSet.contains(nodes, edge.tgt);
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

    public void verify$validSource(int edgeId) {
        if (!this.validSource(edgeId)) this.markAsIllegal();
    }

    public void verify$validTarget(int edgeId) {
        if (!this.validTarget(edgeId)) this.markAsIllegal();
    }

}

package thesis.guava.graph.graph;

import thesis.common.IntMap;
import thesis.common.IntSet;
import thesis.common.ValueType;

public final class MutableGraph {

    private IntMap<Node> nodes;

    private MutableGraph() {
        this.nodes = IntMap.empty();
    }

    public static MutableGraph create() {
        return new MutableGraph();
    }

    public void addNode(int nodeId) {
        this.ensureInLegalState();
        if (!IntMap.containsKey(nodes, nodeId)) {
            nodes = IntMap.put(nodes, nodeId, new Node(IntSet.empty()));
        }
    }

    public void putEdge(int fromId, int toId) {
        this.ensureInLegalState();
        Node fromNode = IntMap.get(nodes, fromId);
        IntSet oldSuccessors = (fromNode != null) ? fromNode.successors : IntSet.empty();
        IntSet newSuccessors = IntSet.add(oldSuccessors, toId);
        nodes = IntMap.put(nodes, fromId, new Node(newSuccessors));
        if (!IntMap.containsKey(nodes, toId)) {
            nodes = IntMap.put(nodes, toId, new Node(IntSet.empty()));
        }
    }

    private static IntMap<Node> removeFromSuccessors(IntMap<Node> nodes, int targetId) {
        if (IntMap.isEmpty(nodes)) return IntMap.empty();
        IntMap<Node> next = removeFromSuccessors(nodes.next, targetId);
        int nodeId = nodes.key;
        Node oldNode = nodes.value;
        IntSet oldSuccessors = oldNode.successors;
        IntSet newSuccessors = IntSet.remove(oldSuccessors, targetId);
        Node newNode = new Node(newSuccessors);
        return IntMap.put(next, nodeId, newNode);
    }

    public void removeNode(int nodeId) {
        this.ensureInLegalState();
        nodes = IntMap.remove(nodes, nodeId);
        nodes = removeFromSuccessors(nodes, nodeId);
    }

    public void removeEdge(int fromId, int toId) {
        this.ensureInLegalState();
        Node fromNode = IntMap.get(nodes, fromId);
        if (fromNode != null) {
            IntSet oldSuccessors = fromNode.successors;
            IntSet newSuccessors = IntSet.remove(oldSuccessors, toId);
            nodes = IntMap.put(nodes, fromId, new Node(newSuccessors));
        }
    }

    private void print() {
        for (IntMap<Node> curr = nodes; !IntMap.isEmpty(curr); curr = curr.next) {
            int nodeId = curr.key;
            Node node = curr.value;
            assert this.validSuccessors(nodeId);
            System.out.printf("Node$%d -> %s%n", nodeId, IntSet.toString(node.successors));
        }
        System.out.println("======================");
    }

    public static void main(String[] args) {
        MutableGraph graph = MutableGraph.create();
        graph.addNode(1);
        graph.addNode(2);
        graph.addNode(3);
        graph.print();
        graph.putEdge(1, 2);
        graph.putEdge(2, 1);
        graph.putEdge(1, 3);
        graph.putEdge(2, 3);
        graph.putEdge(3, 3);
        graph.print();
        graph.addNode(2);
        graph.print();
        graph.removeEdge(1, 3);
        graph.removeEdge(5, 2);
        graph.putEdge(1, 4);
        graph.putEdge(5, 2);
        graph.print();
        graph.removeNode(3);
        graph.print();
        graph.removeNode(5);
        graph.print();
        graph.removeEdge(2, 1);
        graph.print();
        graph.removeNode(2);
        graph.print();
        graph.removeNode(1);
        graph.print();
    }

    // ====================== properties to be verified =======================

    private boolean validSuccessors(int nodeId) {
        Node node = IntMap.get(nodes, nodeId);
        if (node == null) return true;
        for (IntSet curr = node.successors; !IntSet.isEmpty(curr); curr = curr.next) {
            int succId = curr.elem;
            if (!IntMap.containsKey(nodes, succId)) {
                return false;
            }
        }
        return true;
    }

    private ValueType inIllegalState = null;

    private void ensureInLegalState() {
        if (this.inIllegalState != null)
            throw new IllegalStateException();
    }

    private void markAsIllegal() {
        this.inIllegalState = new ValueType();
        this.nodes = null;
    }

    public void verify$validSuccessors(int nodeId) {
        if (!this.validSuccessors(nodeId)) this.markAsIllegal();
    }

}

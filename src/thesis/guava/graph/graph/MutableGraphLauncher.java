package thesis.guava.graph.graph;

import thesis.common.Launcher;

public final class MutableGraphLauncher extends Launcher {

    private static final String targetClassName = "thesis.guava.graph.graph.MutableGraph";
    private static final int maxSeqLength       = 4;
    private static final String logFilePath     = "tmp/thesis/guava/mutgraph.txt";
    private static final String hexFilePath     = "HEXsettings/thesis/guava/mutgraph.jbse";

    public MutableGraphLauncher() {
        super(targetClassName, maxSeqLength, logFilePath);
        super.setScope("thesis.guava.graph.graph.MutableGraph", 1, 1);
        super.setScope("thesis.guava.graph.graph.Node", 5, 6);
        super.setScope("thesis.common.IntMap", 5, 6);
        super.setScope("thesis.common.IntSet", 5, 6);
        super.setHexFilePaths(hexFilePath);
    }

    public static void main(String[] args) {
        MutableGraphLauncher launcher = new MutableGraphLauncher();
        launcher.buildGraph(false);
    }

}

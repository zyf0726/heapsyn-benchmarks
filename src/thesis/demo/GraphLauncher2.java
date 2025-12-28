package thesis.demo;

import thesis.common.Launcher;

public final class GraphLauncher2 extends Launcher {

    private static final String targetClassName    = "thesis.demo.Graph2";
    private static final int maxSeqLength          = 8;
    private static final String logFilePath        = "tmp/thesis/demo/graph2.txt";
    private static final String hexFilePath        = "HEXsettings/thesis/demo/graph2.jbse";

    public GraphLauncher2() {
        super(targetClassName, maxSeqLength, logFilePath);
        super.setScope("thesis.demo.Graph2", 1, 1);
        super.setScope("thesis.demo.Node", 5, 6);
        super.setScope("thesis.demo.Edge", 5, 6);
        super.setScope("thesis.common.IntMap", 5, 6);
        super.setHexFilePaths(hexFilePath);
    }

    public static void main(String[] args) {
        GraphLauncher2 launcher = new GraphLauncher2();
        launcher.buildGraph(false);
    }

}

package thesis.demo;

import thesis.common.Launcher;

public final class GraphLauncher1 extends Launcher {

    private static final String targetClassName    = "thesis.demo.Graph1";
    private static final int maxSeqLength          = 8;
    private static final String logFilePath        = "tmp/thesis/demo/graph1.txt";
    private static final String hexFilePath        = "HEXsettings/thesis/demo/graph1.jbse";

    public GraphLauncher1() {
        super(targetClassName, maxSeqLength, logFilePath);
        super.setScope("thesis.demo.Graph1", 1, 1);
        super.setScope("thesis.demo.Node", 5, 6);
        super.setScope("thesis.demo.Edge", 5, 6);
        super.setScope("thesis.common.IntMap", 5, 6);
        super.setHexFilePaths(hexFilePath);
    }

    public static void main(String[] args) {
        GraphLauncher1 launcher = new GraphLauncher1();
        launcher.buildGraph(false);
    }

}

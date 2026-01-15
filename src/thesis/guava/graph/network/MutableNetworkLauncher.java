package thesis.guava.graph.network;

import thesis.common.Launcher;

public final class MutableNetworkLauncher extends Launcher {

    private static final String targetClassName = "thesis.guava.graph.network.MutableNetwork";
    private static final int maxSeqLength       = 4;
    private static final String logFilePath     = "tmp/thesis/guava/mutnet.txt";

    public MutableNetworkLauncher() {
        super(targetClassName, maxSeqLength, logFilePath);
        super.setScope("thesis.guava.graph.network.MutableNetwork", 1, 1);
        super.setScope("thesis.guava.graph.network.Edge", 5, 6);
        super.setScope("thesis.common.IntMap", 5, 6);
        super.setScope("thesis.common.IntSet", 5, 6);
        super.setHexFilePaths();
    }

    public static void main(String[] args) {
        MutableNetworkLauncher launcher = new MutableNetworkLauncher();
        launcher.buildGraph(false);
    }

}

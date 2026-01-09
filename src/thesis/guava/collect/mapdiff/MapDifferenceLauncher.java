package thesis.guava.collect.mapdiff;

import thesis.common.Launcher;

public final class MapDifferenceLauncher extends Launcher {

    private static final String targetClassName = "thesis.guava.collect.mapdiff.MapDifference";
    private static final int maxSeqLength       = 8;
    private static final String logFilePath     = "tmp/thesis/guava/mapdiff.txt";

    public MapDifferenceLauncher() {
        super(targetClassName, maxSeqLength, logFilePath);
        super.setScope("thesis.guava.collect.mapdiff.MapDifference", 1, 1);
        super.setScope("thesis.guava.collect.mapdiff.Value", 5, 6);
        super.setScope("thesis.common.IntMap", 5, 6);
        super.setHexFilePaths();
    }

    public static void main(String[] args) {
        MapDifferenceLauncher launcher = new MapDifferenceLauncher();
        launcher.buildGraph(false);
    }

}

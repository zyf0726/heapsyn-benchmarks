package thesis.guava.collect.bimap;

import thesis.common.Launcher;

public final class BiMapLauncher extends Launcher {

    private static final String targetClassName = "thesis.guava.collect.bimap.BiMap";
    private static final int maxSeqLength       = 8;
    private static final String logFilePath     = "tmp/thesis/guava/bimap.txt";

    public BiMapLauncher() {
        super(targetClassName, maxSeqLength, logFilePath);
        super.setScope("thesis.guava.collect.bimap.BiMap", 1, 1);
        super.setScope("thesis.guava.collect.bimap.Value", 5, 6);
        super.setScope("thesis.common.IntMap", 5, 6);
        super.setHexFilePaths();
    }

    public static void main(String[] args) {
        BiMapLauncher launcher = new BiMapLauncher();
        launcher.buildGraph(false);
    }

}

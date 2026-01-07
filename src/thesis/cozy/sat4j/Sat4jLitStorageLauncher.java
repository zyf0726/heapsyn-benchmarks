package thesis.cozy.sat4j;

import thesis.common.Launcher;

public final class Sat4jLitStorageLauncher extends Launcher {

    private static final String targetClassName = "thesis.cozy.sat4j.Sat4jLitStorage";
    private static final int maxSeqLength       = 8;
    private static final String logFilePath     = "tmp/thesis/cozy/sat4j.txt";

    public Sat4jLitStorageLauncher() {
        super(targetClassName, maxSeqLength, logFilePath);
        super.setScope("thesis.cozy.sat4j.Sat4jLitStorage", 1, 1);
        super.setScope("thesis.cozy.sat4j.Record", 5, 6);
        super.setScope("thesis.common.IntMap", 5, 6);
        super.setHexFilePaths();
    }

    public static void main(String[] args) {
        Sat4jLitStorageLauncher launcher = new Sat4jLitStorageLauncher();
        launcher.buildGraph(false);
    }

}

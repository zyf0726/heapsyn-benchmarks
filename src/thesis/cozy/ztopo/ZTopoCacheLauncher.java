package thesis.cozy.ztopo;

import thesis.common.Launcher;

public final class ZTopoCacheLauncher extends Launcher {

    private static final String targetClassName = "thesis.cozy.ztopo.ZTopoCache";
    private static final int maxSeqLength       = 8;
    private static final String logFilePath     = "tmp/thesis/cozy/ztopo.txt";

    public ZTopoCacheLauncher() {
        super(targetClassName, maxSeqLength, logFilePath);
        super.setScope("thesis.cozy.ztopo.ZTopoCache", 1, 1);
        super.setScope("thesis.cozy.ztopo.Entry", 5, 6);
        super.setScope("thesis.common.IntMap", 5, 6);
        super.setHexFilePaths();
    }

    public static void main(String[] args) {
        ZTopoCacheLauncher launcher = new ZTopoCacheLauncher();
        launcher.buildGraph(false);
    }

}

package thesis.cozy.lobsters;

import thesis.common.Launcher;

public final class LobstersLauncher extends Launcher {

    private static final String targetClassName = "thesis.cozy.lobsters.Lobsters";
    private static final int maxSeqLength       = 8;
    private static final String logFilePath     = "tmp/thesis/cozy/lobsters.txt";
    private static final String hexFilePath     = "HEXsettings/thesis/cozy/lobsters.jbse";

    public LobstersLauncher() {
        super(targetClassName, maxSeqLength, logFilePath);
        super.setScope("thesis.cozy.lobsters.Lobsters", 1, 1);
        super.setScope("thesis.cozy.lobsters.Story", 5, 6);
        super.setScope("thesis.cozy.lobsters.Vote", 5, 6);
        super.setScope("thesis.common.IntMap", 5, 6);
        super.setScope("thesis.common.IntSet", 5, 6);
        super.setHexFilePaths(hexFilePath);
    }

    public static void main(String[] args) {
        LobstersLauncher launcher = new LobstersLauncher();
        launcher.buildGraph(false);
    }

}

package thesis.cozy.openfire;

import thesis.common.Launcher;

public final class RosterCoreLauncher extends Launcher {

    private static final String targetClassName = "thesis.cozy.openfire.RosterCore";
    private static final int maxSeqLength       = 8;
    private static final String logFilePath     = "tmp/thesis/cozy/openfire.txt";
    private static final String hexFilePath     = "HEXsettings/thesis/cozy/openfire.jbse";

    public RosterCoreLauncher() {
        super(targetClassName, maxSeqLength, logFilePath);
        super.setScope("thesis.cozy.openfire.RosterCore", 1, 1);
        super.setScope("thesis.cozy.openfire.RosterItem", 5, 6);
        super.setScope("thesis.cozy.openfire.Group", 5, 6);
        super.setScope("thesis.common.IntMap", 5, 6);
        super.setScope("thesis.common.IntSet", 5, 6);
        super.setHexFilePaths(hexFilePath);
    }

    public static void main(String[] args) {
        RosterCoreLauncher launcher = new RosterCoreLauncher();
        launcher.buildGraph(false);
    }

}

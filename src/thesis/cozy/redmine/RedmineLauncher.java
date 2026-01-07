package thesis.cozy.redmine;

import thesis.common.Launcher;

public final class RedmineLauncher extends Launcher {

    private static final String targetClassName = "thesis.cozy.redmine.Redmine";
    private static final int maxSeqLength       = 8;
    private static final String logFilePath     = "tmp/thesis/cozy/redmine.txt";
    private static final String hexFilePath     = "HEXsettings/thesis/cozy/redmine.jbse";

    public RedmineLauncher() {
        super(targetClassName, maxSeqLength, logFilePath);
        super.setScope("thesis.cozy.redmine.Redmine", 1, 1);
        super.setScope("thesis.cozy.redmine.Project", 5, 6);
        super.setScope("thesis.cozy.redmine.Issue", 5, 6);
        super.setScope("thesis.common.IntMap", 5, 6);
        super.setScope("thesis.common.IntSet", 5, 6);
        super.setHexFilePaths(hexFilePath);
    }

    public static void main(String[] args) {
        RedmineLauncher launcher = new RedmineLauncher();
        launcher.buildGraph(false);
    }

}

package thesis.cozy.clausedb;

import thesis.common.Launcher;

public final class ClauseDbLauncher extends Launcher {

    private static final String targetClassName    = "thesis.cozy.clausedb.ClauseDb";
    private static final int maxSeqLength          = 8;
    private static final String logFilePath        = "tmp/thesis/cozy/clausedb.txt";

    public ClauseDbLauncher() {
        super(targetClassName, maxSeqLength, logFilePath);
        super.setScope("thesis.cozy.clausedb.ClauseDb", 1, 1);
        super.setScope("thesis.cozy.clausedb.Clause", 5, 6);
        super.setScope("thesis.common.IntSet", 5, 6);
        super.setScope("thesis.common.IntMap", 5, 6);
        super.setHexFilePaths();
    }

    public static void main(String[] args) {
        ClauseDbLauncher launcher = new ClauseDbLauncher();
        launcher.buildGraph(true);
    }

}

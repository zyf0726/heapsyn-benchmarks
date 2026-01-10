package thesis.guava.collect.table;

import thesis.common.Launcher;

public final class TableLauncher extends Launcher {

    private static final String targetClassName = "thesis.guava.collect.table.Table";
    private static final int maxSeqLength       = 8;
    private static final String logFilePath     = "tmp/thesis/guava/table.txt";
    private static final String hexFilePath     = "HEXsettings/thesis/guava/table.jbse";

    public TableLauncher() {
        super(targetClassName, maxSeqLength, logFilePath);
        super.setScope("thesis.guava.collect.table.Table", 1, 1);
        super.setScope("thesis.guava.collect.table.Value", 5, 6);
        super.setScope("thesis.guava.collect.table.Row", 5, 6);
        super.setScope("thesis.common.IntMap", 5, 6);
        super.setHexFilePaths(hexFilePath);
    }

    public static void main(String[] args) {
        TableLauncher launcher = new TableLauncher();
        launcher.buildGraph(false);
    }

}

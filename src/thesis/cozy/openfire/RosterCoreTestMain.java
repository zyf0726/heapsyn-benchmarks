package thesis.cozy.openfire;

import static thesis.cozy.openfire.RosterCoreAssertions.requireNoThrow;
import static thesis.cozy.openfire.RosterCoreAssertions.requireThrows;

/**
 * Plain main() runner for exercising the operations in {@link RosterCore}.
 *
 * <p>It prints a small PASS/FAIL report and exits with code 1 if any scenario fails.
 */
public final class RosterCoreTestMain {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        run("A1 create + add/remove user", RosterCoreTestMain::scenarioCreateAddRemoveUser);
        run("A2 roster item lifecycle", RosterCoreTestMain::scenarioRosterItemLifecycle);
        run("A3 group lifecycle empty", RosterCoreTestMain::scenarioGroupLifecycleEmpty);
        run("A4 members happy path", RosterCoreTestMain::scenarioMembersHappyPath);
        run("A5 shared groups ONLY_GROUP happy path", RosterCoreTestMain::scenarioSharedGroupsHappyPath);

        // A longer scenario that does many operations and prints the final state for manual inspection.
        run("A6 big mixed operations + print final state", RosterCoreTestMain::scenarioBigMixedOperationsAndPrint);

        run("B1 removeUser blocked by group membership", RosterCoreTestMain::scenarioRemoveUserBlocked);
        run("B2 addRosterItem duplicate itemId", RosterCoreTestMain::scenarioAddRosterItemDuplicateId);
        run("B3 addRosterItem redundant user+target", RosterCoreTestMain::scenarioAddRosterItemRedundant);
        run("B4 addGroup duplicate groupId", RosterCoreTestMain::scenarioAddGroupDuplicate);
        run("B5 removeGroup unknown", RosterCoreTestMain::scenarioRemoveGroupUnknown);
        run("B6 removeGroup blocked by members", RosterCoreTestMain::scenarioRemoveGroupNotEmpty);
        run("B7 removeGroup blocked by children", RosterCoreTestMain::scenarioRemoveGroupHasChildren);
        run("B8 removeGroup blocked because still child of parent", RosterCoreTestMain::scenarioRemoveGroupStillChild);
        run("B9 addMember unknown group", RosterCoreTestMain::scenarioAddMemberUnknownGroup);
        run("B10 addMember unknown user", RosterCoreTestMain::scenarioAddMemberUnknownUser);
        run("B11 addSharedGroup unknown parent", RosterCoreTestMain::scenarioAddSharedGroupUnknownParent);
        run("B12 addSharedGroup unknown child", RosterCoreTestMain::scenarioAddSharedGroupUnknownChild);
        run("B13 addSharedGroup parent not ONLY_GROUP", RosterCoreTestMain::scenarioAddSharedGroupParentNotOnlyGroup);
        run("B14 setMode unknown group", RosterCoreTestMain::scenarioSetModeUnknownGroup);
        run("B15 setMode disallowed from ONLY_GROUP while has children", RosterCoreTestMain::scenarioSetModeDisallowed);

        System.out.println("==================================================");
        System.out.printf("RosterCoreTestMain: PASSED=%d, FAILED=%d%n", passed, failed);
        if (failed != 0) {
            System.exit(1);
        }
    }

    private static void run(String name, Runnable scenario) {
        try {
            scenario.run();
            System.out.println("PASS  " + name);
            passed++;
        } catch (Throwable t) {
            System.out.println("FAIL  " + name);
            t.printStackTrace(System.out);
            failed++;
        }
    }

    // -------------------- Happy paths --------------------

    private static void scenarioCreateAddRemoveUser() {
        requireNoThrow(() -> {
            RosterCore core = RosterCore.create();
            core.addUser(1);
            core.removeUser(1);
            // removing a non-existing user is fine (IntSet.remove is a no-op)
            core.removeUser(1);
        }, "create/add/remove user");
    }

    private static void scenarioRosterItemLifecycle() {
        requireNoThrow(() -> {
            RosterCore core = RosterCore.create();
            core.addRosterItem(10, 1, 2);
            core.removeRosterItem(10);
            // removing a non-existing item is fine (IntMap.remove is a no-op)
            core.removeRosterItem(10);
        }, "roster item lifecycle");
    }

    private static void scenarioGroupLifecycleEmpty() {
        requireNoThrow(() -> {
            RosterCore core = RosterCore.create();
            core.addGroup(7, Group.EVERYBODY);
            core.removeGroup(7);
        }, "group lifecycle empty");
    }

    private static void scenarioMembersHappyPath() {
        requireNoThrow(() -> {
            RosterCore core = RosterCore.create();
            core.addUser(1);
            core.addGroup(7, Group.EVERYBODY);
            core.addMember(7, 1);
            core.removeMember(7, 1);
            core.removeGroup(7);
            core.removeUser(1);
        }, "members happy path");
    }

    private static void scenarioSharedGroupsHappyPath() {
        requireNoThrow(() -> {
            RosterCore core = RosterCore.create();
            core.addGroup(1, Group.ONLY_GROUP);
            core.addGroup(2, Group.EVERYBODY);

            core.addSharedGroup(1, 2);
            core.removeSharedGroup(1, 2);

            core.removeGroup(2);
            core.removeGroup(1);
        }, "shared groups happy path");
    }

    /**
     * A "bigger" smoke test that does many operations and then prints the final state.
     *
     * <p>It intentionally does NOT assert on every internal detail; it's meant for manual inspection.
     */
    private static void scenarioBigMixedOperationsAndPrint() {
        requireNoThrow(() -> {
            RosterCore core = RosterCore.create();

            System.out.println("\n=== A6: initial state ===");
            core.print();

            // Users
            core.addUser(10);
            core.addUser(20);
            core.addUser(30);
            core.addUser(40);
            System.out.println("\n=== A6: after addUser(10,20,30,40) ===");
            core.print();

            // Groups
            core.addGroup(100, Group.EVERYBODY);
            core.addGroup(200, Group.ONLY_GROUP);
            core.addGroup(300, Group.EVERYBODY);
            System.out.println("\n=== A6: after addGroup(100,200,300) ===");
            core.print();

            // Members
            core.addMember(100, 10);
            core.addMember(100, 20);
            core.addMember(200, 30);
            core.addMember(300, 40);
            System.out.println("\n=== A6: after addMember(...) ===");
            core.print();

            // Shared groups in ONLY_GROUP parent
            core.addSharedGroup(200, 100);
            core.addSharedGroup(200, 300);
            System.out.println("\n=== A6: after addSharedGroup(200->100,200->300) ===");
            core.print();

            // Roster items
            core.addRosterItem(1, 10, 20);
            core.addRosterItem(2, 10, 30);
            core.addRosterItem(3, 20, 10);
            core.addRosterItem(4, 40, 10);
            System.out.println("\n=== A6: after addRosterItem(1..4) ===");
            core.print();

            core.removeRosterItem(3);
            core.addRosterItem(5, 20, 40);
            System.out.println("\n=== A6: after removeRosterItem(3) and addRosterItem(5) ===");
            core.print();

            // Mutations / removals
            core.removeMember(100, 20);
            core.removeMember(300, 40);
            System.out.println("\n=== A6: after removeMember(100,20) and removeMember(300,40) ===");
            core.print();

            core.setMode(100, Group.ONLY_GROUP); // allowed (group 100 was EVERYBODY)
            System.out.println("\n=== A6: after setMode(100, ONLY_GROUP) ===");
            core.print();

            // Adjust shared groups so parent can later be changed / removed
            core.removeSharedGroup(200, 300);
            System.out.println("\n=== A6: after removeSharedGroup(200,300) ===");
            core.print();

            // To change mode FROM ONLY_GROUP, parent 200 must have no children, so remove one more
            core.removeSharedGroup(200, 100);
            System.out.println("\n=== A6: after removeSharedGroup(200,100) (parent 200 now has no children) ===");
            core.print();

            core.setMode(200, Group.EVERYBODY);
            System.out.println("\n=== A6: after setMode(200, EVERYBODY) ===");
            core.print();

            // Clean up some entities to exercise removals
            core.removeGroup(300); // empty now
            System.out.println("\n=== A6: after removeGroup(300) ===");
            core.print();

            core.removeUser(40);   // user 40 is not in any group now
            System.out.println("\n=== A6: after removeUser(40) ===");
            core.print();

            System.out.println("\n-------------------- A6 final state (manual check) --------------------");
            core.print();
            System.out.println("-----------------------------------------------------------------------\n");
        }, "big mixed operations + multi-stage print");
    }

    // -------------------- Expected failures --------------------

    private static void scenarioRemoveUserBlocked() {
        RosterCore core = RosterCore.create();
        core.addUser(1);
        core.addGroup(7, Group.EVERYBODY);
        core.addMember(7, 1);

        requireThrows(IllegalArgumentException.class,
                () -> core.removeUser(1),
                "removeUser blocked",
                "still in group 7");
    }

    private static void scenarioAddRosterItemDuplicateId() {
        RosterCore core = RosterCore.create();
        core.addRosterItem(10, 1, 2);

        requireThrows(IllegalArgumentException.class,
                () -> core.addRosterItem(10, 3, 4),
                "addRosterItem duplicate id",
                "duplicated roster item: 10");
    }

    private static void scenarioAddRosterItemRedundant() {
        RosterCore core = RosterCore.create();
        core.addRosterItem(10, 1, 2);

        requireThrows(IllegalArgumentException.class,
                () -> core.addRosterItem(11, 1, 2),
                "addRosterItem redundant",
                "redundant roster item for user 1 and target 2");
    }

    private static void scenarioAddGroupDuplicate() {
        RosterCore core = RosterCore.create();
        core.addGroup(7, Group.EVERYBODY);

        requireThrows(IllegalArgumentException.class,
                () -> core.addGroup(7, Group.EVERYBODY),
                "addGroup duplicate",
                "duplicated group: 7");
    }

    private static void scenarioRemoveGroupUnknown() {
        RosterCore core = RosterCore.create();

        requireThrows(IllegalArgumentException.class,
                () -> core.removeGroup(123),
                "removeGroup unknown",
                "unknown group: 123");
    }

    private static void scenarioRemoveGroupNotEmpty() {
        RosterCore core = RosterCore.create();
        core.addUser(1);
        core.addGroup(7, Group.EVERYBODY);
        core.addMember(7, 1);

        requireThrows(IllegalArgumentException.class,
                () -> core.removeGroup(7),
                "removeGroup not empty",
                "group 7 is not empty");
    }

    private static void scenarioRemoveGroupHasChildren() {
        RosterCore core = RosterCore.create();
        core.addGroup(1, Group.ONLY_GROUP);
        core.addGroup(2, Group.EVERYBODY);
        core.addSharedGroup(1, 2);

        requireThrows(IllegalArgumentException.class,
                () -> core.removeGroup(1),
                "removeGroup has children",
                "group 1 has children");
    }

    private static void scenarioRemoveGroupStillChild() {
        RosterCore core = RosterCore.create();
        core.addGroup(1, Group.ONLY_GROUP);
        core.addGroup(2, Group.EVERYBODY);
        core.addSharedGroup(1, 2);

        requireThrows(IllegalArgumentException.class,
                () -> core.removeGroup(2),
                "removeGroup still child",
                "still a child of group 1");
    }

    private static void scenarioAddMemberUnknownGroup() {
        RosterCore core = RosterCore.create();
        core.addUser(1);

        requireThrows(IllegalArgumentException.class,
                () -> core.addMember(999, 1),
                "addMember unknown group",
                "unknown group: 999");
    }

    private static void scenarioAddMemberUnknownUser() {
        RosterCore core = RosterCore.create();
        core.addGroup(7, Group.EVERYBODY);

        requireThrows(IllegalArgumentException.class,
                () -> core.addMember(7, 42),
                "addMember unknown user",
                "unknown user: 42");
    }

    private static void scenarioAddSharedGroupUnknownParent() {
        RosterCore core = RosterCore.create();
        core.addGroup(2, Group.EVERYBODY);

        requireThrows(IllegalArgumentException.class,
                () -> core.addSharedGroup(1, 2),
                "addSharedGroup unknown parent",
                "unknown parent group: 1");
    }

    private static void scenarioAddSharedGroupUnknownChild() {
        RosterCore core = RosterCore.create();
        core.addGroup(1, Group.ONLY_GROUP);

        requireThrows(IllegalArgumentException.class,
                () -> core.addSharedGroup(1, 2),
                "addSharedGroup unknown child",
                "unknown child group: 2");
    }

    private static void scenarioAddSharedGroupParentNotOnlyGroup() {
        RosterCore core = RosterCore.create();
        core.addGroup(1, Group.EVERYBODY);
        core.addGroup(2, Group.EVERYBODY);

        requireThrows(IllegalArgumentException.class,
                () -> core.addSharedGroup(1, 2),
                "addSharedGroup parent not ONLY_GROUP",
                "not in ONLY_GROUP mode");
    }

    private static void scenarioSetModeUnknownGroup() {
        RosterCore core = RosterCore.create();

        requireThrows(IllegalArgumentException.class,
                () -> core.setMode(999, Group.EVERYBODY),
                "setMode unknown group",
                "unknown group: 999");
    }

    private static void scenarioSetModeDisallowed() {
        RosterCore core = RosterCore.create();
        core.addGroup(1, Group.ONLY_GROUP);
        core.addGroup(2, Group.EVERYBODY);
        core.addSharedGroup(1, 2);

        requireThrows(IllegalArgumentException.class,
                () -> core.setMode(1, Group.EVERYBODY),
                "setMode disallowed",
                "from ONLY_GROUP while it has children");
    }
}

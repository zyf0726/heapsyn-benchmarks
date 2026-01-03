package thesis.cozy.clausedb;

import thesis.common.IntMap;
import thesis.common.IntSet;

import java.util.NoSuchElementException;

public final class ClauseDb {

    private IntMap<Clause> clauses;
    private IntSet falseLits;

    public ClauseDb() {
        this.clauses = IntMap.empty();
        this.falseLits = IntSet.empty();
    }

    public static ClauseDb create() {
        return new ClauseDb();
    }

    public void addClause(int cid) {
        if (IntMap.containsKey(clauses, cid))
            throw new IllegalArgumentException("duplicated clause: " + cid);
        clauses = IntMap.put(clauses, cid, new Clause(IntSet.empty()));
    }

    public void addClauseLiteral(int cid, int lid) {
        IntSet oldLits = IntMap.getOrThrow(clauses, cid).literals;
        IntSet newLits = IntSet.add(oldLits, lid);
        clauses = IntMap.put(clauses, cid, new Clause(newLits));
    }

    public void falsify(int lid) {
        falseLits = IntSet.add(falseLits, lid);
    }

    public void unfalsify(int lid) {
        falseLits = IntSet.remove(falseLits, lid);
    }

    private void print() {
        for (IntMap<Clause> curr = clauses; curr != null; curr = curr.next) {
            int cid = curr.key;
            Clause c = curr.value;
            System.out.printf("Clause$%d: literals=%s\n", cid, IntSet.toString(c.literals));
        }
        System.out.printf("falseLits: %s\n", IntSet.toString(falseLits));
        System.out.println("==========");
    }

    public static void main(String[] args) {
        ClauseDb db = ClauseDb.create();
        db.addClause(0);
        db.addClause(1);
        db.addClauseLiteral(1, 10);
        db.addClauseLiteral(0, 5);
        db.addClauseLiteral(1, 5);
        db.falsify(5);
        db.falsify(10);
        db.unfalsify(5);
        db.print();
        try {
            db = ClauseDb.create();
            db.addClause(0);
            db.addClause(0);
            System.out.println("ERROR: duplicated clause not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            db = ClauseDb.create();
            db.addClauseLiteral(0, 1);
            System.out.println("ERROR: adding literal to non-existing clause not detected");
        } catch (NoSuchElementException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
    }

}

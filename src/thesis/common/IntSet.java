package thesis.common;

/**
 * A simple immutable set of integers, implemented as a sorted linked list.
 */
public final class IntSet {

    public final int elem;
    public final IntSet next;

    private IntSet(int elem, IntSet next) {
        this.elem = elem;
        this.next = next;
    }

    /**
     * Create an empty IntSet.
     *
     * @return an empty IntSet
     */
    public static IntSet empty() {
        return null;
    }

    /**
     * Add an element to the set, returning a new set.
     *
     * @param set  the original set
     * @param elem the element to add
     * @return a new set with the element added
     */
    public static IntSet add(IntSet set, int elem) {
        if (set == null || elem < set.elem) {
            return new IntSet(elem, set);
        } else if (elem == set.elem) {
            return set;     // already present
        } else {
            return new IntSet(set.elem, add(set.next, elem));
        }
    }

    /**
     * Check if the set contains an element.
     *
     * @param set  the set to check
     * @param elem the element to look for
     * @return true if the element is in the set, false otherwise
     */
    public static boolean contains(IntSet set, int elem) {
        if (set == null) return false;
        if (elem == set.elem) return true;
        return contains(set.next, elem);
    }

    /**
     * Remove an element from the set, returning a new set.
     *
     * @param set  the original set
     * @param elem the element to remove
     * @return a new set with the element removed
     */
    public static IntSet remove(IntSet set, int elem) {
        if (set == null) {
            return null;
        } else if (elem == set.elem) {
            return set.next;  // remove this element
        } else {
            return new IntSet(set.elem, remove(set.next, elem));
        }
    }

    /**
     * Get the size of the set (number of elements).
     *
     * @param set the set to measure
     * @return the number of elements in the set
     */
    public static int size(IntSet set) {
        if (set == null) return 0;
        return 1 + size(set.next);
    }

    /**
     * Check if the set is empty.
     *
     * @param set the set to check
     * @return true if the set is empty, false otherwise
     */
    public static boolean isEmpty(IntSet set) {
        return set == null;
    }

    /**
     * Convert the set to a string representation.
     *
     * @param set the set to convert
     * @return a string representation of the set
     */
    public static String toString(IntSet set) {
        StringBuilder sb = new StringBuilder("{");
        for (IntSet curr = set; curr != null; curr = curr.next) {
            sb.append(curr.elem);
            if (curr.next != null) sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }

    private static boolean isStrictlyIncreasing(IntSet set) {
        if (set == null) return true;
        for (IntSet curr = set; curr.next != null; curr = curr.next) {
            if (curr.elem >= curr.next.elem) return false;
        }
        return true;
    }

    // Basic tests to exercise the IntSet API
    public static void main(String[] args) {
        IntSet s = IntSet.empty();
        assert isEmpty(s);
        assert size(s) == 0;
        assert toString(s).equals("{}");

        // Insert out of order to check sorting + de-dup
        s = add(s, 5);
        s = add(s, 1);
        s = add(s, 3);
        s = add(s, 2);
        s = add(s, 4);
        s = add(s, 3); // duplicate

        assert size(s) == 5 : "size should be 5 after adding 1..5 with a duplicate";
        assert contains(s, 1) && contains(s, 5);
        assert !contains(s, 9);
        assert isStrictlyIncreasing(s) : "set must be strictly increasing (sorted, no dups)";

        String str = toString(s);
        System.out.println("Set: " + str);
        assert str.equals("{1, 2, 3, 4, 5}");

        // Remove middle/head/tail
        s = remove(s, 3);
        assert !contains(s, 3);
        assert size(s) == 4;
        assert isStrictlyIncreasing(s);
        assert toString(s).equals("{1, 2, 4, 5}");

        s = remove(s, 1);
        assert !contains(s, 1);
        assert size(s) == 3;
        assert isStrictlyIncreasing(s);
        assert toString(s).equals("{2, 4, 5}");

        s = remove(s, 5);
        assert !contains(s, 5);
        assert size(s) == 2;
        assert isStrictlyIncreasing(s);
        assert toString(s).equals("{2, 4}");

        // Remove non-existing: should be no-op in terms of contents
        IntSet s2 = remove(s, 42);
        assert size(s2) == size(s);
        assert toString(s2).equals(toString(s));
        assert isStrictlyIncreasing(s2);

        // Negative / zero
        s2 = add(s2, 0);
        s2 = add(s2, -7);
        assert contains(s2, 0) && contains(s2, -7);
        assert isStrictlyIncreasing(s2);
        assert toString(s2).equals("{-7, 0, 2, 4}");

        System.out.println("All IntSet tests passed.");
    }
}

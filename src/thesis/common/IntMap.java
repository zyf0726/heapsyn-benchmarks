package thesis.common;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A simple immutable map from int keys to ValueType values, implemented as a sorted linked list.
 *
 * @param <V> the type of values stored in the map
 */
public final class IntMap<V extends ValueType> {

    public final int key;
    public final V value;
    public final IntMap<V> next;

    private IntMap(int key, V value, IntMap<V> next) {
        this.key = key;
        this.value = Objects.requireNonNull(value);
        this.next = next;
    }

    /**
     * Create an empty IntMap.
     *
     * @param <V> the type of values stored in the map
     * @return an empty IntMap
     */
    public static <V extends ValueType> IntMap<V> empty() {
        return null;
    }

    /**
     * Put a key-value pair into the map, returning a new map.
     *
     * @param map   the original map
     * @param key   the key to insert
     * @param value the value to insert
     * @param <V>   the type of values stored in the map
     * @return a new map with the key-value pair added
     */
    public static <V extends ValueType> IntMap<V> put(IntMap<V> map, int key, V value) {
        if (map == null || key < map.key) {
            return new IntMap<>(key, value, map);
        } else if (key == map.key) {
            return new IntMap<>(key, value, map.next);  // replace existing
        } else {
            return new IntMap<>(map.key, map.value, put(map.next, key, value));
        }
    }

    /**
     * Get the value associated with a key in the map.
     *
     * @param map the map to search
     * @param key the key to look for
     * @param <V> the type of values stored in the map
     * @return the value associated with the key, or null if not found
     */
    public static <V extends ValueType> V get(IntMap<V> map, int key) {
        if (map == null) return null;
        if (key == map.key) return map.value;
        return get(map.next, key);
    }

    /**
     * Get the value associated with a key in the map, throwing if not found.
     *
     * @param map the map to search
     * @param key the key to look for
     * @param <V> the type of values stored in the map
     * @return the value associated with the key
     * @throws NoSuchElementException if the key is not found
     */
    public static <V extends ValueType> V getOrThrow(IntMap<V> map, int key) {
        V value = get(map, key);
        if (value == null) {
            throw new NoSuchElementException("key not found: " + key);
        } else {
            return value;
        }
    }

    /**
     * Check if the map contains a key.
     *
     * @param map the map to search
     * @param key the key to look for
     * @param <V> the type of values stored in the map
     * @return true if the key is in the map, false otherwise
     */
    public static <V extends ValueType> boolean containsKey(IntMap<V> map, int key) {
        if (map == null) return false;
        if (key == map.key) return true;
        return containsKey(map.next, key);
    }

    /**
     * Remove a key from the map, returning a new map.
     *
     * @param map the original map
     * @param key the key to remove
     * @param <V> the type of values stored in the map
     * @return a new map with the key removed
     */
    public static <V extends ValueType> IntMap<V> remove(IntMap<V> map, int key) {
        if (map == null) {
            return null;
        } else if (key == map.key) {
            return map.next;  // remove this entry
        } else {
            return new IntMap<>(map.key, map.value, remove(map.next, key));
        }
    }

    /**
     * Get the size of the map (number of key-value pairs).
     *
     * @param map the map to measure
     * @param <V> the type of values stored in the map
     * @return the number of key-value pairs in the map
     */
    public static <V extends ValueType> int size(IntMap<V> map) {
        if (map == null) return 0;
        return 1 + size(map.next);
    }

    /**
     * Check if the map is empty.
     *
     * @param map the map to check
     * @param <V> the type of values stored in the map
     * @return true if the map is empty, false otherwise
     */
    public static <V extends ValueType> boolean isEmpty(IntMap<V> map) {
        return map == null;
    }

    /**
     * Convert the map to a string representation.
     *
     * @param map the map to convert
     * @param <V> the type of values stored in the map
     * @return a string representation of the map
     */
    public static <V extends ValueType> String toString(IntMap<V> map) {
        StringBuilder sb = new StringBuilder("{");
        for (IntMap<V> curr = map; curr != null; curr = curr.next) {
            sb.append(curr.key).append("=").append(curr.value);
            if (curr.next != null) sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }

    // Basic tests to exercise the IntMap API
    public static void main(String[] args) {
        // Local helper value type used only for testing
        class MyVal extends ValueType {
            final String label;
            MyVal(String label) { this.label = label; }
            @Override public String toString() { return label; }
        }

        IntMap<MyVal> m = IntMap.empty();
        assert isEmpty(m);
        assert size(m) == 0;

        // Putting a null value must fail
        try {
            put(m, 99, null);
            throw new AssertionError("put should reject null values");
        } catch (NullPointerException expected) {
            // expected
        }

        // insert out of order to check sorting
        m = put(m, 5, new MyVal("five"));
        m = put(m, 1, new MyVal("one"));
        m = put(m, 3, new MyVal("three"));
        m = put(m, 2, new MyVal("two"));
        m = put(m, 4, new MyVal("four"));
        // replace existing key
        m = put(m, 3, new MyVal("THREE"));

        // Verify size and order
        assert size(m) == 5 : "size should be 5";
        assert containsKey(m, 1) && containsKey(m, 5);
        assert !containsKey(m, 9);
        assert get(m, 9) == null;
        try {
            getOrThrow(m, 9);
            throw new AssertionError("getOrThrow should throw on missing key");
        } catch (NoSuchElementException expected) {
            // expected
        }
        assert get(m, 3).toString().equals("THREE");
        assert getOrThrow(m, 3).toString().equals("THREE");

        String s = toString(m);
        System.out.println("Map: " + s);
        // Expect keys 1..5 in order
        assert s.startsWith("{") && s.endsWith("}");

        // Remove middle and head and tail
        m = remove(m, 3);
        assert size(m) == 4 && !containsKey(m, 3) && get(m, 3) == null;
        m = remove(m, 1);
        assert size(m) == 3 && !containsKey(m, 1) && get(m, 5) != null;
        m = remove(m, 5);
        assert size(m) == 2 && !containsKey(m, 5);
        try {
            getOrThrow(m, 5);
            throw new AssertionError("getOrThrow should throw on missing key");
        } catch (NoSuchElementException expected) {
            // expected
        }

        // Remove non-existing is no-op
        IntMap<MyVal> m2 = remove(m, 42);
        assert size(m2) == size(m);

        System.out.println("After removals: " + toString(m2));
        System.out.println("All IntMap tests passed.");
    }
}

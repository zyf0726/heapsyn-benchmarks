package thesis.cozy.openfire;

/**
 * Tiny assertion helpers so we can test {@link RosterCore} from a plain main() without JUnit.
 */
public final class RosterCoreAssertions {

    private RosterCoreAssertions() {
        // utility
    }

    public static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void requireNoThrow(Runnable body, String name) {
        try {
            body.run();
        } catch (Throwable t) {
            throw new AssertionError("Expected no exception in scenario: " + name + ", but got: " + t, t);
        }
    }

    public static void requireThrows(Class<? extends Throwable> expectedType,
                                     Runnable body,
                                     String name,
                                     String expectedMessageSubstring) {
        try {
            body.run();
        } catch (Throwable t) {
            if (!expectedType.isInstance(t)) {
                throw new AssertionError("Scenario '" + name + "' expected exception of type " +
                        expectedType.getName() + ", but got " + t.getClass().getName() + ": " + t, t);
            }
            if (expectedMessageSubstring != null) {
                String msg = t.getMessage();
                if (msg == null || !msg.contains(expectedMessageSubstring)) {
                    throw new AssertionError("Scenario '" + name + "' expected exception message to contain '" +
                            expectedMessageSubstring + "', but was: '" + msg + "'", t);
                }
            }
            return; // expected
        }

        throw new AssertionError("Scenario '" + name + "' expected exception " + expectedType.getName() +
                " but no exception was thrown.");
    }
}


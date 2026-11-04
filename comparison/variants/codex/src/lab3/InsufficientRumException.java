package lab3;

/** Ожидаемая нехватка ресурса, после которой сценарий может продолжиться. */
public final class InsufficientRumException extends Exception {
    private static final long serialVersionUID = 1L;
    private final int requested;
    private final int available;

    public InsufficientRumException(int requested, int available) {
        this.requested = requested;
        this.available = available;
    }

    @Override public String getMessage() {
        return "Недостаточно рома: нужно " + requested + " мл, осталось " + available + " мл";
    }
}

package lab3;

/** Положение персонажа относительно лодки. */
public enum Location {
    ON_SHORE("на берегу"), BY_BOAT("рядом с лодкой"), IN_BOAT("в лодке");

    private final String description;
    Location(String description) { this.description = description; }
    @Override public String toString() { return description; }
}

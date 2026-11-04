package lab3;

public enum Emotion {
    WORRIED("тревога"), HOPEFUL("надежда"), JOYFUL("радость"),
    MOVED("растроганность"), CALM("спокойствие");

    private final String description;
    Emotion(String description) { this.description = description; }
    @Override public String toString() { return description; }
}

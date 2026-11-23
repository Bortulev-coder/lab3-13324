package lab3;

public enum JoyAction {
    CRY("плачет от радости", 1) {
        @Override public boolean perform(Friday friday) { return friday.cry(); }
    },
    LAUGH("смеётся", 1) {
        @Override public boolean perform(Friday friday) { return friday.laugh(); }
    },
    DANCE("танцует на берегу", 5) {
        @Override public boolean perform(Friday friday) { return friday.dance(); }
    },
    WAVE("машет руками", 2) {
        @Override public boolean perform(Friday friday) { return friday.waveHands(); }
    },
    HIT_HEAD("колотит себя по голове от избытка чувств", 3) {
        @Override public boolean perform(Friday friday) { return friday.hitHead(); }
    },
    HIT_FACE("колотит себя по лицу от избытка чувств", 3) {
        @Override public boolean perform(Friday friday) { return friday.hitFace(); }
    };

    private final String description;
    private final int cost;
    JoyAction(String description, int cost) { this.description = description; this.cost = cost; }
    public String description() { return description; }
    public int cost() { return cost; }
    public abstract boolean perform(Friday friday);
}

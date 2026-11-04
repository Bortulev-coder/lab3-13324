package lab3;

public enum JoyAction {
    CRY("плачет от радости", 1), LAUGH("смеётся", 1),
    DANCE("танцует на берегу", 5), WAVE("машет руками", 2);

    private final String description;
    private final int cost;
    JoyAction(String description, int cost) { this.description = description; this.cost = cost; }
    public String description() { return description; }
    public int cost() { return cost; }
}

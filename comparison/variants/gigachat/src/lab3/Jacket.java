package lab3;

public final class Jacket extends WorldObject {
    private boolean open;

    public Jacket(String id, StoryLog log) { super(id, "Куртка Пятницы", log); }
    public boolean isOpen() { return open; }

    public void open() {
        if (!open) {
            open = true;
            log.event("Пятница распахивает куртку, чтобы прижать отца к груди.");
        }
    }

    @Override protected String describeState() { return "распахнута=" + open; }
}

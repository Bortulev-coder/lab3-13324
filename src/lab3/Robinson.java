package lab3;

public final class Robinson extends Person {
    public Robinson(String id, StoryLog log) {
        super(id, "Робинзон", new Condition(90, 0), log);
    }

    public void orderRelease(Friday friday, Father captive) {
        log.event("Робинзон просит Пятницу объяснить пленнику, что тот свободен.");
        friday.announceFreedom(captive);
    }

    public void adviseMassage() {
        log.event("Робинзон советует растереть одеревеневшие руки и ноги ромом.");
    }

    @Override public void reactToReunion() {
        feel(Emotion.MOVED);
        log.event("Робинзон тронут сыновней любовью и улыбается выходкам друга.");
    }
}

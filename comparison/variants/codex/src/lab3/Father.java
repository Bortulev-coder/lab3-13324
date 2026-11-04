package lab3;

public final class Father extends Person {
    private boolean free;
    private boolean seated;

    public Father(String id, Condition condition, StoryLog log) {
        super(id, "Отец Пятницы", condition, log);
    }

    public boolean isFree() { return free; }
    public boolean isSeated() { return seated; }
    public boolean canSpeak() { return condition().energy() >= 25; }

    public void release() {
        if (free) return;
        free = true;
        recover(5, 0);
        feel(Emotion.HOPEFUL);
        log.event("Пленник узнаёт о свободе и оживляется; силы=" + condition().energy());
    }

    public void acceptSip() {
        recover(15, 10);
        log.event("Глоток рома ободряет отца; " + describeState());
    }

    public void sitUp() {
        if (!seated && condition().energy() >= 20) {
            seated = true;
            log.event("Отец приподнимается и садится в лодке.");
        } else if (!seated) {
            log.event("Отец пока слишком слаб, чтобы сесть.");
        }
    }

    public void receiveCare(int relief) {
        recover(3, relief);
        log.event("Отец чувствует облегчение; " + describeState());
    }

    @Override public void reactToReunion() { feel(Emotion.JOYFUL); }

    @Override protected String describeState() {
        return super.describeState() + ", свободен=" + free + ", сидит=" + seated;
    }
}

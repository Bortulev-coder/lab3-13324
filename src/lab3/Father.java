package lab3;

import java.util.Objects;

public final class Father extends Person {
    private boolean free;
    private boolean seated;
    private int encouragements;

    public Father(String id, Condition condition, StoryLog log) {
        super(id, "Отец Пятницы", condition, log);
    }

    public boolean isFree() { return free; }
    public boolean isSeated() { return seated; }
    public boolean canSpeak() { return condition().energy() >= 25; }
    public int encouragements() { return encouragements; }

    public void acceptEncouragement(Person supporter) {
        Objects.requireNonNull(supporter);
        encouragements++;
        feel(Emotion.HOPEFUL);
        recordEvent(supporter.name() + " поддерживает пленника добрым словом.");
    }

    public void release() {
        if (free) return;
        free = true;
        recover(5, 0);
        feel(Emotion.HOPEFUL);
        recordEvent("Пленник узнаёт о свободе и оживляется; силы=" + condition().energy());
    }

    public void acceptSip() {
        recover(15, 10);
        recordEvent("Глоток рома ободряет отца; " + describeState());
    }

    public void sitUp() {
        if (!seated && condition().energy() >= 20) {
            seated = true;
            recordEvent("Отец приподнимается и садится.");
        } else if (!seated) {
            recordEvent("Отец пока слишком слаб, чтобы сесть.");
        }
    }

    public void receiveCare(int relief) {
        recover(3, relief);
        recordEvent("Отец чувствует облегчение; " + describeState());
    }

    @Override public void reactToReunion() { feel(Emotion.JOYFUL); }

    @Override protected String describeState() {
        return super.describeState() + ", свободен=" + free + ", сидит=" + seated
                + ", поддержек=" + encouragements;
    }
}

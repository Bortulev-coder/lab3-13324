package lab3;

import java.util.Objects;
import java.util.Random;

public final class Friday extends Person {
    private final Father father;
    private final Jacket jacket;
    private boolean recognized;
    private int joyfulActions;

    public Friday(String id, int energy, Father father, Jacket jacket, StoryLog log) {
        super(id, "Пятница", new Condition(energy, 0), log);
        this.father = Objects.requireNonNull(father);
        this.jacket = Objects.requireNonNull(jacket);
    }

    public boolean recognizedFather() { return recognized; }
    public int joyfulActions() { return joyfulActions; }
    public void announceFreedom(Father captive) { captive.release(); }

    public void giveSip(Father captive, RumBottle bottle) throws InsufficientRumException {
        Objects.requireNonNull(captive);
        bottle.consume(this, 10);
        captive.acceptSip();
    }

    public boolean recognize(Father person, boolean daylight) {
        if (!father.equals(person)) return false;
        if (recognized) return true;
        if (daylight || person.canSpeak()) {
            recognized = true;
            log.event("Пятница узнаёт отца по " + (daylight ? "лицу" : "голосу") + ".");
        } else {
            log.event("В темноте Пятница не различает лицо; пленник слишком слаб, чтобы говорить.");
        }
        return recognized;
    }

    @Override public void reactToReunion() { feel(Emotion.JOYFUL); }

    public void celebrate(Random random) {
        if (!recognized) return;
        JoyAction[] actions = JoyAction.values();
        int count = 4 + random.nextInt(4);
        for (int i = 0; i < count; i++) {
            JoyAction action = actions[random.nextInt(actions.length)];
            if (!spendEnergy(action.cost())) {
                log.event("Пятница устал и прекращает бурно выражать радость.");
                break;
            }
            joyfulActions++;
            log.event("Пятница " + action.description() + "; силы=" + condition().energy());
        }
    }

    public void jumpAround(Boat boat, Random random) {
        if (!recognized) return;
        int count = 2 + random.nextInt(4);
        for (int i = 0; i < count; i++) {
            if (condition().energy() < 4) {
                log.event("Пятница бережёт оставшиеся силы и больше не прыгает.");
                break;
            }
            try {
                boat.board(this);
                spendEnergy(4);
                joyfulActions++;
                log.event("Пятница прыгает в лодку и обратно; силы=" + condition().energy());
                boat.leave(this);
            } catch (IllegalStateException exception) {
                log.event(exception.getMessage() + ". Пятница остаётся рядом на берегу.");
                break;
            }
        }
    }

    public void calmDown() {
        feel(Emotion.CALM);
        log.event("Пятница успокаивается и объясняет Робинзону: это его отец.");
    }

    public void embrace() {
        if (!recognized) return;
        jacket.open();
        father.receiveCare(5);
        log.event("Пятница обнимает отца, осторожно прижимая его голову к груди.");
    }

    public void massage(Father patient, RumBottle bottle, Random random)
            throws InsufficientRumException {
        Objects.requireNonNull(patient);
        Objects.requireNonNull(random);
        bottle.consume(this, 20);
        int relief = 20 + random.nextInt(11);
        patient.receiveCare(relief);
        log.event("Пятница растирает ромом руки и ноги; уменьшение скованности=" + relief);
    }

    public void massageWithoutRum(Father patient) {
        patient.receiveCare(8);
        log.event("Пятница осторожно растирает руки и ноги без рома.");
    }

    @Override protected String describeState() {
        return super.describeState() + ", узнал отца=" + recognized
                + ", радостных действий=" + joyfulActions;
    }
}

package lab3;

import java.util.Objects;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public final class Friday extends Person {
    private final Father father;
    private final Jacket jacket;
    private boolean recognized;
    private int joyfulActions;
    private final ArrayList<JoyAction> expressions = new ArrayList<>();
    private CareMethod plannedCare = CareMethod.NONE;
    private Father careRecipient;
    private boolean relationshipExplained;
    private int failedBoardings;

    public Friday(String id, int energy, Father father, Jacket jacket, StoryLog log) {
        super(id, "Пятница", new Condition(energy, 0), log);
        this.father = Objects.requireNonNull(father);
        this.jacket = Objects.requireNonNull(jacket);
    }

    public boolean recognizedFather() { return recognized; }
    public int joyfulActions() { return joyfulActions; }
    public Father father() { return father; }
    public CareMethod plannedCare() { return plannedCare; }
    public Father careRecipient() { return careRecipient; }
    public boolean relationshipExplained() { return relationshipExplained; }
    public int failedBoardings() { return failedBoardings; }
    public List<JoyAction> expressions() { return List.copyOf(expressions); }
    public void announceFreedom(Father captive) { captive.release(); }

    public void comfort(Father captive, InsufficientRumException reason) {
        requireReachable(captive);
        Objects.requireNonNull(reason);
        careRecipient = captive;
        captive.acceptEncouragement(this);
        recordEvent(reason.getMessage() + ". Пятница выбирает словесную поддержку.");
    }

    public void offerHelp(Father captive) {
        Objects.requireNonNull(captive);
        if (careRecipient == captive) return;
        careRecipient = captive;
        recordEvent("Встреча пока не узнана; Пятница берётся помогать незнакомому пленнику.");
    }

    public void planCare(CareMethod method) {
        Objects.requireNonNull(method);
        if (plannedCare == method) return;
        plannedCare = method;
        recordEvent("Пятница выбирает способ ухода: " + plannedCare + ".");
    }

    public void chooseDryCare(InsufficientRumException reason) {
        Objects.requireNonNull(reason);
        planCare(CareMethod.DRY);
        recordEvent(reason.getMessage() + ". Выбран уход без рома.");
    }

    public void giveSip(Father captive, RumBottle bottle) throws InsufficientRumException {
        requireReachable(captive);
        bottle.consume(this, 10);
        captive.acceptSip();
    }

    public boolean recognize(Father person, boolean daylight) {
        if (!father.equals(person)) return false;
        if (recognized) return true;
        if (daylight || person.canSpeak()) {
            recognized = true;
            recordEvent("Пятница узнаёт отца по " + (daylight ? "лицу" : "голосу") + ".");
        } else {
            recordEvent("В темноте Пятница не различает лицо; пленник слишком слаб, чтобы говорить.");
        }
        return recognized;
    }

    @Override public void reactToReunion() { feel(Emotion.JOYFUL); }

    public void celebrate(Random random) {
        if (!recognized) return;
        Objects.requireNonNull(random);
        JoyAction[] actions = JoyAction.values();
        int count = 4 + random.nextInt(4);
        for (int i = 0; i < count; i++) {
            JoyAction action = actions[random.nextInt(actions.length)];
            if (!action.perform(this)) {
                recordEvent("Пятница устал и прекращает бурно выражать радость.");
                break;
            }
        }
    }

    public boolean cry() { return express(JoyAction.CRY); }
    public boolean laugh() { return express(JoyAction.LAUGH); }
    public boolean dance() { return express(JoyAction.DANCE); }
    public boolean waveHands() { return express(JoyAction.WAVE); }
    public boolean hitHead() { return express(JoyAction.HIT_HEAD); }
    public boolean hitFace() { return express(JoyAction.HIT_FACE); }

    private boolean express(JoyAction action) {
        if (action == JoyAction.DANCE && location() == Location.IN_BOAT) {
            throw new IllegalStateException("Для танца на берегу нужно выйти из лодки");
        }
        if (!recognized || !spendEnergy(action.cost())) return false;
        expressions.add(action);
        joyfulActions++;
        recordEvent("Пятница " + action.description() + "; силы=" + condition().energy());
        return true;
    }

    public boolean jumpInto(Boat target) {
        Objects.requireNonNull(target);
        if (location() == Location.IN_BOAT) {
            throw new IllegalStateException("Сначала нужно выйти из лодки");
        }
        if (condition().energy() < 4) return false;
        if (!isNear(target)) approach(target);
        target.board(this);
        spendEnergy(4);
        joyfulActions++;
        recordEvent("Пятница впрыгивает в лодку; силы=" + condition().energy());
        return true;
    }

    public void jumpOut(Boat target) {
        Objects.requireNonNull(target);
        if (!isAboard(target)) {
            throw new IllegalStateException("Пятница не находится в этой лодке");
        }
        target.leave(this);
    }

    public void jumpAround(Boat boat, Random random) {
        if (!recognized) return;
        Objects.requireNonNull(boat);
        Objects.requireNonNull(random);
        int count = 2 + random.nextInt(4);
        for (int i = 0; i < count; i++) {
            try {
                if (!jumpInto(boat)) {
                    recordEvent("Пятница бережёт оставшиеся силы и больше не прыгает.");
                    break;
                }
                jumpOut(boat);
            } catch (IllegalStateException exception) {
                failedBoardings++;
                recordEvent(exception.getMessage() + ". Пятница остаётся рядом на берегу.");
                break;
            }
        }
    }

    public void calmDown() {
        feel(Emotion.CALM);
    }

    public void explainRelationship(Robinson listener) {
        Objects.requireNonNull(listener);
        if (!recognized || emotion() != Emotion.CALM) {
            throw new IllegalStateException("Сначала нужно узнать отца и успокоиться");
        }
        listener.learnFamily(this, father);
        if (relationshipExplained) return;
        relationshipExplained = true;
        recordEvent("Пятница объясняет Робинзону: это его отец.");
    }

    public void embrace() {
        if (!recognized) return;
        requireReachable(father);
        jacket.open();
        father.receiveCare(5);
        careRecipient = father;
        recordEvent("Пятница обнимает отца, осторожно прижимая его голову к груди.");
    }

    public void performCare(Father patient, RumBottle bottle, Random random)
            throws InsufficientRumException {
        switch (plannedCare) {
            case RUM -> massage(patient, bottle, random);
            case DRY -> massageWithoutRum(patient);
            case NONE -> throw new IllegalStateException("Способ ухода ещё не выбран");
        }
    }

    public void massage(Father patient, RumBottle bottle, Random random)
            throws InsufficientRumException {
        requireReachable(patient);
        Objects.requireNonNull(random);
        bottle.consume(this, 20);
        int relief = 20 + random.nextInt(11);
        patient.receiveCare(relief);
        careRecipient = patient;
        recordEvent("Пятница растирает ромом руки и ноги; уменьшение скованности=" + relief);
    }

    public void massageWithoutRum(Father patient) {
        requireReachable(patient);
        patient.receiveCare(8);
        careRecipient = patient;
        recordEvent("Пятница осторожно растирает руки и ноги без рома.");
    }

    private void requireReachable(Father patient) {
        Objects.requireNonNull(patient);
        if (!canReach(patient)) {
            throw new IllegalStateException("Сначала нужно подойти к лодке с пленником");
        }
    }

    @Override protected String describeState() {
        return super.describeState() + ", узнал отца=" + recognized
                + ", радостных действий=" + joyfulActions + ", выражения радости=" + expressions
                + ", объяснил родство=" + relationshipExplained + ", способ ухода=" + plannedCare
                + ", получатель помощи=" + (careRecipient == null ? "нет" : careRecipient.name())
                + ", отказов в посадке=" + failedBoardings;
    }
}

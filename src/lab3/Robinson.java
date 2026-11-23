package lab3;

import java.util.Objects;

public final class Robinson extends Person {
    private int releaseOrders;
    private boolean massageAdvised;
    private FamilyRelation knownFamily;

    public Robinson(String id, StoryLog log) {
        super(id, "Робинзон", new Condition(90, 0), log);
    }

    public void orderRelease(Friday friday, Father captive) {
        Objects.requireNonNull(friday);
        Objects.requireNonNull(captive);
        releaseOrders++;
        recordEvent("Робинзон просит Пятницу объяснить пленнику, что тот свободен.");
        friday.announceFreedom(captive);
    }

    public void adviseMassage() {
        if (massageAdvised) return;
        massageAdvised = true;
        recordEvent("Робинзон советует растереть одеревеневшие руки и ноги ромом.");
    }

    public void adviseMassage(Friday friday) {
        Objects.requireNonNull(friday);
        adviseMassage();
        friday.planCare(CareMethod.RUM);
    }

    public boolean massageAdvised() { return massageAdvised; }
    public int releaseOrders() { return releaseOrders; }
    public FamilyRelation knownFamily() { return knownFamily; }

    public void learnFamily(Friday son, Father father) {
        FamilyRelation relation = new FamilyRelation(son, father);
        if (!son.recognizedFather()) {
            throw new IllegalStateException("Пятница ещё не узнал отца");
        }
        if (relation.equals(knownFamily)) return;
        knownFamily = relation;
        recordEvent("Робинзон узнаёт, что спасённый пленник — отец Пятницы.");
    }

    @Override public void reactToReunion() {
        feel(Emotion.MOVED);
        recordEvent("Робинзон тронут сыновней любовью и улыбается выходкам друга.");
    }

    @Override protected String describeState() {
        return super.describeState() + ", поручений об освобождении=" + releaseOrders
                + ", совет о растирании=" + massageAdvised
                + ", знает родство=" + (knownFamily != null);
    }
}

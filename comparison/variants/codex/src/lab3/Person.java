package lab3;

import java.util.Objects;

public abstract class Person extends WorldObject {
    private Condition condition;
    private Emotion emotion = Emotion.WORRIED;

    protected Person(String id, String name, Condition condition, StoryLog log) {
        super(id, name, log);
        this.condition = Objects.requireNonNull(condition);
    }

    public final Condition condition() { return condition; }
    public final Emotion emotion() { return emotion; }

    protected final void recover(int energy, int relief) {
        condition = condition.recover(energy, relief);
    }

    protected final boolean spendEnergy(int cost) {
        if (cost < 0) throw new IllegalArgumentException("Отрицательная затрата сил");
        if (condition.energy() < cost) return false;
        condition = condition.spend(cost);
        return true;
    }

    protected final void feel(Emotion next) {
        emotion = Objects.requireNonNull(next);
        log.event(name() + ": настроение - " + emotion + ".");
    }

    public abstract void reactToReunion();

    @Override
    protected String describeState() {
        return "силы=" + condition.energy() + ", скованность="
                + condition.stiffness() + ", настроение=" + emotion;
    }
}

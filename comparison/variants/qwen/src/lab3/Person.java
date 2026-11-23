package lab3;

import java.util.Objects;

public abstract class Person extends WorldObject {
    private Condition condition;
    private Emotion emotion = Emotion.WORRIED;
    private Location location = Location.ON_SHORE;
    private Boat boat;

    protected Person(String id, String name, Condition condition, StoryLog log) {
        super(id, name, log);
        this.condition = Objects.requireNonNull(condition);
    }

    public final Condition condition() { return condition; }
    public final Emotion emotion() { return emotion; }
    public final Location location() { return location; }
    public final Boat boat() { return boat; }

    public final boolean isNear(Boat target) {
        return location == Location.BY_BOAT && boat == target;
    }

    public final boolean isAboard(Boat target) {
        return location == Location.IN_BOAT && boat == target;
    }

    public final boolean canReach(Person other) {
        Objects.requireNonNull(other);
        if (location == Location.ON_SHORE && other.location == Location.ON_SHORE) return true;
        return boat != null && boat == other.boat;
    }

    public final void approach(Boat target) {
        Objects.requireNonNull(target);
        if (location == Location.IN_BOAT) {
            throw new IllegalStateException("Сначала нужно выйти из лодки");
        }
        if (isNear(target)) return;
        boat = target;
        location = Location.BY_BOAT;
        recordEvent(name() + " подходит к лодке у самого берега.");
    }

    // Изменение положения при посадке и высадке согласует объект Boat.
    final void embark(Boat target) {
        boat = target;
        location = Location.IN_BOAT;
    }

    final void disembark(Boat target) {
        boat = target;
        location = Location.BY_BOAT;
    }

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
        Objects.requireNonNull(next);
        if (emotion == next) return;
        emotion = next;
        recordEvent(name() + ": настроение - " + emotion + ".");
    }

    public abstract void reactToReunion();

    @Override
    protected String describeState() {
        return "силы=" + condition.energy() + ", скованность="
                + condition.stiffness() + ", настроение=" + emotion + ", положение=" + location
                + ", лодка=" + (boat == null ? "нет" : boat.name());
    }
}

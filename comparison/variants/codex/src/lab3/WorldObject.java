package lab3;

import java.util.Objects;

/** Идентичность сущности не зависит от её изменяемого состояния. */
public abstract class WorldObject {
    private final String id;
    private final String name;
    protected final StoryLog log;

    protected WorldObject(String id, String name, StoryLog log) {
        if (id == null || id.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя и идентификатор обязательны");
        }
        this.id = id;
        this.name = name;
        this.log = Objects.requireNonNull(log);
    }

    public final String name() { return name; }
    protected abstract String describeState();

    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return id.equals(((WorldObject) other).id);
    }

    @Override
    public final int hashCode() { return Objects.hash(getClass(), id); }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + "[id=" + id + ", name=" + name
                + ", " + describeState() + "]";
    }
}

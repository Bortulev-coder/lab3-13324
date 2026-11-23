package lab3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Boat extends WorldObject {
    private final int capacity;
    private final ArrayList<Person> passengers = new ArrayList<>();

    public Boat(String id, int capacity, StoryLog log) {
        super(id, "Лодка у берега", log);
        if (capacity < 1) throw new IllegalArgumentException("В лодке должно быть хотя бы одно место");
        this.capacity = capacity;
    }

    public boolean contains(Person person) { return passengers.contains(person); }
    public List<Person> passengers() { return List.copyOf(passengers); }

    public void board(Person person) {
        Objects.requireNonNull(person);
        if (contains(person)) return;
        if (person.location() == Location.IN_BOAT) {
            throw new IllegalStateException("Персонаж уже находится в другой лодке");
        }
        if (passengers.size() == capacity) {
            throw new IllegalStateException("В лодке нет свободного места");
        }
        passengers.add(person);
        person.embark(this);
        recordEvent(person.name() + " оказывается в лодке; пассажиров=" + passengers.size());
    }

    public void leave(Person person) {
        int index = passengers.indexOf(person);
        if (index >= 0) {
            Person passenger = passengers.remove(index);
            passenger.disembark(this);
            recordEvent(passenger.name() + " выходит из лодки на берег; пассажиров=" + passengers.size());
        }
    }

    @Override protected String describeState() {
        return "мест=" + capacity + ", пассажиры="
                + passengers.stream().map(Person::name).toList();
    }
}

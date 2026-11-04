package lab3;

import java.util.Objects;

public final class RumBottle extends WorldObject {
    private int volume;
    private Person owner;

    public RumBottle(String id, int volume, Person owner, StoryLog log) {
        super(id, "Бутылочка рома", log);
        if (volume < 0 || volume > 1000) {
            throw new IllegalArgumentException("Объём бутылки должен быть 0..1000 мл");
        }
        this.volume = volume;
        this.owner = Objects.requireNonNull(owner);
    }

    public int volume() { return volume; }
    public Person owner() { return owner; }

    public void transfer(Person from, Person to) {
        requireOwner(from);
        owner = Objects.requireNonNull(to);
        log.event(from.name() + " передаёт бутылочку: " + to.name() + ".");
    }

    public void consume(Person user, int amount) throws InsufficientRumException {
        requireOwner(user);
        if (amount <= 0) throw new IllegalArgumentException("Порция должна быть положительной");
        if (amount > volume) throw new InsufficientRumException(amount, volume);
        volume -= amount;
        log.event(user.name() + " расходует " + amount + " мл рома; осталось " + volume + " мл.");
    }

    private void requireOwner(Person person) {
        if (!owner.equals(person)) {
            throw new IllegalArgumentException("Бутылка находится у другого персонажа");
        }
    }

    @Override protected String describeState() {
        return "объём=" + volume + " мл, владелец=" + owner.name();
    }
}

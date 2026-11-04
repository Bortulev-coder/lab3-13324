package lab3;

/** Сценарий зависит от журнала, а не от конкретной консоли. */
@FunctionalInterface
public interface StoryLog {
    void event(String message);
}

package lab3;

import java.util.List;
import java.util.Objects;

/** Выводит события модели и технические сведения о прогоне. */
public final class StoryNarrator {
    private final StoryLog output;

    public StoryNarrator(StoryLog output) {
        this.output = Objects.requireNonNull(output);
    }

    public void begin(ScenarioConfig config) {
        write("Вариант 13324. " + config);
    }

    public void describe(List<? extends WorldObject> objects) {
        write("Итоговое состояние мира:");
        for (WorldObject object : objects) write(object.toString());
    }

    public static void publish(StoryLog output, StoryEvent event) {
        write(output, event.message());
    }

    private void write(String message) {
        write(output, message);
    }

    private static void write(StoryLog output, String message) {
        output.event(message);
    }
}

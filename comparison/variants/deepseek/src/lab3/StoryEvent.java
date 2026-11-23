package lab3;

import java.util.Objects;

/** Неизменяемая запись события и состояния его участника после действия. */
public record StoryEvent(String message, String state) {
    public StoryEvent {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Описание события обязательно");
        }
        Objects.requireNonNull(state);
    }
}

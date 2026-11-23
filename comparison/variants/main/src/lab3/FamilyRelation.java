package lab3;

import java.util.Objects;

/** Сообщённая Робинзону связь между сыном и отцом. */
public record FamilyRelation(Friday son, Father father) {
    public FamilyRelation {
        Objects.requireNonNull(son);
        Objects.requireNonNull(father);
        if (!son.father().equals(father)) {
            throw new IllegalArgumentException("Этот человек не является отцом Пятницы");
        }
    }
}

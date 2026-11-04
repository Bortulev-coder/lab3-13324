package lab3;

/** Неизменяемый снимок состояния. Все показатели измеряются от 0 до 100. */
public record Condition(int energy, int stiffness) {
    public Condition {
        if (energy < 0 || energy > 100 || stiffness < 0 || stiffness > 100) {
            throw new IllegalArgumentException("Показатели должны быть в диапазоне 0..100");
        }
    }

    public Condition recover(int energyGain, int stiffnessRelief) {
        if (energyGain < 0 || stiffnessRelief < 0) {
            throw new IllegalArgumentException("Улучшение не может быть отрицательным");
        }
        return new Condition(energy + Math.min(100 - energy, energyGain),
                stiffness - Math.min(stiffness, stiffnessRelief));
    }

    public Condition spend(int cost) {
        if (cost < 0 || cost > energy) {
            throw new IllegalArgumentException("Нельзя потратить столько сил: " + cost);
        }
        return new Condition(energy - cost, stiffness);
    }
}

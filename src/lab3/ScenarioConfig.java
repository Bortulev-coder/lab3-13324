package lab3;

/** Параметры начального мира и воспроизводимой случайности. */
public record ScenarioConfig(long seed, int rum, int fatherEnergy,
        int fridayEnergy, int capacity, boolean daylight) {
    public ScenarioConfig {
        if (rum < 0 || rum > 1000) throw new IllegalArgumentException("Ром: 0..1000 мл");
        if (fatherEnergy < 0 || fatherEnergy > 100 || fridayEnergy < 0 || fridayEnergy > 100) {
            throw new IllegalArgumentException("Начальные силы: 0..100");
        }
        if (capacity < 1 || capacity > 10) throw new IllegalArgumentException("Места в лодке: 1..10");
    }

    public static ScenarioConfig defaults() {
        return new ScenarioConfig(13324L, 80, 15, 70, 3, true);
    }

    public static ScenarioConfig parse(String[] args) {
        ScenarioConfig defaults = defaults();
        long seed = defaults.seed();
        int rum = defaults.rum(), father = defaults.fatherEnergy();
        int friday = defaults.fridayEnergy(), capacity = defaults.capacity();
        boolean daylight = true;
        for (String arg : args) {
            if (arg.equals("--dark")) { daylight = false; continue; }
            String[] pair = arg.split("=", 2);
            if (pair.length != 2) throw new IllegalArgumentException("Ожидался --параметр=число: " + arg);
            switch (pair[0]) {
                case "--seed" -> seed = Long.parseLong(pair[1]);
                case "--rum" -> rum = Integer.parseInt(pair[1]);
                case "--father-energy" -> father = Integer.parseInt(pair[1]);
                case "--friday-energy" -> friday = Integer.parseInt(pair[1]);
                case "--capacity" -> capacity = Integer.parseInt(pair[1]);
                default -> throw new IllegalArgumentException("Неизвестный параметр: " + pair[0]);
            }
        }
        return new ScenarioConfig(seed, rum, father, friday, capacity, daylight);
    }
}

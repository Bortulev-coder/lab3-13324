package lab3;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/** Создаёт новый мир при каждом запуске; последовательность действий отделена от сущностей. */
public final class Scenario {
    private final StoryLog log;

    public Scenario(StoryLog log) { this.log = Objects.requireNonNull(log); }

    public void run(ScenarioConfig config) {
        Random random = new Random(config.seed());
        Robinson robinson = new Robinson("robinson", log);
        Father father = new Father("father", new Condition(config.fatherEnergy(), 80), log);
        Jacket jacket = new Jacket("jacket", log);
        Friday friday = new Friday("friday", config.fridayEnergy(), father, jacket, log);
        Boat boat = new Boat("boat", config.capacity(), log);
        RumBottle bottle = new RumBottle("rum", config.rum(), robinson, log);
        List<Person> people = List.of(robinson, friday, father);

        log.event("Вариант 13324. " + config);
        boat.board(father);
        log.event("Пятница подходит к лодке у самого берега.");
        robinson.orderRelease(friday, father);
        bottle.transfer(robinson, friday);
        try {
            friday.giveSip(father, bottle);
        } catch (InsufficientRumException exception) {
            log.event(exception.getMessage() + ". Пятница поддерживает пленника добрым словом.");
        }
        father.sitUp();
        if (friday.recognize(father, config.daylight())) {
            // Динамическая диспетчеризация: одна операция, три разных реакции.
            for (Person person : people) person.reactToReunion();
            friday.celebrate(random);
            friday.jumpAround(boat, random);
            friday.calmDown();
            friday.embrace();
        } else {
            log.event("Встреча пока не узнана; Пятница помогает незнакомому пленнику.");
        }
        robinson.adviseMassage();
        try {
            friday.massage(father, bottle, random);
        } catch (InsufficientRumException exception) {
            log.event(exception.getMessage() + ". Выбран уход без рома.");
            friday.massageWithoutRum(father);
        }
        father.sitUp();
        log.event("Итоговое состояние мира:");
        for (WorldObject object : List.of(robinson, friday, father, boat, bottle, jacket)) {
            log.event(object.toString());
        }
    }
}

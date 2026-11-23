package lab3;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/** Создаёт новый мир при каждом запуске; последовательность действий отделена от сущностей. */
public final class Scenario {
    private final StoryLog log;
    private final StoryNarrator narrator;

    public Scenario(StoryLog log) {
        this.log = Objects.requireNonNull(log);
        narrator = new StoryNarrator(log);
    }

    public void run(ScenarioConfig config) {
        Random random = new Random(config.seed());
        Robinson robinson = new Robinson("robinson", log);
        Father father = new Father("father", new Condition(config.fatherEnergy(), 80), log);
        Jacket jacket = new Jacket("jacket", log);
        Friday friday = new Friday("friday", config.fridayEnergy(), father, jacket, log);
        Boat boat = new Boat("boat", config.capacity(), log);
        RumBottle bottle = new RumBottle("rum", config.rum(), robinson, log);

        narrator.begin(config);
        boat.board(father);
        friday.approach(boat);
        robinson.orderRelease(friday, father);
        bottle.transfer(robinson, friday);
        try {
            friday.giveSip(father, bottle);
        } catch (InsufficientRumException exception) {
            friday.comfort(father, exception);
        }
        father.sitUp();
        if (friday.recognize(father, config.daylight())) {
            // Динамическая диспетчеризация: общая операция реакции для персонажей.
            for (Person person : List.of(friday, father)) person.reactToReunion();
            friday.celebrate(random);
            friday.jumpAround(boat, random);
            friday.calmDown();
            friday.explainRelationship(robinson);
            Person witness = robinson;
            witness.reactToReunion();
            friday.embrace();
        } else {
            friday.offerHelp(father);
        }
        robinson.adviseMassage(friday);
        try {
            friday.performCare(father, bottle, random);
        } catch (InsufficientRumException exception) {
            friday.chooseDryCare(exception);
            friday.massageWithoutRum(father);
        }
        father.sitUp();
        narrator.describe(List.of(robinson, friday, father, boat, bottle, jacket));
    }
}

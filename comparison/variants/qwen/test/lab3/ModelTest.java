package lab3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/** Автономные проверки: внешние библиотеки и флаг -ea не требуются. */
public final class ModelTest {
    private static int passed;
    private static final StoryLog SILENT = message -> { };
    private ModelTest() { }

    @FunctionalInterface
    private interface CheckedAction { void run() throws Exception; }

    private static void check(boolean value, String description) {
        if (!value) throw new AssertionError(description);
        passed++;
    }

    private static void expect(Class<? extends Throwable> type, CheckedAction action) {
        try {
            action.run();
        } catch (Exception exception) {
            check(type.isInstance(exception), "Expected " + type + ", got " + exception);
            return;
        }
        throw new AssertionError("Expected exception: " + type.getSimpleName());
    }

    private static Father father(int energy) {
        return new Father("father", new Condition(energy, 80), SILENT);
    }

    private static Friday friday(Father father, int energy) {
        return new Friday("friday", energy, father, new Jacket("jacket", SILENT), SILENT);
    }

    public static void main(String[] args) throws Exception {
        conditionTests();
        identityTests();
        bottleTests();
        boatTests();
        interactionTests();
        movementTests();
        communicationTests();
        actionTests();
        scenarioTests();
        System.out.println("PASS: " + passed + " checks");
    }

    private static void conditionTests() {
        Condition initial = new Condition(95, 5);
        check(initial.recover(20, 20).equals(new Condition(100, 0)), "Recovery limits");
        check(initial.recover(Integer.MAX_VALUE, Integer.MAX_VALUE)
                .equals(new Condition(100, 0)), "No integer overflow");
        check(initial.equals(new Condition(95, 5)), "Record is immutable");
        check(initial.spend(95).energy() == 0, "Exact energy boundary");
        expect(IllegalArgumentException.class, () -> new Condition(-1, 0));
        expect(IllegalArgumentException.class, () -> new Condition(0, 101));
        expect(IllegalArgumentException.class, () -> initial.recover(-1, 0));
        expect(IllegalArgumentException.class, () -> initial.spend(96));
    }

    private static void identityTests() {
        Father first = father(15);
        Father second = father(90);
        Father third = father(50);
        check(first.equals(first), "Reflexivity");
        check(first.equals(second) && second.equals(first), "Symmetry, same entity id");
        check(second.equals(third) && first.equals(third), "Transitivity");
        check(!first.equals(null) && !first.equals("father"), "Null and different type");
        check(!first.equals(new Robinson("father", SILENT)), "Different entity classes");
        check(first.hashCode() == second.hashCode(), "Equal hash codes");
        HashSet<WorldObject> set = new HashSet<>();
        set.add(first);
        int hash = first.hashCode();
        first.release();
        check(hash == first.hashCode() && set.contains(first), "Hash survives state changes");
        check(first.toString().contains("свободен=true"), "Dynamic state description");
        first.release();
        check(first.condition().energy() == 20, "Release is idempotent");
        Jacket a = new Jacket("a", SILENT), b = new Jacket("b", SILENT);
        check(!a.equals(b), "Different items");
    }

    private static void bottleTests() throws Exception {
        Robinson owner = new Robinson("owner", SILENT);
        RumBottle bottle = new RumBottle("rum", 20, owner, SILENT);
        expect(IllegalArgumentException.class, () -> bottle.consume(owner, 0));
        expect(IllegalArgumentException.class, () -> bottle.consume(owner, -1));
        expect(IllegalArgumentException.class, () -> bottle.consume(new Robinson("other", SILENT), 1));
        check(bottle.volume() == 20, "Bad requests do not consume rum");
        expect(InsufficientRumException.class, () -> bottle.consume(owner, 21));
        check(bottle.volume() == 20, "Insufficient request is atomic");
        bottle.consume(owner, 20);
        check(bottle.volume() == 0, "Consume entire bottle");
        expect(InsufficientRumException.class, () -> bottle.consume(owner, 1));
        check(new InsufficientRumException(20, 5).getMessage().contains("5 мл"), "Exception details");
        Father next = father(15);
        bottle.transfer(owner, next);
        check(bottle.owner().equals(next), "Ownership changes");
        expect(IllegalArgumentException.class, () -> bottle.transfer(owner, next));
    }

    private static void boatTests() {
        Boat boat = new Boat("boat", 1, SILENT);
        Father father = father(15);
        boat.board(father);
        boat.board(father);
        check(boat.passengers().size() == 1, "No duplicate passenger");
        List<Person> snapshot = boat.passengers();
        expect(UnsupportedOperationException.class, () -> snapshot.clear());
        expect(IllegalStateException.class, () -> boat.board(new Robinson("r", SILENT)));
        check(boat.passengers().size() == 1, "Capacity respected after failure");
        boat.leave(father);
        check(boat.passengers().isEmpty() && snapshot.size() == 1, "Independent snapshot");
    }

    private static void interactionTests() throws Exception {
        Father father = father(0);
        Friday friday = friday(father, 0);
        check(!friday.recognize(father, false), "Cannot recognize in darkness without voice");
        check(friday.recognize(father, true), "Recognize by face");
        friday.celebrate(new Random(1));
        check(friday.joyfulActions() == 0 && friday.condition().energy() == 0, "Fatigue limits actions");
        RumBottle empty = new RumBottle("rum", 0, friday, SILENT);
        Condition before = father.condition();
        expect(InsufficientRumException.class, () -> friday.giveSip(father, empty));
        check(father.condition().equals(before), "No phantom drink benefit");
        expect(InsufficientRumException.class, () -> friday.massage(father, empty, new Random(1)));
        check(father.condition().equals(before), "No phantom massage benefit");
        friday.massageWithoutRum(father);
        check(father.condition().stiffness() == 72, "Fallback care works");
        Father voice = father(25);
        check(friday(voice, 70).recognize(voice, false), "Recognize by voice in darkness");
        RumBottle full = new RumBottle("full", 30, friday, SILENT);
        friday.giveSip(father, full);
        friday.massage(father, full, new Random(1));
        check(full.volume() == 0, "Sip and massage consume 30 ml");
        check(father.condition().stiffness() >= 32 && father.condition().stiffness() <= 42,
                "Random relief is within declared range");
    }

    private static String transcript(ScenarioConfig config) {
        ArrayList<String> events = new ArrayList<>();
        new Scenario(events::add).run(config);
        return String.join("\n", events);
    }

    private static void movementTests() throws Exception {
        Father father = father(15);
        Friday friday = friday(father, 70);
        Boat boat = new Boat("boat", 2, SILENT);
        boat.board(father);
        RumBottle bottle = new RumBottle("rum", 30, friday, SILENT);
        check(friday.location() == Location.ON_SHORE && friday.boat() == null, "Initial position");
        check(father.isAboard(boat) && boat.contains(father), "Board synchronizes both objects");
        expect(IllegalStateException.class, () -> friday.giveSip(father, bottle));
        check(bottle.volume() == 30 && father.condition().energy() == 15, "Distance failure has no resource effect");
        friday.approach(boat);
        check(friday.isNear(boat) && friday.canReach(father), "Approach changes position and reachability");
        check(!boat.contains(friday), "Approach does not board");
        String snapshot = friday.history().get(0).state();
        check(snapshot.contains("положение=рядом с лодкой"), "Approach records actual state");
        int events = friday.history().size();
        friday.approach(boat);
        check(friday.history().size() == events, "Repeated approach is idempotent");
        friday.giveSip(father, bottle);
        check(bottle.volume() == 20 && father.condition().energy() == 30, "Approach enables real care");
        check(friday.jumpInto(boat) && friday.isAboard(boat) && boat.contains(friday), "Jump synchronizes position and passengers");
        check(snapshot.equals(friday.history().get(0).state()), "Event is immutable snapshot");
        expect(IllegalStateException.class, () -> friday.approach(new Boat("other", 3, SILENT)));
        Boat other = new Boat("other", 3, SILENT);
        expect(IllegalStateException.class, () -> other.board(friday));
        check(other.passengers().isEmpty() && friday.isAboard(boat), "Cannot board two boats");
        int energy = friday.condition().energy();
        friday.jumpOut(boat);
        check(friday.isNear(boat) && !boat.contains(friday), "Jump out changes both objects");
        check(friday.condition().energy() == energy, "Exit does not invent extra expense");
        Boat full = new Boat("full", 1, SILENT);
        full.board(new Robinson("passenger", SILENT));
        expect(IllegalStateException.class, () -> friday.jumpInto(full));
        check(friday.condition().energy() == energy && !full.contains(friday), "Failed jump does not spend energy");
        List<StoryEvent> history = friday.history();
        expect(UnsupportedOperationException.class, history::clear);
        expect(NullPointerException.class, () -> friday.approach(null));
    }

    private static void communicationTests() {
        Father father = father(15);
        Friday friday = friday(father, 70);
        Robinson robinson = new Robinson("robinson", SILENT);
        check(robinson.knownFamily() == null && !friday.relationshipExplained(), "Initial knowledge");
        expect(IllegalStateException.class, () -> friday.explainRelationship(robinson));
        check(robinson.knownFamily() == null, "Failed explanation preserves knowledge");
        friday.recognize(father, true);
        friday.reactToReunion();
        expect(IllegalStateException.class, () -> friday.explainRelationship(robinson));
        friday.calmDown();
        friday.explainRelationship(robinson);
        check(robinson.knownFamily().father() == father && robinson.knownFamily().son() == friday, "Explanation transfers relationship");
        check(friday.relationshipExplained(), "Explanation changes speaker state");
        int events = robinson.history().size();
        friday.explainRelationship(robinson);
        check(robinson.history().size() == events, "Repeated explanation preserves knowledge");
        robinson.orderRelease(friday, father);
        check(robinson.releaseOrders() == 1 && father.isFree(), "Release instruction has model effects");
        robinson.adviseMassage(friday);
        check(robinson.massageAdvised() && friday.plannedCare() == CareMethod.RUM, "Advice selects real care plan");
        InsufficientRumException shortage = new InsufficientRumException(20, 5);
        friday.chooseDryCare(shortage);
        check(friday.plannedCare() == CareMethod.DRY, "Resource shortage changes care plan");
        friday.comfort(father, shortage);
        check(father.encouragements() == 1 && friday.careRecipient() == father, "Comfort affects recipient and caregiver");
        Father stranger = new Father("stranger", new Condition(15, 80), SILENT);
        friday.offerHelp(stranger);
        check(friday.careRecipient() == stranger, "Offer help selects recipient");
        expect(IllegalArgumentException.class, () -> new FamilyRelation(friday, stranger));
    }

    private static void actionTests() throws Exception {
        Father father = father(15);
        Friday friday = friday(father, 70);
        friday.recognize(father, true);
        int energy = friday.condition().energy();
        check(friday.cry() && friday.laugh() && friday.dance() && friday.waveHands()
                && friday.hitHead() && friday.hitFace(), "All literary expressions are actions");
        check(friday.expressions().equals(List.of(JoyAction.CRY, JoyAction.LAUGH, JoyAction.DANCE,
                JoyAction.WAVE, JoyAction.HIT_HEAD, JoyAction.HIT_FACE)), "Concrete action history");
        check(friday.condition().energy() == energy - 15 && friday.joyfulActions() == 6, "Each expression has energy effect");
        expect(UnsupportedOperationException.class, () -> friday.expressions().clear());
        Boat boat = new Boat("boat", 2, SILENT);
        boat.board(friday);
        energy = friday.condition().energy();
        expect(IllegalStateException.class, friday::dance);
        check(friday.condition().energy() == energy, "Failed dance does not spend energy");
        boat.leave(friday);
        friday.approach(boat);
        boat.board(father);
        RumBottle empty = new RumBottle("empty", 0, friday, SILENT);
        friday.planCare(CareMethod.RUM);
        Condition before = father.condition();
        expect(InsufficientRumException.class, () -> friday.performCare(father, empty, new Random(1)));
        check(father.condition().equals(before), "Failed planned care has no phantom effect");
        friday.chooseDryCare(new InsufficientRumException(20, 0));
        friday.performCare(father, empty, new Random(1));
        check(father.condition().equals(before.recover(3, 8)), "Selected fallback is executed");
        check(friday.careRecipient() == father, "Successful care saves recipient");
    }

    private static void scenarioTests() {
        ScenarioConfig defaults = ScenarioConfig.defaults();
        String normal = transcript(defaults);
        check(normal.equals(transcript(defaults)), "Same seed reproduces full log");
        check(normal.contains("объём=50 мл"), "Default resource balance");
        check(normal.contains("распахнута=true"), "Jacket state changes");
        check(normal.contains("положение=рядом с лодкой") && normal.contains("знает родство=true")
                && normal.contains("объяснил родство=true"), "Story models movement and transferred knowledge");
        check(normal.indexOf("Пятница объясняет Робинзону") < normal.indexOf("Робинзон тронут"),
                "Robinson reacts after learning relationship");
        String scarce = transcript(new ScenarioConfig(13324, 5, 15, 70, 3, true));
        check(scarce.contains("Выбран уход без рома"), "Checked failure handled in scenario");
        check(scarce.contains("объём=5 мл"), "Failed attempts keep resource intact");
        String dark = transcript(new ScenarioConfig(13324, 0, 0, 70, 3, false));
        check(dark.contains("узнал отца=false") && dark.contains("распахнута=false"), "Alternative plot");
        String full = transcript(new ScenarioConfig(13324, 80, 15, 70, 1, true));
        check(full.contains("В лодке нет свободного места"), "Unchecked failure handled in story");
        check(full.contains("Итоговое состояние мира"), "Story continues after full boat");
        HashSet<String> actionSequences = new HashSet<>();
        for (int seed = 0; seed < 20; seed++) {
            String story = transcript(new ScenarioConfig(seed, 80, 15, 70, 3, true));
            actionSequences.add(story.substring(story.indexOf("Пятница узнаёт")));
        }
        check(actionSequences.size() > 1, "Randomness changes events, not only the header");
        check(ScenarioConfig.parse(new String[] {"--rum=5", "--dark"}).rum() == 5, "Parse options");
        expect(IllegalArgumentException.class, () -> ScenarioConfig.parse(new String[] {"--rum=-1"}));
        expect(IllegalArgumentException.class, () -> ScenarioConfig.parse(new String[] {"--seed=bad"}));
        expect(IllegalArgumentException.class, () -> ScenarioConfig.parse(new String[] {"--unknown=1"}));
        expect(IllegalArgumentException.class, () -> ScenarioConfig.parse(new String[] {"--rum"}));
    }
}

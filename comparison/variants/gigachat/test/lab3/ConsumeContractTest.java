package lab3;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/** Одинаковые проверки для каждого изолированно подставленного метода. */
public final class ConsumeContractTest {
    private static int cases;
    private ConsumeContractTest() { }

    private static final class NamedPerson extends Person {
        NamedPerson(String id, String name) {
            super(id, name, new Condition(100, 0), message -> { });
        }
        @Override public void reactToReunion() { }
    }

    private static final class Fixture {
        final Person owner = new NamedPerson("owner", "Владелец");
        final Person other = new NamedPerson("other", "Посторонний");
        final ArrayList<String> messages = new ArrayList<>();
        final ArrayList<Integer> statesAtLog = new ArrayList<>();
        final RumBottle bottle;
        Fixture(int volume) {
            bottle = new RumBottle("bottle", volume, owner, this::record);
        }
        private void record(String message) {
            messages.add(message);
            statesAtLog.add(bottle.volume());
        }
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private static String rejected(Fixture f, Person user, int amount,
            Class<? extends Exception> type) throws Exception {
        int before = f.bottle.volume();
        try {
            f.bottle.consume(user, amount);
        } catch (Exception e) {
            check(e.getClass() == type, "Wrong exception for amount=" + amount);
            check(before == f.bottle.volume(), "State changed on failure");
            check(f.messages.isEmpty(), "Log emitted on failure");
            cases++;
            return e.getMessage();
        }
        throw new AssertionError("Missing exception for amount=" + amount);
    }

    private static void success(int amount, int remaining) throws Exception {
        Fixture f = new Fixture(100);
        f.bottle.consume(f.owner, amount);
        check(f.bottle.volume() == remaining, "Wrong remaining volume");
        check(f.messages.size() == 1, "Expected exactly one log");
        check(f.messages.get(0).contains(f.owner.name()), "Missing name");
        List<Integer> numbers = Pattern.compile("\\d+").matcher(f.messages.get(0))
                .results().map(m -> Integer.valueOf(m.group())).toList();
        check(numbers.equals(List.of(amount, remaining)), "Wrong numbers in log");
        check(f.statesAtLog.equals(List.of(remaining)), "Log precedes debit");
        cases++;
    }

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        success(30, 70);
        success(100, 0);
        Fixture f = new Fixture(100);
        rejected(f, f.owner, 101, InsufficientRumException.class);
        rejected(f, f.owner, 0, IllegalArgumentException.class);
        rejected(f, f.owner, -5, IllegalArgumentException.class);
        String ownerMessage = rejected(f, f.other, 10, IllegalArgumentException.class);
        rejected(f, null, 10, IllegalArgumentException.class);
        String priorityMessage = rejected(f, f.other, 0, IllegalArgumentException.class);
        check(ownerMessage.equals(priorityMessage), "Wrong validation priority");
        rejected(f, f.other, 101, IllegalArgumentException.class);
        Fixture empty = new Fixture(0);
        rejected(empty, empty.owner, 1, InsufficientRumException.class);
        rejected(f, f.owner, Integer.MAX_VALUE, InsufficientRumException.class);
        f.bottle.consume(f.owner, 20);
        f.bottle.consume(f.owner, 15);
        check(f.bottle.volume() == 65 && f.messages.size() == 2, "Repeated debits");
        check(f.statesAtLog.equals(List.of(80, 65)), "Log timing on repeated calls");
        cases++;
        System.out.println("PASS: " + cases + " shared contract cases");

        Fixture alias = new Fixture(100);
        Person user = new NamedPerson("owner", "Исполнитель");
        check(alias.owner.equals(user), "Alias must compare equal by identity");
        alias.bottle.consume(user, 10);
        boolean logsUser = alias.messages.get(0).contains(user.name());
        System.out.println("LOG_USES_USER_NAME=" + logsUser);
        System.out.println("ALIAS_LOG=" + alias.messages.get(0));
    }
}

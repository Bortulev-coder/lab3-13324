package lab3;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        PrintStream err = new PrintStream(System.err, true, StandardCharsets.UTF_8);
        if (Arrays.asList(args).contains("--help")) {
            help(out);
            return;
        }
        ScenarioConfig config;
        try {
            config = ScenarioConfig.parse(args);
        } catch (IllegalArgumentException exception) {
            err.println("Ошибка параметров: " + exception.getMessage());
            help(err);
            System.exit(2);
            return;
        }
        new Scenario(out::println).run(config);
    }

    private static void help(PrintStream out) {
        out.println("Запуск: java -jar lab3.jar [--seed=N] [--rum=0..1000]");
        out.println("  [--father-energy=0..100] [--friday-energy=0..100] [--capacity=1..10] [--dark]");
        out.println("Без параметров: seed=13324, ром=80, силы отца=15, силы Пятницы=70, мест=3, день.");
    }
}

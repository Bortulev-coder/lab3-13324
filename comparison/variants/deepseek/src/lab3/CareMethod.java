package lab3;

public enum CareMethod {
    NONE("не выбран"), RUM("растирание ромом"), DRY("растирание без рома");

    private final String description;
    CareMethod(String description) { this.description = description; }
    @Override public String toString() { return description; }
}

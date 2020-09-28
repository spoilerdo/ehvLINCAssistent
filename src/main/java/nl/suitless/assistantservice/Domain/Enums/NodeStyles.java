package nl.suitless.assistantservice.Domain.Enums;

public enum NodeStyles {
    START("0");

    private final String value;
    private NodeStyles(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

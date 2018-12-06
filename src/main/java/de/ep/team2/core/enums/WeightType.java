package de.ep.team2.core.enums;

public enum WeightType {

    SELF_WEIGHT("Eigengewicht"), FIXED_WEIGHT("Befestigtes Gewicht");

    private final String value;

    WeightType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}

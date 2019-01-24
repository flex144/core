package de.ep.team2.core.enums;

public enum TrainingsFocus {

    MUSCLE("Muskelaufbau"),
    STAMINA("Ausdauer"),
    WEIGHT("Gewichtsreduktion");

    private final String name;

    TrainingsFocus(String name) {
        this.name = name;
    }

    public static TrainingsFocus getValueByName(String name) {
        switch (name) {
        case "Muskelaufbau":
            return TrainingsFocus.MUSCLE;
        case "Ausdauer":
            return TrainingsFocus.STAMINA;
        case "Gewichtsreduktion":
            return TrainingsFocus.WEIGHT;
        default : return null;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}

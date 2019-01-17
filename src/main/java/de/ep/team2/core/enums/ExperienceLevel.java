package de.ep.team2.core.enums;

public enum ExperienceLevel {

    BEGINNER("Anfänger"),
    MEDIUM("Fortgeschritten"),
    EXPERT("Profi");

    private final String name;

    ExperienceLevel(String name) {
        this.name = name;
    }

    public static ExperienceLevel getValueByName(String name) {
        switch (name) {
        case "Anfänger":
            return ExperienceLevel.BEGINNER;
        case "Fortgeschritten":
            return ExperienceLevel.MEDIUM;
        case "Profi":
            return ExperienceLevel.EXPERT;
        default:
            return null;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}

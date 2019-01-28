package de.ep.team2.core.enums;

public enum Gender {

    MALE("Männlich"),
    FEMALE("Weiblich");

    private final String name;

    Gender(String name) {
        this.name = name;
    }

    public static Gender getValueByName(String name) {
        if (name == null) {
            return null;
        } else {
            switch (name) {
            case "Männlich":
                return Gender.MALE;
            case "Weiblich":
                return Gender.FEMALE;
            default:
                return null;
            }
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getConst() {
        if (this.name.equals("Männlich")) {
            return "MALE";
        } else if (this.name.equals("Weiblich")) {
            return "FEMALE";
        } else {
            return null;
        }
    }
}

package de.ep.team2.core.enums;

public enum ImageType {

    MUSCLE_IMAGE("muscle"),
    OTHER_IMAGE("other");

    private final String name;

    ImageType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

package de.ep.team2.core.enums;

public enum MuscleGroup {

    BRUST("Brust"), BIZEPS("Bizeps"), TRIZEPS("Trizeps"), UNTERARME("Unterarme"),
    OBERER_RUECKEN("Oberer Rücken"), RUECKEN_UNTEN("Unterer Rücken"), WADEN("Waden"),
    OBERSCHENKEL("Oberschenkel"), SEITLICHE_ABS("Seitliche Bauchmuskeln"),
    ABDOMINALS("Mittlere Bauchmuskeln"), SCHUTLER("Schulter"), NACKEN("Nacken");

    private final String name;

    MuscleGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

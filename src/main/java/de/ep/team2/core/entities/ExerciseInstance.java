package de.ep.team2.core.entities;

public class ExerciseInstance {

    private Exercise isExercise;
    private TrainingsSession trainingsSession;
    private int id;
    private int repetitionMaxiumum;
    private int sets;
    private int[] reps;
    private String tempo;
    private int pauseInSec;

    public ExerciseInstance(Exercise isExercise,
                            TrainingsSession trainingsSession, int id, int repetitionMaxiumum,
                            int sets, int[] reps, String tempo, int pauseInSec) {
        this.isExercise = isExercise;
        this.trainingsSession = trainingsSession;
        this.id = id;
        this.repetitionMaxiumum = repetitionMaxiumum;
        this.sets = sets;
        this.reps = reps;
        this.tempo = tempo;
        this.pauseInSec = pauseInSec;
    }

    public Exercise getIsExercise() {
        return isExercise;
    }

    public void setIsExercise(Exercise isExercise) {
        this.isExercise = isExercise;
    }

    public TrainingsSession getTrainingsSession() {
        return trainingsSession;
    }

    public void setTrainingsSession(TrainingsSession trainingsSession) {
        this.trainingsSession = trainingsSession;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRepetitionMaxiumum() {
        return repetitionMaxiumum;
    }

    public void setRepetitionMaxiumum(int repetitionMaxiumum) {
        this.repetitionMaxiumum = repetitionMaxiumum;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int[] getReps() {
        return reps;
    }

    public void setReps(int[] reps) {
        this.reps = reps;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public int getPauseInSec() {
        return pauseInSec;
    }

    public void setPauseInSec(int pauseInSec) {
        this.pauseInSec = pauseInSec;
    }
}

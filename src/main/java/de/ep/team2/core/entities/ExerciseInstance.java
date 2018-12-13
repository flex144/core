package de.ep.team2.core.entities;

public class ExerciseInstance {

    private int isExerciseID;
    private int trainingsSessionID;
    private int id;
    private int repetitionMaximum;
    private int sets;
    private Integer[] reps;
    private String tempo;
    private int pauseInSec;

    public ExerciseInstance(int isExerciseID, int trainingsSessionID, int id,
                            int repetitionMaxiumum, int sets, Integer[] reps,
                            String tempo, int pauseInSec) {
        this.isExerciseID = isExerciseID;
        this.trainingsSessionID = trainingsSessionID;
        this.id = id;
        this.repetitionMaximum = repetitionMaxiumum;
        this.sets = sets;
        this.reps = reps;
        this.tempo = tempo;
        this.pauseInSec = pauseInSec;
    }

    public int getIsExerciseID() {
        return isExerciseID;
    }

    public void setIsExerciseID(int isExerciseID) {
        this.isExerciseID = isExerciseID;
    }

    public int getTrainingsSessionID() {
        return trainingsSessionID;
    }

    public void setTrainingsSessionID(int trainingsSessionID) {
        this.trainingsSessionID = trainingsSessionID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRepetitionMaximum() {
        return repetitionMaximum;
    }

    public void setRepetitionMaximum(int repetitionMaximum) {
        this.repetitionMaximum = repetitionMaximum;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public Integer[] getReps() {
        return reps;
    }

    public void setReps(Integer[] reps) {
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

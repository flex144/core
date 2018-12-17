package de.ep.team2.core.entities;

public class ExerciseInstance {

    private int isExerciseID;
    private String category;
    private String description;
    private int trainingsSessionID;
    private int id;
    private int repetitionMaximum;
    private int sets;
    private Integer[] reps;
    private String tempo;
    private int pauseInSec;

    public ExerciseInstance(int isExerciseID, String category, String description,
                            int trainingsSessionID, int id, int repetitionMaximum, int sets,
                            Integer[] reps, String tempo, int pauseInSec) {
        this.isExerciseID = isExerciseID;
        this.category = category;
        this.description = description;
        this.trainingsSessionID = trainingsSessionID;
        this.id = id;
        this.repetitionMaximum = repetitionMaximum;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

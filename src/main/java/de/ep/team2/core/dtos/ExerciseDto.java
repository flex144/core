package de.ep.team2.core.dtos;

import de.ep.team2.core.entities.Exercise;

public class ExerciseDto {

    private Integer indexInList = null;
    private int idUserPlan;
    private int idExerciseInstance;
    private Exercise exercise;
    private String category;
    private int sets;
    private String tempo;
    private int pause;
    private Integer[] reps;
    private Integer[] weights;
    private boolean done;

    private boolean isFirstTraining;
    private Integer weightDone;
    private int repMax;

    // Not handled yet

    private Integer[] repsDone; // reps he actually did
    private Integer exhaustion; // rating the exercise


    public ExerciseDto(int idUserPlan, int idExerciseInstance, Exercise exercise, String category, int sets, String tempo, int pause,
                       Integer[] reps, Integer[] weights, Boolean isFirstTraining, int repMax) {
        this.idUserPlan = idUserPlan;
        this.idExerciseInstance = idExerciseInstance;
        this.exercise = exercise;
        this.category = category;
        this.sets = sets;
        this.tempo = tempo;
        this.pause = pause;
        this.reps = reps;
        this.weights = weights;
        this.isFirstTraining = isFirstTraining;
        this.repMax = repMax;
        this.done = false;
    }

    public ExerciseDto() {
    }

    public int getIdUserPlan() {
        return idUserPlan;
    }

    public void setIdUserPlan(int idUserPlan) {
        this.idUserPlan = idUserPlan;
    }

    public int getIdExerciseInstance() {
        return idExerciseInstance;
    }

    public void setIdExerciseInstance(int idExerciseInstance) {
        this.idExerciseInstance = idExerciseInstance;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public int getPause() {
        return pause;
    }

    public void setPause(int pause) {
        this.pause = pause;
    }

    public Integer[] getReps() {
        return reps;
    }

    public void setReps(Integer[] reps) {
        this.reps = reps;
    }

    public Integer[] getWeights() {
        return weights;
    }

    public void setWeights(Integer[] weights) {
        this.weights = weights;
    }

    public boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public boolean getFirstTraining() {
        return isFirstTraining;
    }

    public void setFirstTraining(Boolean firstTraining) {
        isFirstTraining = firstTraining;
    }

    public Integer getWeightDone() {
        return weightDone;
    }

    public void setWeightDone(Integer weightDone) {
        this.weightDone = weightDone;
    }

    public int getRepMax() {
        return repMax;
    }

    public void setRepMax(int repMax) {
        this.repMax = repMax;
    }

    public Integer[] getRepsDone() {
        return repsDone;
    }

    public void setRepsDone(Integer[] repsDone) {
        this.repsDone = repsDone;
    }

    public Integer getExhaustion() {
        return exhaustion;
    }

    public void setExhaustion(Integer exhaustion) {
        this.exhaustion = exhaustion;
    }

    public Integer getIndexInList() {
        return indexInList;
    }

    public void setIndexInList(Integer indexInList) {
        this.indexInList = indexInList;
    }
}

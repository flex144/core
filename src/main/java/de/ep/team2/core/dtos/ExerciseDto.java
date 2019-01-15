package de.ep.team2.core.dtos;

import de.ep.team2.core.entities.Exercise;

public class ExerciseDto {

    private Exercise exercise;
    private String category;
    private int sets;
    private String tempo;
    private int pause;
    private Integer[] reps;
    private Integer[] weights;

    private Boolean isFirstTraining;
    private Integer[] weightDone;
    private int repMax;

    public ExerciseDto(Exercise exercise, String category, int sets, String tempo, int pause,
                       Integer[] reps, Integer[] weights, Boolean isFirstTraining, Integer[] weightDone,
                       int repMax) {
        this.exercise = exercise;
        this.category = category;
        this.sets = sets;
        this.tempo = tempo;
        this.pause = pause;
        this.reps = reps;
        this.weights = weights;
        this.isFirstTraining = isFirstTraining;
        this.weightDone = weightDone;
        this.repMax = repMax;
    }

    public ExerciseDto() {
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

    public Boolean getFirstTraining() {
        return isFirstTraining;
    }

    public void setFirstTraining(Boolean firstTraining) {
        isFirstTraining = firstTraining;
    }

    public Integer[] getWeightDone() {
        return weightDone;
    }

    public void setWeightDone(Integer[] weightDone) {
        this.weightDone = weightDone;
    }

    public int getRepMax() {
        return repMax;
    }

    public void setRepMax(int repMax) {
        this.repMax = repMax;
    }
}

package de.ep.team2.core.entities;

import java.util.ArrayList;

public class TrainingsSession {

    private int id;
    private int ordering;
    private int exerciseInstanceId;
    private int sets;
    private Integer[] weightDiff;
    private Integer[] reps;
    private String tempo;
    private int pauseInSec;

    public TrainingsSession(int id, int ordering, int exerciseInstanceId, int sets,
                            Integer[] weightDiff, Integer[] reps, String tempo, int pauseInSec) {
        this.id = id;
        this.ordering = ordering;
        this.exerciseInstanceId = exerciseInstanceId;
        this.sets = sets;
        this.weightDiff = weightDiff;
        this.reps = reps;
        this.tempo = tempo;
        this.pauseInSec = pauseInSec;
    }

    public TrainingsSession() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
    }

    public int getExerciseInstanceId() {
        return exerciseInstanceId;
    }

    public void setExerciseInstanceId(int exerciseInstanceId) {
        this.exerciseInstanceId = exerciseInstanceId;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public Integer[] getWeightDiff() {
        return weightDiff;
    }

    public void setWeightDiff(Integer[] weightDiff) {
        this.weightDiff = weightDiff;
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

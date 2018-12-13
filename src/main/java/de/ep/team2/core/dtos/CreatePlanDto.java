package de.ep.team2.core.dtos;

import java.util.List;

public class CreatePlanDto {

    private String planName;
    private String trainingsFocus; // todo
    private Integer sessionNums;
    private String category;
    private String exerciseID;
    private String description;
    private List<String> sets;
    private List<String> tempo;
    private List<Integer> pause;

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getTrainingsFocus() {
        return trainingsFocus;
    }

    public void setTrainingsFocus(String trainingsFocus) {
        this.trainingsFocus = trainingsFocus;
    }

    public Integer getSessionNums() {
        return sessionNums;
    }

    public void setSessionNums(Integer sessionNums) {
        this.sessionNums = sessionNums;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(String exerciseID) {
        this.exerciseID = exerciseID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSets() {
        return sets;
    }

    public void setSets(List<String> sets) {
        this.sets = sets;
    }

    public List<String> getTempo() {
        return tempo;
    }

    public void setTempo(List<String> tempo) {
        this.tempo = tempo;
    }

    public List<Integer> getPause() {
        return pause;
    }

    public void setPause(List<Integer> pause) {
        this.pause = pause;
    }
}

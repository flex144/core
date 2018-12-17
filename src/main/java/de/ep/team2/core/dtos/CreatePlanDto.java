package de.ep.team2.core.dtos;

import de.ep.team2.core.entities.Exercise;
import de.ep.team2.core.service.ExerciseService;

import java.util.ArrayList;

public class CreatePlanDto {

    private String planName;
    private String trainingsFocus; // todo as enum
    private Integer id; // todo hidden field for id
    private Integer sessionNums;
    private String category;
    private String exerciseName;
    private Integer exerciseID; // todo pass id instead of name
    private String description;
    private ArrayList<String> sets;
    private ArrayList<String> tempo;
    private ArrayList<Integer> pause;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public Integer getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(Integer exerciseID) {
        this.exerciseID = exerciseID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getSets() {
        return sets;
    }

    public void setSets(ArrayList<String> sets) {
        this.sets = sets;
    }

    public ArrayList<String> getTempo() {
        return tempo;
    }

    public void setTempo(ArrayList<String> tempo) {
        this.tempo = tempo;
    }

    public ArrayList<Integer> getPause() {
        return pause;
    }

    public void setPause(ArrayList<Integer> pause) {
        this.pause = pause;
    }

    public Integer nameToId() {
        if (exerciseName != null) {
            ExerciseService service = new ExerciseService();
            Exercise exercise = service.getExerciseByName(exerciseName);
            if (exercise != null) {
                exerciseID = exercise.getId();
            } else {
                throw new IllegalArgumentException("Exercise doesn't exist!");
            }
        } else {
            throw new IllegalArgumentException("No exercise name provided!");
        }
        return exerciseID;
    }
}

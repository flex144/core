package de.ep.team2.core.dtos;

import de.ep.team2.core.entities.Exercise;
import de.ep.team2.core.service.ExerciseService;

import java.util.LinkedList;

public class CreatePlanDto {

    private String planName;
    private String trainingsFocus; // todo as enum
    private Integer id; // todo hidden field for id
    private Integer sessionNums;
    private String category;
    private String exerciseName;
    private Integer exerciseID;
    private LinkedList<String> sets;
    private LinkedList<String> tempo;
    private LinkedList<Integer> pause;
    private LinkedList<String> tags;

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

    public LinkedList<String> getSets() {
        return sets;
    }

    public void setSets(LinkedList<String> sets) {
        this.sets = sets;
    }

    public LinkedList<String> getTempo() {
        return tempo;
    }

    public void setTempo(LinkedList<String> tempo) {
        this.tempo = tempo;
    }

    public LinkedList<Integer> getPause() {
        return pause;
    }

    public void setPause(LinkedList<Integer> pause) {
        this.pause = pause;
    }

    public LinkedList<String> getTags() {
        return tags;
    }

    public void setTags(LinkedList<String> tags) {
        this.tags = tags;
    }

    /**
     * Checks if the Exercise name of this object is set and if an exercise with this name exists.
     * When not Throws an Exception. Otherwise takes the id of the exercise saved in the database and sets it as the exercise id of this object.
     *
     * @return The Id of the exercise which is determined by {@code exerciseName}.
     */
    public Integer nameToId() {
        if (this.exerciseName != null) {
            ExerciseService service = new ExerciseService();
            Exercise exercise = service.getExerciseByName(this.exerciseName);
            if (exercise != null) {
                this.exerciseID = exercise.getId();
            } else {
                throw new IllegalArgumentException("Exercise doesn't exist!");
            }
        } else {
            throw new IllegalArgumentException("No exercise name provided!");
        }
        return exerciseID;
    }
}

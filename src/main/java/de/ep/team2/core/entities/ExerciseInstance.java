package de.ep.team2.core.entities;

import java.util.LinkedList;

public class ExerciseInstance {

    private int planTemplateID;
    private int isExerciseID;
    private int id;
    private String category;
    private String description;
    private LinkedList<TrainingsSession> trainingsSessions;

    public ExerciseInstance(int planTemplateID, int isExerciseID, int id, String category,
                            String description, LinkedList<TrainingsSession> trainingsSessions) {
        this.planTemplateID = planTemplateID;
        this.isExerciseID = isExerciseID;
        this.id = id;
        this.category = category;
        this.description = description;
        this.trainingsSessions = trainingsSessions;
    }

    public int getPlanTemplateID() {
        return planTemplateID;
    }

    public void setPlanTemplateID(int planTemplateID) {
        this.planTemplateID = planTemplateID;
    }

    public int getIsExerciseID() {
        return isExerciseID;
    }

    public void setIsExerciseID(int isExerciseID) {
        this.isExerciseID = isExerciseID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public LinkedList<TrainingsSession> getTrainingsSessions() {
        return trainingsSessions;
    }

    public void setTrainingsSessions(LinkedList<TrainingsSession> trainingsSessions) {
        this.trainingsSessions = trainingsSessions;
    }
}

package de.ep.team2.core.entities;

import java.util.LinkedList;

public class ExerciseInstance {

    private int planTemplateID;
    private int isExerciseID;
    private int id;
    private String name;
    private String category;
    private LinkedList<String> tags;
    public LinkedList<TrainingsSession> trainingsSessions;

    public ExerciseInstance(int planTemplateID, int isExerciseID, int id, String category,
                            LinkedList<String> tags, LinkedList<TrainingsSession> trainingsSessions,
                            String name) {
        this.planTemplateID = planTemplateID;
        this.isExerciseID = isExerciseID;
        this.id = id;
        this.category = category;
        this.tags = tags;
        this.trainingsSessions = trainingsSessions;
        this.name = name;
    }

    public ExerciseInstance() {}

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LinkedList<String> getTags() {
        return tags;
    }

    public void setTags(LinkedList<String> description) {
        this.tags = description;
    }

    public LinkedList<TrainingsSession> getTrainingsSessions() {
        return trainingsSessions;
    }

    public void setTrainingsSessions(LinkedList<TrainingsSession> trainingsSessions) {
        this.trainingsSessions = trainingsSessions;
    }

    public String tagsToString() {
        String toReturn = "";

        for (int i = 0; i < tags.size(); i++) {
            toReturn = toReturn + tags.get(i);
            if (i != tags.size() - 1) {
                toReturn = toReturn + ", ";
            }
        }

        return toReturn;
    }

}
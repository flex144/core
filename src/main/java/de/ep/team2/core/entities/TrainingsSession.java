package de.ep.team2.core.entities;

import java.util.LinkedList;

public class TrainingsSession {

    private int id;
    private int planTemplateID;
    private int ordering;
    private LinkedList<ExerciseInstance> exerciseInstances;

    public TrainingsSession(int id, int planTemplateID, int ordering, LinkedList<ExerciseInstance> exerciseInstances) {
        this.id = id;
        this.planTemplateID = planTemplateID;
        this.ordering = ordering;
        this.exerciseInstances = exerciseInstances;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getPlanTemplateID() {
        return planTemplateID;
    }

    public void setPlanTemplateID(int planTemplateID) {
        this.planTemplateID = planTemplateID;
    }

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
    }

    public LinkedList<ExerciseInstance> getExerciseInstances() {
        return exerciseInstances;
    }

    public void setExerciseInstances(LinkedList<ExerciseInstance> exerciseInstances) {
        this.exerciseInstances = exerciseInstances;
    }
}

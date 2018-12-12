package de.ep.team2.core.entities;

import java.util.LinkedList;

public class TrainingsPlanTemplate {

    private int id;
    private String name;
    private String description;
    private User author;
    private boolean oneShotPlan;
    private int numTrainSessions;
    private int exercisesPerSession;
    private LinkedList<TrainingsSession> trainingsSessions;

    public TrainingsPlanTemplate(int id, String name, String description, User author, boolean oneShotPlan,
                                 int numTrainSessions, int exercisesPerSession,
                                 LinkedList<TrainingsSession> trainingsSessions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.author = author;
        this.oneShotPlan = oneShotPlan;
        this.numTrainSessions = numTrainSessions;
        this.exercisesPerSession = exercisesPerSession;
        this.trainingsSessions = trainingsSessions;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public boolean isOneShotPlan() {
        return oneShotPlan;
    }

    public void setOneShotPlan(boolean oneShotPlan) {
        this.oneShotPlan = oneShotPlan;
    }

    public int getNumTrainSessions() {
        return numTrainSessions;
    }

    public void setNumTrainSessions(int numTrainSessions) {
        this.numTrainSessions = numTrainSessions;
    }

    public int getExercisesPerSession() {
        return exercisesPerSession;
    }

    public void setExercisesPerSession(int exercisesPerSession) {
        this.exercisesPerSession = exercisesPerSession;
    }

    public LinkedList<TrainingsSession> getTrainingsSessions() {
        return trainingsSessions;
    }

    public void setTrainingsSessions(LinkedList<TrainingsSession> trainingsSessions) {
        this.trainingsSessions = trainingsSessions;
    }
}

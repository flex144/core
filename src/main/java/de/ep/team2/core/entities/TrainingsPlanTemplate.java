package de.ep.team2.core.entities;

import java.util.LinkedList;

public class TrainingsPlanTemplate {

    private int id;
    private String name;
    private String trainingsFocus;
    private User author;
    private boolean oneShotPlan;
    private int numTrainSessions;
    private int exercisesPerSession;
    private LinkedList<ExerciseInstance> exerciseInstances;

    public TrainingsPlanTemplate(int id, String name, String trainingsFocus, User author, boolean oneShotPlan,
                                 int numTrainSessions, int exercisesPerSession,
                                 LinkedList<ExerciseInstance> exerciseInstances) {
        this.id = id;
        this.name = name;
        this.trainingsFocus = trainingsFocus;
        this.author = author;
        this.oneShotPlan = oneShotPlan;
        this.numTrainSessions = numTrainSessions;
        this.exercisesPerSession = exercisesPerSession;
        this.exerciseInstances = exerciseInstances;
    }

    public TrainingsPlanTemplate() {
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

    public String getTrainingsFocus() {
        return trainingsFocus;
    }

    public void setTrainingsFocus(String trainingsFocus) {
        this.trainingsFocus = trainingsFocus;
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

    public LinkedList<ExerciseInstance> getExerciseInstances() {
        return exerciseInstances;
    }

    public void setTrainingsSessions(LinkedList<ExerciseInstance> trainingsSessions) {
        this.exerciseInstances = exerciseInstances;
    }
}

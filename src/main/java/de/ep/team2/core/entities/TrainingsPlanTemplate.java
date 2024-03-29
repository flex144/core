package de.ep.team2.core.entities;

import de.ep.team2.core.enums.ExperienceLevel;
import de.ep.team2.core.enums.TrainingsFocus;

import java.util.LinkedList;

public class TrainingsPlanTemplate {

    private int id;
    private String name;
    private TrainingsFocus trainingsFocus;
    private ExperienceLevel targetGroup;
    private User author;
    private boolean oneShotPlan;
    private int recomSessionsPerWeek;
    private int numTrainSessions;
    private int exercisesPerSession;
    private LinkedList<ExerciseInstance> exerciseInstances;
    private boolean confirmed;

    public TrainingsPlanTemplate(int id, String name, TrainingsFocus trainingsFocus, ExperienceLevel targetGroup, User author, boolean oneShotPlan,
                                 int recomSessionsPerWeek, int numTrainSessions, int exercisesPerSession,
                                 LinkedList<ExerciseInstance> exerciseInstances, boolean confirmed) {
        this.id = id;
        this.name = name;
        this.trainingsFocus = trainingsFocus;
        this.targetGroup = targetGroup;
        this.author = author;
        this.oneShotPlan = oneShotPlan;
        this.recomSessionsPerWeek = recomSessionsPerWeek;
        this.numTrainSessions = numTrainSessions;
        this.exercisesPerSession = exercisesPerSession;
        this.exerciseInstances = exerciseInstances;
        this.confirmed = confirmed;
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

    public TrainingsFocus getTrainingsFocus() {
        return trainingsFocus;
    }

    public void setTrainingsFocus(TrainingsFocus trainingsFocus) {
        this.trainingsFocus = trainingsFocus;
    }

    public ExperienceLevel getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(ExperienceLevel targetGroup) {
        this.targetGroup = targetGroup;
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

    public int getRecomSessionsPerWeek() {
        return recomSessionsPerWeek;
    }

    public void setRecomSessionsPerWeek(int recomSessionsPerWeek) {
        this.recomSessionsPerWeek = recomSessionsPerWeek;
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

    public void setExerciseInstances(LinkedList<ExerciseInstance> exerciseInstances) {
        this.exerciseInstances = exerciseInstances;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    /**
     * Returns a String representation of the boolean 'confirmed'.
     * @return "Ja" if confirmed, "Nein" if otherwise
     */
    public String confirmed() {
        if(confirmed) {
            return "Ja";
        } else {
            return "Nein";
        }
    }
}

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

    public void setExerciseInstances(LinkedList<ExerciseInstance> exerciseInstances) {
        this.exerciseInstances = exerciseInstances;
    }

    /**
    public void addExerciseInstance (ExerciseInstance exerciseInstance) {
        this.exerciseInstances.add(exerciseInstance);
    }
     */

    public static void main(String[] args) {
        Integer[] reps = {1, 2, 3};
        LinkedList<String> llst= new LinkedList<String>();
        llst.add("1");
        llst.add("2");
        LinkedList<TrainingsSession> ll = new LinkedList<TrainingsSession>();
        TrainingsSession ts1 = new TrainingsSession(1, 2,3,4,5,reps,"asd",1);
        TrainingsSession ts2 = new TrainingsSession(2, 2,3,4,5,reps,"asd",1);
        ll.add(ts1);
        ll.add(ts2);
        ExerciseInstance ei1 = new ExerciseInstance(1,2,3,"asd", llst, ll, "asd");
        LinkedList<ExerciseInstance> llei= new LinkedList<ExerciseInstance>();
        llei.add(ei1);
        TrainingsPlanTemplate tpt = new TrainingsPlanTemplate();
        tpt.setExerciseInstances(llei);

        System.out.println(tpt.exerciseInstances.getFirst().getTrainingsSessions().getFirst().getId());
    }
}

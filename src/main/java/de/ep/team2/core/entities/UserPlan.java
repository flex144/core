package de.ep.team2.core.entities;

public class UserPlan {

    private int id;
    private String userMail;
    private int idOfTemplate;
    private int currentSession;
    private int maxSession;
    private boolean initialTrainingDone;

    public UserPlan(int id, String userMail, int idOfTemplate, int currentSession, int maxSession
            , boolean initialTrainingDone) {
        this.id = id;
        this.userMail = userMail;
        this.idOfTemplate = idOfTemplate;
        this.currentSession = currentSession;
        this.maxSession = maxSession;
        this.initialTrainingDone = initialTrainingDone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public int getIdOfTemplate() {
        return idOfTemplate;
    }

    public void setIdOfTemplate(int idOfTemplate) {
        this.idOfTemplate = idOfTemplate;
    }

    public int getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(int currentSession) {
        this.currentSession = currentSession;
    }

    public int getMaxSession() {
        return maxSession;
    }

    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }

    public boolean isInitialTrainingDone() {
        return initialTrainingDone;
    }

    public void setInitialTrainingDone(boolean initialTrainingDone) {
        this.initialTrainingDone = initialTrainingDone;
    }
}

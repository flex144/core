package de.ep.team2.core.entities;

public class TrainingsSession {

    private int id;
    private TrainingsSession trainingsSession;

    public TrainingsSession(int id, TrainingsSession trainingsSession) {
        this.id = id;
        this.trainingsSession = trainingsSession;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TrainingsSession getTrainingsSession() {
        return trainingsSession;
    }

    public void setTrainingsSession(TrainingsSession trainingsSession) {
        this.trainingsSession = trainingsSession;
    }
}

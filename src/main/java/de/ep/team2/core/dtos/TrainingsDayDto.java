package de.ep.team2.core.dtos;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
@Scope("session")
public class TrainingsDayDto {

    private LinkedList<ExerciseDto> exercises;
    private Boolean initialTraining;
    private String currentCategory;
    private Integer currentSession;
    private boolean trainingStarted;

    public TrainingsDayDto() {
        trainingStarted = false;
    }

    public LinkedList<ExerciseDto> getExercises() {
        return exercises;
    }

    /**
     * Adds the exerciseDTO List to the DayDTO and calls a method on each exerciseDTO to save its
     * position in the List there.
     * @param exercises List of Exercises to add.
     */
    public void setExercises(LinkedList<ExerciseDto> exercises) {
        this.exercises = exercises;
        for (int i = 0; i < exercises.size(); i++) {
            exercises.get(i).setIndexInList(i);
        }
    }

    /**
     * Changes the Exercise Dto in the list on the specific position to the new one.
     *
     * @param newDto DTO to replace the old one with.
     * @param pos position of the old dto in the list starting at 0.
     */
    public void changeExercise(ExerciseDto newDto, int pos) {
        if (exercises != null && pos <= exercises.size()) {
            exercises.set(pos, newDto);
        } else {
            throw new IllegalArgumentException("exercises not defined or index out of bounds");
        }
    }

    public boolean isInitialTraining() {
        return initialTraining;
    }

    public void setInitialTraining(boolean initialTraining) {
        this.initialTraining = initialTraining;
    }

    public String getCurrentCategory() {
        return currentCategory;
    }

    public void setCurrentCategory(String currentCategory) {
        this.currentCategory = currentCategory;
    }

    public Integer getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Integer currentSession) {
        this.currentSession = currentSession;
    }

    public boolean isTrainingStarted() {
        return trainingStarted;
    }

    public void setTrainingStarted(boolean trainingStarted) {
        this.trainingStarted = trainingStarted;
    }

    /**
     * resets the DTO without having to invalidate the session.
     */
    public void clear() {
        this.exercises = null;
        this.initialTraining = null;
        this.currentCategory = null;
        this.currentSession = null;
        this.trainingStarted = false;
    }
}

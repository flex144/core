package de.ep.team2.core.dtos;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
@Scope("session")
public class TrainingsDayDto {

    private LinkedList<ExerciseDto> exercises;
    private Boolean initialTraining;

    public LinkedList<ExerciseDto> getExercises() {
        return exercises;
    }

    public void setExercises(LinkedList<ExerciseDto> exercises) {
        this.exercises = exercises;
        for (int i = 0; i < exercises.size(); i++) {
            exercises.get(i).setIndexInList(i);
        }
    }

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

    public void clear() {
        this.exercises = null;
        this.initialTraining = null;
    }
}

package de.ep.team2.core.service;

import de.ep.team2.core.entities.Exercise;

public class ExerciseService {

    public Exercise getExerciseById(int id) {
        return DataBaseService.getInstance().getExerciseById(id);
    }

    public boolean exerciseNameUnique(String name) {
        DataBaseService db = DataBaseService.getInstance();
        return db.exerciseNameUnique(name);
    }

    public Integer insertExercise(String name, String description, String imgPath) {
        return DataBaseService.getInstance().insertExercise(name, description, imgPath);
    }
}

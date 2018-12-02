package de.ep.team2.core.service;

import de.ep.team2.core.entities.Exercise;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;

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

    public String uploadImg(MultipartFile image, Exercise exercise) {
        String fileName = image.getOriginalFilename();
        String dirDestString = "src/main/resources/static/images/" +  exercise.getName();
        Path dirDest = Paths.get(dirDestString);
        Path dest = Paths.get( dirDestString, fileName);
        try {
            Files.createDirectories(dirDest);
            Files.createFile(dest);
            image.transferTo(dest);
        } catch (IOException exception) {
            return null;
        }
        return "/images/" + exercise.getName() + "/" + fileName;
    }
}

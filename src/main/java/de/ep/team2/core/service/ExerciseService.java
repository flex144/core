package de.ep.team2.core.service;

import de.ep.team2.core.entities.Exercise;
import de.ep.team2.core.enums.ImageType;
import de.ep.team2.core.enums.WeightType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Forwards requests to the Database Service and manages the Storage of the
 * Exercise images. Tested with Acceptance Test.
 */
public class ExerciseService {

    public Exercise getExerciseById(int id) {
        return DataBaseService.getInstance().getExerciseById(id);
    }

    public boolean exerciseNameUnique(String name) {
        DataBaseService db = DataBaseService.getInstance();
        return db.exerciseNameUnique(name);
    }

    /**
     * Forwards the insert Exercise request to the DatabaseService.
     * Merges the two lists of image paths to one and adds an attribute which
     * type of image the file is.
     *
     * @param name name of the exercise to insert.
     * @param description optional description of the exercise to insert.
     * @param weightType weight type of the exercise to insert.
     * @param link optional link to a example video of the exercise.
     * @param muscleImgPaths optional Paths to the muscle images.
     * @param otherImgPaths optional Paths to the other images.
     * @return The id of the just inserted exercise.
     */
    public Integer insertExercise(String name, String description,
                                  WeightType weightType, String link,
                                  List<String> muscleImgPaths, List<String> otherImgPaths) {
        LinkedList<String[]> imgPaths = new LinkedList<>();
        if (muscleImgPaths != null) {
            for (String s : muscleImgPaths) {
                imgPaths.add(new String[]{s, ImageType.MUSCLE_IMAGE.toString()});
            }
        }
        if (otherImgPaths != null) {
            for (String s : otherImgPaths) {
                imgPaths.add(new String[]{s, ImageType.OTHER_IMAGE.toString()});
            }
        }
        return DataBaseService.getInstance().insertExercise(name, description,
                weightType, link, imgPaths);
    }

    /**
     * Saves the given image to the folder static/images.
     * Creates a subfolder named after the exercise the image belongs to.
     *
     * @param image image to save.
     * @param exercise exercise of the image.
     * @return The relative path, used by thymeleaf ,where the image is saved.
     *          null when an errors appears.
     */
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

    /**
     * Forwards the delete request to the DatabaseService.
     * Also deletes all saved images and the folder which belongs to the exercise
     * in static/images.
     *
     * @param id id of the exercise to delete.
     */
    public void deleteExercise(int id) {
        DataBaseService db = DataBaseService.getInstance();
        Exercise toDelete = db.getExerciseById(id);
        String dirDestString = "src/main/resources/static/images/" +  toDelete.getName();
        Path dirDest = Paths.get(dirDestString);
        try {
            if (!toDelete.getOtherImgPaths().isEmpty()) {
                for (String s : toDelete.getOtherImgPaths()) {
                    Path dest = Paths.get("src/main/resources/static" + s);
                    Files.delete(dest);
                }
            }
            if (!toDelete.getMuscleImgPaths().isEmpty()) {
                for (String s : toDelete.getMuscleImgPaths()) {
                    Path dest = Paths.get("src/main/resources/static" + s);
                    Files.delete(dest);
                }
            }
            Files.delete(dirDest);
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
            return;
        }
        db.deleteExerciseById(id);
    }
}

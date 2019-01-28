package de.ep.team2.core.service;

import de.ep.team2.core.entities.Exercise;
import de.ep.team2.core.entities.ExerciseInstance;
import de.ep.team2.core.entities.TrainingsPlanTemplate;
import de.ep.team2.core.enums.ImageType;
import de.ep.team2.core.enums.WeightType;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Forwards requests to the Database Service and manages the Storage of the
 * Exercise images. Tested with Acceptance Test.
 */
public class ExerciseService {

    private final boolean deployOnTomcat = false; // 'true' to deploy on tomcat

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
        LinkedList<String[]> imgPaths = uniteImgPaths(muscleImgPaths, otherImgPaths);
        return DataBaseService.getInstance().insertExercise(name, description,
                weightType, link, imgPaths);
    }

    private LinkedList<String[]> uniteImgPaths(List<String> muscleImgPaths, List<String> otherImgPaths) {
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
        return imgPaths;
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
        String dirDestString;
        // Used to adjust File Path to IDE or Tomcat.
        if (deployOnTomcat) {
            // path to files on tomcat
            dirDestString = "webapps/team2/WEB-INF/classes/static/images/Exercises/" +  exercise.getName();
        } else {
            // path to files in IDE
            dirDestString = "src/main/resources/static/images/Exercises/" +  exercise.getName();
        }
        Path dirDest = Paths.get(dirDestString);
        Path dest = Paths.get( dirDestString, fileName);
        try {
            Files.createDirectories(dirDest);
            Files.createFile(dest);
            image.transferTo(dest);
        } catch (IOException exception) {
            return null;
        }
        return "/images/Exercises/" + exercise.getName() + "/" + fileName;
    }

    /**
     * Forwards the delete request to the DatabaseService.
     * Also deletes all saved images and the folder which belongs to the exercise
     * in static/images.
     *
     * If the exercise is still referenced in a Plan throws an Exception with an Error message with
     * all the Plans where it appears.
     *
     * @param id id of the exercise to delete.
     */
    public void deleteExercise(int id) {
        DataBaseService db = DataBaseService.getInstance();
        LinkedList<ExerciseInstance> dependentInstances = db.getInstancesOfExercise(id);
        if (dependentInstances == null || dependentInstances.isEmpty()) {
            Exercise toDelete = db.getExerciseById(id);
            try {
                String dirDestString;
                // Used to adjust File Path to IDE or Tomcat.
                if (deployOnTomcat) {
                    // path to files on tomcat
                    dirDestString = "webapps/team2/WEB-INF/classes/static/images/Exercises/" +  toDelete.getName();
                } else {
                    // path to files in IDE
                    dirDestString = "src/main/resources/static/images/Exercises/" +  toDelete.getName();
                }
                File newFile = new File(dirDestString);
                FileUtils.deleteDirectory(newFile);
            } catch (IOException exception) {
                System.err.println(exception.getMessage());
                return;
            }
            db.deleteExerciseById(id);
        } else {
            LinkedList<TrainingsPlanTemplate> plans = new LinkedList<>();
            LinkedList<ExerciseInstance> instances = db.getInstancesOfExercise(id);
            for (ExerciseInstance instance : instances) {
                plans.add(db.getOnlyPlanTemplateById(instance.getPlanTemplateID()));
            }
            List<TrainingsPlanTemplate> listWithoutDuplicates = plans.stream()
                    .distinct()
                    .collect(Collectors.toList());
            StringBuilder textToDisplay = new StringBuilder();
            for (TrainingsPlanTemplate template : listWithoutDuplicates) {
                textToDisplay.append(template.getName()).append(", ");
            }
            throw new IllegalArgumentException("Übung kann nicht gelöscht werden da Pläne " +
                    "existieren welche diese beinhalten. Lösche oder bearbeite zuvor diese Pläne: "
                    + textToDisplay);
        }
    }

    /**
     * gets a List of all existing exercises in the Database.
     *
     * @return a List of all existing exercises in the Database.
     */
    public List<Exercise> getAllExercises() {
        return DataBaseService.getInstance().getAllExercises();
    }

    /**
     * Returns a List of exercise where the name is part of the name.
     * If name left empty returns all exercises.
     *
     * @param name Name to search for; if left blank returns all exercises.
     * @return List of matching exercises; null if nothing was found.
     */
    public List<Exercise> getExerciseListByName(String name) {
        if (name == null || name.equals("")) {
            return getAllExercises();
        } else {
            return DataBaseService.getInstance().getExerciseListByName(name);
        }
    }

    /**
     * Returns one exercise with the exact name.
     *
     * @param name exact name you are looking for.
     * @return matching exercise or null if nothing was found.
     */
    public Exercise getExerciseByName(String name) {
        return DataBaseService.getInstance().getExerciseByName(name);
    }

    /**
     * Updates all values of the provided Exercise in the Database.
     * Throws exception if the new Name is already in the Database, but saves all other Data.
     * Uploads all new images. (images are not uploaded when the name is not unique)
     *
     * @param exercise Exercise Object which holds the data.
     */
    public void updateExerciseAndAddNewImg(Exercise exercise) {
        DataBaseService.getInstance().updateExerciseWithoutImg(exercise.getId(), exercise.getName(), exercise.getDescription(), exercise.getWeightType(), exercise.getVideoLink());
        DataBaseService.getInstance().addNewImgPaths(exercise.getId(), uniteImgPaths(exercise.getMuscleImgPaths(), exercise.getOtherImgPaths()));
    }

    /**
     * Deletes the image with the provided path from the File System and the Database.
     *
     * @param pathToDelete to specify the File.
     */
    public void deleteImg(String pathToDelete) {
        String dirDestString;
        if (deployOnTomcat) {
            // path to files on tomcat
            dirDestString = "webapps/team2/WEB-INF/classes/static";
        } else {
            // path to files in IDE
            dirDestString = "src/main/resources/static";
        }
        try {
            Path toDelete = Paths.get(dirDestString + pathToDelete);
            Files.delete(toDelete);
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
            return;
        }
        DataBaseService.getInstance().deleteImg(pathToDelete);
    }
}
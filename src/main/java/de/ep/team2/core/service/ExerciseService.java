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
        String dirDestString = "src/main/resources/images/" +  exercise.getName();
        Path dirDest = Paths.get(dirDestString);
        Path dest = Paths.get( exercise.getName(), fileName);
        //FileSystem fs = FileSystems.getDefault();
        //Path dirDest = fs.getPath("src" ,"main/resources/images", exercise.getName());
        //Path dest = fs.getPath("src" ,"main/resources/images", exercise.getName(), fileName);
        try {
            Files.createDirectory(dirDest);
            Files.createFile(dest);
            System.out.println("ok");
            image.transferTo(dest);
        } catch (IOException exception) {
            return exception.getMessage();
        }
        return "Success";
    }

    private void convertPaths(Path path) {
        Path real = null;
        try {
            real = path.toRealPath();
        }
        catch (IOException e) {
            System.out.println("Real path could not be created !");
        }
        System.out.println("Real path: " + real);
    }
}

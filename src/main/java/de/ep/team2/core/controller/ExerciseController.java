package de.ep.team2.core.controller;

import de.ep.team2.core.entities.Exercise;
import de.ep.team2.core.service.ExerciseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/exercises")
public class ExerciseController {

    /**
     * handles get requests for exercises.
     * checks if the provided id is valid and if the exercise exists.
     *
     * @param id the id of the exercise to be displayed
     * @param model the model Thymeleaf uses.
     * @return exercise at success to display the found exercise;
     * error when exercise not found or id invalid.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getExercise(@PathVariable("id") String id, Model model) {
        ExerciseService service = new ExerciseService();
        int idInt;
        try {
            idInt = Integer.parseInt(id);
        } catch (NumberFormatException excception) {
            model.addAttribute("error", "Das war keine gültige ID!");
            return "error";
        }
        Exercise toGet = service.getExerciseById(idInt);
        if (toGet == null) {
            model.addAttribute("error", "Übung nicht gefunden!");
            return "error";
        } else {
            model.addAttribute("exercise", toGet);
            return "exercise";
        }
    }

    /**
     * Handles Post-requests for exercises.
     * Creates a new Exercise in the Database with the information provided by the user.
     * Saves the images to resources/static/images and references them in the Database.
     * Checks if the uploaded images are of the format png or jpeg and allows only them.
     * When the file is to large or the upload fails writes a message to the error console.
     *
     * @param otherImage Array of files uploaded as other images.
     * @param muscleImage Array of files uploaded as muscle images.
     * @param exercise Exercise with data Thymeleaf provides.
     * @param model Model Thymeleaf uses.
     * @return redirects to the mod user search site
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public String postExercise(@RequestParam("otherImage") MultipartFile[] otherImage,
                               @RequestParam("muscleImage") MultipartFile[] muscleImage,
                               @ModelAttribute("exercise") Exercise exercise, Model model) {
        ExerciseService service = new ExerciseService();
        if (!service.exerciseNameUnique(exercise.getName())) {
            model.addAttribute("error", "Übungsname schon vorhanden!");
            return "error";
        } else {
            for (MultipartFile img : otherImage) {
                if (!img.isEmpty() || img.getContentType() != null) {
                    if (img.getContentType().equals("image/jpeg")
                            || img.getContentType().equals("image/png")) {
                        String imgPath = service.uploadImg(img, exercise);
                        if (imgPath == null) {
                            System.err.println("Image upload failed!");
                        } else {
                            exercise.addOtherImgPath(imgPath);
                        }
                    }
                }
            }
            for (MultipartFile img : muscleImage) {
                if (!img.isEmpty() || img.getContentType() != null) {
                    if (img.getContentType().equals("image/jpeg")
                            || img.getContentType().equals("image/png")) {
                        String imgPath = service.uploadImg(img, exercise);
                        if (imgPath == null) {
                            System.err.println("Image upload failed!");
                        } else {
                            exercise.addMuscleImgPath(imgPath);
                        }
                    }
                }
            }
            service.insertExercise(exercise.getName(), exercise.getDescription(),
                    exercise.getWeightType(), exercise.getVideoLink()
                    , exercise.getMuscleImgPaths(), exercise.getOtherImgPaths());
            return "redirect:/mods/searchexercise";
        }
    }

    /**
     * Handles delete requests for exercises.
     * Checks if the id is valid and if the exercise exists.
     *
     * @param id id of the exercise to delete.
     * @param model model thymeleaf uses.
     * @return redirects to mod user search at success; returns the error page
     * when id invalid or user doesn't exist.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String deleteUserById(@PathVariable("id") String id, Model model) {
        ExerciseService service = new ExerciseService();
        if (!UsersController.isInteger(id)) {
            model.addAttribute("error", "ID nicht valide!");
            return "error";
        } else if (service.getExerciseById(Integer.parseInt(id)) == null) {
            model.addAttribute("error", "Übung existiert nicht!");
            return "error";
        } else {
            try {
                service.deleteExercise(Integer.parseInt(id));
            } catch (IllegalArgumentException exception) {
                model.addAttribute("error", exception.getMessage());
                return "error";
            }
            return "redirect:/mods/searchexercise";
        }
    }
}

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
            model.addAttribute("error", "Benutzer nicht gefunden!");
            return "error";
        } else {
            model.addAttribute("exercise", toGet);
            return "exercise";
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String postExercise(@RequestParam("image") MultipartFile image,
                               @ModelAttribute("exercise") Exercise exercise, Model model) {
        ExerciseService service = new ExerciseService();
        if (!service.exerciseNameUnique(exercise.getName())) {
            model.addAttribute("error", "Übungsname schon vorhanden!");
            return "error";
        } else {
            service.insertExercise(exercise.getName(), exercise.getDescription(),
                    exercise.getImgPath());
            if (!image.isEmpty() || image.getContentType() != null) {
                if (image.getContentType().equals("image/jpeg")
                        || image.getContentType().equals("image/png")) {
                        String msg = service.uploadImg(image, exercise);
                    System.out.println(msg);
                        model.addAttribute("messageImage", msg);
                }
            }
            return "mod_exercise_search";
        }
    }
}

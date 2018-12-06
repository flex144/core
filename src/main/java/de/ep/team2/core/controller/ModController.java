package de.ep.team2.core.controller;

import de.ep.team2.core.entities.Exercise;
import de.ep.team2.core.service.ExerciseService;
import de.ep.team2.core.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles http requests with path '/mod'.
 */
@Controller
@RequestMapping("/mods")
public class ModController {

    @RequestMapping(value = {"","/home"}, method = RequestMethod.GET)
    public String home() {
        return "mod_startup_page";
    }

    @RequestMapping(value = {"/createplan"}, method = RequestMethod.GET)
    public String createPlan() {
        return "mod_create_plan";
    }

    @RequestMapping(value = {"/createexercise"}, method = RequestMethod.GET)
    public String createExercise(Model model) {
        model.addAttribute("exercise", new Exercise());
        return "mod_create_exercise";
    }

    @GetMapping(value = {"/searchexercise"})
    public String searchExercise() {
        return "mod_exercise_search";
    }

    /**
     * Handles exercise searches sent by the form on mod_exercise_search.
     * Looks for a list of Exercises where the name matches.
     * when no name is provided returns all.
     *
     * @param name name or fragment of a name to look for.
     * @param model Model Thymeleaf uses.
     * @return mod_exercise_search to display the results.
     */
    @PostMapping(value = {"/searchexercise"})
    public String searchExercise(@RequestParam("nameUebung") String name, Model model) {
        ExerciseService service = new ExerciseService();
        List<Exercise> toReturn = service.getExercisesByName(name);
        if (toReturn.isEmpty()) {
            model.addAttribute("message","Keine passenden Übungen gefunden!");
        }
        model.addAttribute("exercises", toReturn);
        return "mod_exercise_search";
    }

    @RequestMapping(value = {"/searchplan"}, method = RequestMethod.GET)
    public String searchPlan() {
        return "mod_plan_search";
    }

    @RequestMapping(value = {"/searchuser"}, method = RequestMethod.GET)
    public String searchUser(Model model) {
        UserService userService = new UserService();
        model.addAttribute("users", userService.getAllUsers());
        return "mod_user_search";
    }

}

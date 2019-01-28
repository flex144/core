package de.ep.team2.core.controller;

import de.ep.team2.core.dtos.CreatePlanDto;
import de.ep.team2.core.entities.Exercise;
import de.ep.team2.core.entities.ExerciseInstance;
import de.ep.team2.core.entities.TrainingsPlanTemplate;
import de.ep.team2.core.entities.TrainingsSession;
import de.ep.team2.core.service.ExerciseService;
import de.ep.team2.core.service.PlanService;
import de.ep.team2.core.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    /**
     * Handles Get request regarding the url /mods/createplan, when the site is visited the first
     * time(without an existing dto)
     * this method creates an empty dto adds the default value for SessionNums. adds all
     * Exercises and tags so they can be displayed.
     *
     * @param model model thymeleaf uses.
     * @return the site mod create plan.
     */
    @RequestMapping(value = {"/createplan"}, method = RequestMethod.GET)
    public String createPlan(Model model) {
        ExerciseService exerciseService = new ExerciseService();
        PlanService planService = new PlanService();
        if (!model.containsAttribute("createDto")) {
            CreatePlanDto dto = new CreatePlanDto();
            dto.setSessionNums(6);
            dto.setRecomSessionsPerWeek(2);
            model.addAttribute("createDto", dto);
        }
        model.addAttribute("allExercises", exerciseService.getAllExercises());
        model.addAttribute("allTags", planService.getAllTagNames());
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
        List<Exercise> toReturn = service.getExerciseListByName(name);
        if (toReturn.isEmpty()) {
            model.addAttribute("message","Keine passenden Übungen gefunden!");
        }
        model.addAttribute("exercises", toReturn);
        return "mod_exercise_search";
    }

    @GetMapping(value = {"/searchplan"})
    public String getSearchPlan() {
        return "mod_plan_search";
    }

    /**
     * Searches for all Plans that match with the provided name and binds them to the thymeleaf model.
     * When name left blank returns all plans.
     *
     * @param name name to search for.
     * @param model model thymeleaf uses.
     * @return the page mode_plan_search
     */
    @PostMapping(value = {"/searchplan"})
    public String postSearchPlan(@RequestParam("planName") String name, Model model) {
        PlanService service = new PlanService();
        model.addAttribute("planList", service.getPlanTemplateListByName(name));
        return "mod_plan_search";
    }

    @RequestMapping(value = {"/searchuser"}, method = RequestMethod.GET)
    public String searchUser(Model model) {
        UserService userService = new UserService();
        model.addAttribute("users", userService.getAllUsers());
        return "mod_user_search";
    }

    @RequestMapping(value="/editplan", method = RequestMethod.GET)
    public String getEditPlan(Model model) {
        if (model.containsAttribute("tpt")) {
            return "mod_edit_plan";
        } else {
            model.addAttribute("tpt", new TrainingsPlanTemplate());
            return "mod_edit_plan";
        }
    }

    @RequestMapping(value="/editplan", method = RequestMethod.PUT)
    public String postEditPlan(@ModelAttribute("tpt") TrainingsPlanTemplate tpt, Model model) {

        return "";
    }

    @RequestMapping(value="/editplan/{id}", method = RequestMethod.GET)
    public String editPlan(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        PlanService service = new PlanService();
        TrainingsPlanTemplate tpt = service.getPlanTemplateAndSessionsByID(id);
        redirectAttributes.addFlashAttribute("tpt", tpt);
        return "redirect:/mods/editplan";
    }

    @RequestMapping(value="/editplan/{id}/{exId}", method = RequestMethod.GET)
    public String editExIn(@PathVariable("id") Integer id, @PathVariable("exId") Integer exId,
                           Model model) {
        PlanService service = new PlanService();
        TrainingsPlanTemplate tpt = service.getPlanTemplateAndSessionsByID(id);
        ExerciseInstance exIn = service.getExerciseInstanceById(exId);
        model.addAttribute("tpt", tpt);
        model.addAttribute("exIn", exIn);
        return "mod_edit_exerciseInstance";
    }

    @RequestMapping(value="/editExerciseInstance", method = RequestMethod.POST)
    public String postEditExIn(@ModelAttribute("exIn") ExerciseInstance exIn) {

        return "redirect:/mods/home";
    }

}

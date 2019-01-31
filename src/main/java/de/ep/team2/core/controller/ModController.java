package de.ep.team2.core.controller;

import de.ep.team2.core.dtos.CreatePlanDto;
import de.ep.team2.core.entities.Exercise;
import de.ep.team2.core.entities.User;
import de.ep.team2.core.entities.ExerciseInstance;
import de.ep.team2.core.entities.TrainingsPlanTemplate;
import de.ep.team2.core.entities.TrainingsSession;
import de.ep.team2.core.service.ExerciseService;
import de.ep.team2.core.service.PlanService;
import de.ep.team2.core.service.StatisticService;
import de.ep.team2.core.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    /**
     * Provides the page to edit an exercise. Gets the Exercise to edit from teh database.
     *
     * @param id of the exercise to be edited.
     * @param model thymeleaf uses.
     * @return "error" when the exercise isn't in the database; "mod_exercise_edit" when everything went fine.
     */
    @GetMapping("/exercise/edit/{id}")
    public String editExerciseGet(@PathVariable("id") int id, Model model) {
        ExerciseService exerciseService = new ExerciseService();
        Exercise toEdit = exerciseService.getExerciseById(id);
        if (toEdit == null) {
            model.addAttribute("error", "Übung existiert nicht!");
            return "error";
        } else {
            model.addAttribute("exercise", toEdit);
            return "mod_exercise_edit";
        }
    }

    /**
     * Updates All values of the Provided Exercise.
     * If the new name is already in the database the data is updated but no images are uploaded or deleted and an error message is displayed.
     * Adds the new images to the FileSystem and the Database.
     * Deletes the image with the given path if one was provided.
     *
     * @param exercise Exercise Object which holds the Data filled with thymeleaf.
     * @param otherImage Array of the new images.
     * @param muscleImage Array of the new images.
     * @param pathToDelete path of the File to delete.
     * @param model thymeleaf uses.
     * @param redirectAttributes used to redirect attributes.
     * @return "error" when the exercise doesn't exist;
     * "redirect:/mods/exercise/edit/" when no complications occurred or the new name wasn't unique to display the error message.
     */
    @PostMapping("/exercise/edit")
    public String editExercisePost(@ModelAttribute("exercise") Exercise exercise,
                                   @RequestParam("otherImage") MultipartFile[] otherImage,
                                   @RequestParam("muscleImage") MultipartFile[] muscleImage,
                                   @RequestParam("pathPictureToDelete") String pathToDelete,
                                   Model model, RedirectAttributes redirectAttributes) {
        ExerciseService exerciseService = new ExerciseService();
        if (exerciseService.getExerciseById(exercise.getId()) == null) {
            model.addAttribute("error", "Übung existiert nicht!");
            return "error";
        }
        try {
            ExerciseController exerciseController = new ExerciseController();
            exerciseController.addImgPathsAndUploadFile(exercise, otherImage, muscleImage);
            exerciseService.updateExerciseAndAddNewImg(exercise);
            if (pathToDelete != null && !pathToDelete.equals("")) {
                exerciseService.deleteImg(pathToDelete);
            }
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMsg", exception.getMessage());
            return "redirect:/mods/exercise/edit/" + exercise.getId();
        }
        return "redirect:/mods/exercise/edit/" + exercise.getId();
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
    public String searchUser() {
        return "mod_user_search";
    }

    /**
     * Searches for all Users whos email, first or Last name contains the parameter name,
     * removes the password from the user object and returns the List.
     * If the name is null or blank returns all User in the System.
     *
     * @param name String to look for.
     * @param model model thymeleaf uses.
     * @return "mod_user_search"
     */
    @PostMapping("/searchuser")
    public String postSearchUser(@RequestParam("userName") String name, Model model) {
        UserService userService = new UserService();
        model.addAttribute("users", userService.getUserListByName(name));
        return "mod_user_search";
    }

    /**
     * gets all mods in the system removes the password form the object and redirects the list to the
     * "/mods/searchuser" to display them.
     *
     * @param redirectAttributes used to pass the list.
     * @return "redirect:/mods/searchuser"
     */
    @GetMapping("/searchmods")
    public String getMods(RedirectAttributes redirectAttributes) {
        UserService userService = new UserService();
        redirectAttributes.addFlashAttribute("users", userService.getAllMods());
        return "redirect:/mods/searchuser";
    }

    /**
     * adds all stats to the model and returns the mod statistic page.
     *
     * @param model model thymeleaf uses.
     * @return "mod_statistics"
     */
    @RequestMapping(value = {"/statistics"}, method = RequestMethod.GET)
    public String statistics(Model model) {
        StatisticService statisticService = new StatisticService();
        model.addAttribute("focusMap", statisticService.getUserFocusStats());
        model.addAttribute("expMap", statisticService.getUserExperienceStats());
        model.addAttribute("frequencyMap", statisticService.getUserFrequencyStats());
        model.addAttribute("userNumberMap", statisticService.getUserNumberStats());
        model.addAttribute("userGenderMap", statisticService.getUserGenderStats());
        model.addAttribute("numberExercises", statisticService.getNumberExercises());
        model.addAttribute("numberPlans", statisticService.getNumberPlans());
        return "mod_statistics";
    }

    /**
     * Searches for a plantemplate in the database and binds it to the model for thymeleaf
     * to use.
     *
     * @param id id of the plantemplate
     * @param model the model thymeleaf uses.
     * @return the page mod_edit_plan
     */
    @RequestMapping(value="/editplan/{id}", method = RequestMethod.GET)
    public String editPlan(@PathVariable("id") Integer id, Model model) {
        PlanService service = new PlanService();
        TrainingsPlanTemplate tpt = service.getPlanTemplateAndSessionsByID(id);
        if(tpt== null) {
            model.addAttribute("error", "Plan nicht gefunden!");
            return "error";
        }
        model.addAttribute("tpt", tpt);
        return "mod_edit_plan";
    }

    /**
     * Function used to edit a plan in the database.
     *
     * @param tpt The plantemplate to edit.
     * @param model the model thymeleaf uses.
     * @return redirect to /mods/searchplan
     */
    @RequestMapping(value="/editplan", method = RequestMethod.PUT)
    public String postEditPlan(@ModelAttribute("tpt") TrainingsPlanTemplate tpt, Model model) {
        PlanService service = new PlanService();
        service.editPlanTemplate(tpt);
        return "redirect:/mods/searchplan";
    }

    /**
     * Returns a website to edit a exercise instance of a plan.
     * The method looks in the database for the searched exercise instance and
     * binds it to the model.
     *
     * @param id id of the plan template
     * @param exId id of the exercise instance
     * @param model model thymeleaf uses
     * @return returns the website mod_edit_exerciseInstance
     */
    @RequestMapping(value="/editplan/{id}/{exId}", method = RequestMethod.GET)
    public String editExIn(@PathVariable("id") Integer id, @PathVariable("exId") Integer exId,
                           Model model) {
        PlanService service = new PlanService();
        ExerciseService exerciseService = new ExerciseService();
        TrainingsPlanTemplate tpt = service.getPlanTemplateAndSessionsByID(id);
        ExerciseInstance exIn = service.getExerciseInstanceById(exId);
        if(tpt == null || exIn == null) {
            model.addAttribute("error", "Plan oder Übung nicht vorhanden!");
            return "error";
        }
        model.addAttribute("tpt", tpt);
        model.addAttribute("allExercises", exerciseService.getAllExercises());
        if (!model.containsAttribute("exIn")) {
            model.addAttribute("exIn", exIn);
        }
        return "mod_edit_exerciseInstance";
    }

    /**
     * Handles post requests for editing an exercise instance.
     * The function checks if the values are valid and saves the exercise in the database.
     *
     * @param exIn the edited exercise instance
     * @param id id of the plan template where the exercise belongs
     * @param exId id of the exercise instance
     * @param model model thymeleaf uses
     * @param redirectAttributes handles attributes for a redirect
     * @return if args are valid it returns the editplan site for the plan,
     *          if not, it returns the edit exercise page, with an errormessage.
     */
    @RequestMapping(value="/editplan/{id}/{exId}", method = RequestMethod.POST)
    public String postEditExIn(@ModelAttribute("exIn") ExerciseInstance exIn,
                               @PathVariable("id") Integer id, @PathVariable("exId") Integer exId,
                               Model model, RedirectAttributes redirectAttributes) {
        PlanService service = new PlanService();
        ExerciseService exerciseService = new ExerciseService();
        Exercise toUpdate = exerciseService.getExerciseByName(exIn.getName());
        String validArgs = checkIfArgsValid(exIn);
        if (toUpdate == null) {
            String errorMessage = "Übung '"+exIn.getName()+"' nicht vorhanden!";
            redirectAttributes.addFlashAttribute("exIn", exIn);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/mods/editplan/"+id+"/"+exId;
        } else if (!validArgs.equals("")) {
            redirectAttributes.addFlashAttribute("errorMessage", validArgs);
            redirectAttributes.addFlashAttribute("exIn", exIn);
            return "redirect:/mods/editplan/"+id+"/"+exId;
        }

        exIn.setIsExerciseID(toUpdate.getId());
        service.editExerciseInstance(exIn);
        TrainingsPlanTemplate tpt = service.getPlanTemplateAndSessionsByID(exIn.getPlanTemplateID());
        if(!tpt.isConfirmed()) {
            return "redirect:/mods/plans/"+tpt.getId();
        }
        return "redirect:/mods/editplan/"+id;
    }

    /**
     * Handles delete requests for deleting an exercise instance.
     *
     * @param id id of the plan template where the exercise instance belongs
     * @param exId id of the exercise instance to delete
     * @param model model thymeleaf uses
     * @return returns the editplan site if everything goes right, else it redirects to
     *          the error page.
     */
    @RequestMapping(value="/editplan/{id}/{exId}", method = RequestMethod.DELETE)
    public String deleteExIn(@PathVariable("id") Integer id, @PathVariable("exId") Integer exId,
                             Model model) {
        PlanService planService = new PlanService();
        ExerciseInstance exIn = planService.getExerciseInstanceById(exId);
        if(exIn == null) {
            model.addAttribute("error", "Übungsinstanz existiert nicht!");
            return "error";
        } else{
            TrainingsPlanTemplate tpt = planService.getPlanTemplateAndSessionsByID(exIn.getPlanTemplateID());
            planService.deleteExerciseInstanceById(exId);
            if(!tpt.isConfirmed()) {
                return "redirect:/mods/plans/"+tpt.getId();
            }
            return "redirect:/mods/editplan/"+id;
        }
    }

    private String checkIfArgsValid(ExerciseInstance exIn) {
        String validationMessage = "";
        int sessionCounter = 1;

        for(TrainingsSession session : exIn.getTrainingsSessions()) {
            int weightLength = session.getWeightDiff().length;
            int repsLength = session.getReps().length;
            if(repsLength >7) {
                validationMessage = "Maximal 7 Sets erlaubt!";
            } else if(repsLength == 0) {
                validationMessage = "Wiederholungen dürfen nicht leer sein!";
            }else if(weightLength > repsLength) {
                validationMessage = "Es kann nicht mehr Gewichtsänderungen als Sets geben!";
            } else if(weightLength>1 && (repsLength != weightLength ||
                     containsNull(session.getWeightDiff()))) {
                validationMessage = "Gewichtsänderungen passen nicht!";
            } else if(containsNull(session.getReps())) {
                validationMessage = "Wiederholungen passen nicht!";
            }
            if(!validationMessage.equals("")) {
                validationMessage = validationMessage + " (Trainingseinheit '"+sessionCounter+"')" ;
                break;
            }
            sessionCounter++;
        }

        return validationMessage;
    }

    private boolean containsNull(Integer[] numbers) {
        boolean toReturn = false;
        for(Integer number : numbers) {
            if(number == null) {
                toReturn = true;
            }
        }
        return toReturn;
    }
}

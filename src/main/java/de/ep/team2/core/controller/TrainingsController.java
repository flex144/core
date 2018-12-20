package de.ep.team2.core.controller;

import de.ep.team2.core.dtos.CreatePlanDto;
import de.ep.team2.core.service.ExerciseService;
import de.ep.team2.core.service.PlanService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mods/")
public class TrainingsController {

    /**
     * Handles the adding of an exercise to a plan template or creating a new template with an
     * initial exercise. Forwards the input only to the backend if it is valid,
     * Provides an error messages when there was an issue.
     *
     * @param dto                CreatePlanDto provided by the view with user input.
     * @param redirectAttributes used to send Model attributes to another controller to read them
     *                          with thymeleaf.
     * @return redirects to '/mods/createplan'
     */
    @PostMapping("/plans")
    public String trainingsPlanCreate(@ModelAttribute("createDto") CreatePlanDto dto,
                                      RedirectAttributes redirectAttributes) {
        PlanService service = new PlanService();
        String checkArgs = checkIfArgsValid(dto);
        if (checkArgs.equals("valid!")) {
            dto.nameToId();
            redirectAttributes.addFlashAttribute("createDto", service.createPlan(dto));
            return "redirect:/mods/createplan";
        } else {
            redirectAttributes.addFlashAttribute("createDto", dto);
            redirectAttributes.addFlashAttribute("errorMsg", checkArgs);
            return "redirect:/mods/createplan";
        }
    }

    /**
     * Checks if the provided arguments by the User are valid.
     *
     * @param dto CreatePlanDto provided by the view with user input.
     * @return when no issues occurred 'valid!' or a String with a Description of the error.
     */
    private String checkIfArgsValid(CreatePlanDto dto) {
        ExerciseService exerciseService = new ExerciseService();
        if (dto.getSessionNums() != dto.getSets().size() || dto.getSets().contains("")
                || dto.getSets().contains(null)) {
            return "Anzahl der Trainingseinheiten nicht passend!";
        } else if (!validStringsSets(dto)) {
            return "Eingabe bei Sets entspricht nicht den Vorschriften!";
        } else if (exerciseService.getExerciseByName(dto.getExerciseName()) == null) {
            return "Übung nicht Vorhanden!";
        } else {
            return "valid!";
        }
    }

    /**
     * Checks if all Strings in the Arraylist {@code sets} of the dto can be parsed to Integer
     * Arrays by the same code used in the Plan Service.
     *
     * @param dto CreatePlanDto provided by the view with user input.
     * @return true if it is parsable, false if not
     */
    private boolean validStringsSets(CreatePlanDto dto) {
        try {
            for (String sets : dto.getSets()) {
                String[] splitted = sets.trim().replaceAll("\\s+", "").split("/");
                Integer[] toReturn = new Integer[splitted.length];
                for (int i = 0; i < splitted.length; i++) {
                    toReturn[i] = Integer.parseInt(splitted[i].trim());
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
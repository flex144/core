package de.ep.team2.core.controller;

import de.ep.team2.core.dtos.CreatePlanDto;
import de.ep.team2.core.entities.TrainingsPlanTemplate;
import de.ep.team2.core.entities.User;
import de.ep.team2.core.service.EmailSenderService;
import de.ep.team2.core.service.ExerciseService;
import de.ep.team2.core.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedList;

@Controller
@RequestMapping("/mods/")
public class TrainingsController {

    @Autowired
    private EmailSenderService emailSenderService;

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
            try {
                redirectAttributes.addFlashAttribute("createDto", service.createPlan(dto));
            } catch (IllegalArgumentException exception) {
                redirectAttributes.addFlashAttribute("errorMsg", exception.getMessage());
                return "redirect:/mods/createplan";
            }
            //Add Exercises to the thymeleaf model
            TrainingsPlanTemplate tpt = service
                    .getPlanTemplateAndSessionsByID(dto.getId());
            redirectAttributes.addFlashAttribute("plan", tpt);
            return "redirect:/mods/createplan";
        } else {
            if (dto.getId() != null) {
                //Add Exercises to the thymeleaf model
                TrainingsPlanTemplate tpt = service
                        .getPlanTemplateAndSessionsByID(dto.getId());
                redirectAttributes.addFlashAttribute("plan", tpt);
            }
            redirectAttributes.addFlashAttribute("createDto", dto);
            redirectAttributes.addFlashAttribute("errorMsg", checkArgs);
            return "redirect:/mods/createplan";
        }
    }

    /**
     * Searches for an specific plan with id and binds it to thymeleaf to be displayed and modified in create plan.
     *
     * @param id id of plan to look for.
     * @param redirectAttributes used to redirect attributes to other controller.
     * @return the page mods_create-plan
     */
    @GetMapping("/plans/{id}")
    public String showPlan(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        PlanService service = new PlanService();
        TrainingsPlanTemplate tpt = service
                .getPlanTemplateAndSessionsByID(id);
        redirectAttributes.addFlashAttribute("plan", tpt);
        CreatePlanDto dto = new CreatePlanDto();
        dto.setId(id);
        dto.setPlanName(tpt.getName());
        dto.setTrainingsFocus(tpt.getTrainingsFocus());
        dto.setSessionNums(tpt.getNumTrainSessions());
        dto.setRecomSessionsPerWeek(tpt.getRecomSessionsPerWeek());
        redirectAttributes.addFlashAttribute("createDto", dto);
        return "redirect:/mods/createplan";
    }

    /**
     * deletes a plan with an specific id and sends an email to all users whose plan was based on the deleted plan.
     *
     * @param id id of plan to delete.
     * @return the page mods_plan_search.
     */
    @DeleteMapping("/plans/{id}")
    public String deletePlanAndChildren(@PathVariable("id") Integer id) {
        PlanService service = new PlanService();
        LinkedList<User> users = service.deleteTemplateAndChildrenById(id);
        for (User user : users) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Plan has been reset!");
            mailMessage.setText("Unfortunately your plan has been deleted since your plan is outdated. Please " +
                    "log in and create a new one and keep training!");
            emailSenderService.sendEmail(mailMessage);
        }
        return "redirect:/mods/searchplan";
    }

    @PostMapping("/confirmPlan")
    public String confirmPlan(@ModelAttribute("createDto") CreatePlanDto dto) {
        PlanService service = new PlanService();
        service.confirmPlan(dto.getId());
        return "redirect:/mods/searchplan";
    }

    /**
     * Checks if the provided arguments by the User are valid.
     *
     * @param dto CreatePlanDto provided by the view with user input.
     * @return when no issues occurred 'valid!' or a String with a Description of the error.
     */
    private String checkIfArgsValid(CreatePlanDto dto) {
        ExerciseService exerciseService = new ExerciseService();
        if (dto.getSessionNums() != dto.getSets().size() || dto.getSets().contains(null)
                || dto.getSets().contains("")) {
            return "Anzahl der angegebenen Trainingseinheiten nicht passend!";
        } else if (dto.getSessionNums() != dto.getPause().size() || dto.getPause().contains(null)) {
            return "Anzahl der angegebenen Pausenwerte nicht passend!";
        } else if (dto.getSessionNums() != dto.getTempo().size() || dto.getTempo().contains(null)
                || dto.getTempo().contains("")) {
            return "Anzahl der angegebenen Tempowerte nicht passend!";
        } else if (!validStringsSets(dto)) {
            return "Eingabe bei Sets entspricht nicht den Vorschriften!";
        } else if (exerciseService.getExerciseByName(dto.getExerciseName()) == null) {
            return "Übung nicht Vorhanden!";
        } else if (dto.getId() == null && !checkPlanNameUnique(dto)) {
            return "Planname schon vorhanden!";
        } else {
            return "valid!";
        }
    }

    private boolean checkPlanNameUnique(CreatePlanDto dto) {
        PlanService planService = new PlanService();
        LinkedList<TrainingsPlanTemplate> plans = planService.getPlanTemplateListByName(dto.getPlanName());
        for (TrainingsPlanTemplate plan : plans) {
            if (plan.getName().equals(dto.getPlanName())) {
                return false;
            }
        }
        return true;
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

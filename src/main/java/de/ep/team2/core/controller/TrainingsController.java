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

    private String checkIfArgsValid(CreatePlanDto dto) {
        ExerciseService exerciseService = new ExerciseService();
        if (dto.getSessionNums() != dto.getSets().size() || dto.getSets().contains("")
                || dto.getSets().contains(null)) {
            return "Anzahl der Trainingseinheiten nicht passend!";
        } else if (exerciseService.getExerciseByName(dto.getExerciseName()) == null) {
            return "Übung nicht Vorhanden!";
        } else {
            return "valid!";
        }
    }
}

package de.ep.team2.core.controller;

import de.ep.team2.core.dtos.ExerciseDto;
import de.ep.team2.core.dtos.TrainingsDayDto;
import de.ep.team2.core.entities.User;
import de.ep.team2.core.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@Scope("request")
public class UserController {

    @Autowired
    private TrainingsDayDto dayDto;

    @RequestMapping("/home")
    public String startUp() { return "user_startup_page"; }

    @RequestMapping("/new")
    public String newUser() {
        return "user_data";
    }

    @RequestMapping("/plan")
    public String handleUserTrainingStart(Model model) {
        PlanService planService = new PlanService();

        // start Training
        if (dayDto.getExercises() == null) {
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dayDto = planService.fillTrainingsDayDto(principal.getEmail(), dayDto);
            model.addAttribute("dayDto", dayDto);
            return "user_training_overview";
        } else { // Training already started
            boolean isDone = planService.checkIfDayDone(dayDto);
            if (isDone) {
                if (dayDto.isInitialTraining()) {
                    planService.setUserPlanInitialTrainDone(dayDto.getExercises().getFirst().getIdUserPlan());
                }
                dayDto.clear();
                return "redirect:/user/home"; // maybe info page that training is over
            } else {
                model.addAttribute("dayDto", dayDto);
                return "user_training_overview";
            }
        }
    }

    @PostMapping("/plan/done")
    public String exerciseCompleted(@RequestParam("doneFlag") boolean doneFlag, @RequestParam("indexInList") Integer indexInList) {
        ExerciseDto exerciseDto = dayDto.getExercises().get(indexInList);
        exerciseDto.setDone(doneFlag);
        dayDto.setCurrentCategory(exerciseDto.getCategory());
        dayDto.changeExercise(exerciseDto, indexInList);
        return "redirect:/user/plan";
    }

    @PostMapping("/plan/done/init")
    public String exerciseInitCompleted(@RequestParam("doneFlag") boolean doneFlag, @RequestParam("indexInList") Integer indexInList,
    @RequestParam Integer weightDone) {
        PlanService service = new PlanService();
        ExerciseDto exerciseDto = dayDto.getExercises().get(indexInList);
        exerciseDto.setDone(doneFlag);
        exerciseDto.setWeightDone(weightDone);
        service.setWeightsOfExercise(exerciseDto);
        dayDto.setCurrentCategory(exerciseDto.getCategory());
        dayDto.changeExercise(exerciseDto, indexInList);
        return "redirect:/user/plan";
    }

    @RequestMapping("/plan/exercise/{index}")
    public String openExercise(@PathVariable Integer index, Model model) {
        if (dayDto.getExercises() == null) {
            model.addAttribute("error", "No active plan visit plan overview first!");
            return "error";
        } else {
            ExerciseDto exerciseDto = dayDto.getExercises().get(index);
            model.addAttribute("exerciseDto", exerciseDto);
            if (dayDto.isInitialTraining()) {
                return "user_first_training_of_plan";
            } else {
                return "user_in_exercise";
            }
        }
    }

}

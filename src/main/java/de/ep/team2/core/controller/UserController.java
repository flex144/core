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

import javax.servlet.http.HttpSession;

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
            if (dayDto.isInitialTraining()) {
                model.addAttribute("dayDto", dayDto);
                return "init_train";
            } else {
                return "user_training_overview";
            }
        } else { // Training already started
            Boolean isDone = planService.checkIfDone(dayDto);
            if (isDone) {
                if (dayDto.isInitialTraining()) {
                    planService.setUserPlanInitialTrainDone(dayDto.getExercises().getFirst().getIdUserPlan());
                }
                dayDto.clear();
                return "redirect:/user/home"; // maybe info page that training is over
            } else {
                model.addAttribute("dayDto", dayDto);
                if (dayDto.isInitialTraining()) {
                    return "init_train";
                }
                return "user_training_overview";
            }
        }
    }

    @PostMapping("/plan/done")
    public String exerciseCompleted(@RequestParam("doneFlag") boolean doneFlag, @RequestParam("indexInList") Integer indexInList) {
        ExerciseDto exerciseDto = dayDto.getExercises().get(indexInList);
        exerciseDto.setDone(doneFlag);
        dayDto.changeExercise(exerciseDto, indexInList);
        return "redirect:/user/plan";
    }

    @PostMapping("/plan/init/done")
    public String exerciseInitCompleted(@RequestParam("doneFlag") boolean doneFlag, @RequestParam("indexInList") Integer indexInList,
    @RequestParam Integer weightDone) {
        PlanService service = new PlanService();
        ExerciseDto exerciseDto = dayDto.getExercises().get(indexInList);
        exerciseDto.setDone(doneFlag);
        exerciseDto.setWeightDone(weightDone);
        service.setWeightsOfExercise(exerciseDto);
        dayDto.changeExercise(exerciseDto, indexInList);
        return "redirect:/user/plan";
    }

    @RequestMapping("/plan/overview")
    public String openTraining() {
        return "user_training_overview";
    }

    @RequestMapping("/plan/exercise/{index}")
    public String openExercise(@PathVariable Integer index, Model model) {
        ExerciseDto exerciseDto = dayDto.getExercises().get(index); // todo method to search fo them
        model.addAttribute("exerciseDto", exerciseDto);
        return "user_in_exercise";
    }

    @RequestMapping("/plan/init/exercise/{index}")
    public String openInitExercise(@PathVariable Integer index, Model model) {
        ExerciseDto exerciseDto = dayDto.getExercises().get(index); // todo method to search fo them
        model.addAttribute("exerciseDto", exerciseDto);
        return "init_exercise";}
}

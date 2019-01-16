package de.ep.team2.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/home")
    public String startUp() { return "user_startup_page"; }

    @RequestMapping("/new")
    public String newUser() {
        return "user_data";
    }

    @RequestMapping("/plan")
    public String plan() {
        return "user_in_exercise";
    }

    @RequestMapping("/plan/done")
    public String planEvalu() {
        return "user_exercise_evaluation";
    }

    @RequestMapping("/plan/overview")
    public String openTraining() {
        return "user_training_overview";
    }

    @RequestMapping("/plan/exercise")
    public String openExercise() { return "user_in_exercise"; }

    @RequestMapping("/plan/first_exercise")
    public String openFirstExercise() { return "user_first_training_of_plan"; }

    @RequestMapping("/plan/finished")
    public String planFinished() { return "user_training_finished";}
}

package de.ep.team2.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles http requests with path '/mod'.
 */
@Controller
@RequestMapping("/mods")
public class ModController {

    @RequestMapping(value= {"/home"}, method = RequestMethod.GET)
    public String home() {
        return "mod_startup_page_web";
    }

    @RequestMapping(value= {"/createplan"}, method = RequestMethod.GET)
    public String createPlan() {
        return "mod_create_plan";
    }

    @RequestMapping(value = {"/createexercise"}, method = RequestMethod.GET)
    public String createExercise() {
        return "mod_create_exercise";
    }

    @RequestMapping(value = {"/searchexercise"}, method = RequestMethod.GET)
    public String searchExercise() {
        return "mod_exercise_search";
    }

    @RequestMapping(value = {"/searchplan"}, method = RequestMethod.GET)
    public String searchPlan() {
        return "mod_plan_search";
    }

    @RequestMapping(value = {"/searchuser"}, method = RequestMethod.GET)
    public String searchUser() {
        return "mod_user_search";
    }

}

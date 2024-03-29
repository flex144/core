package de.ep.team2.core.controller;

import de.ep.team2.core.Exceptions.NoPlanException;
import de.ep.team2.core.dataInit.DataInit;
import de.ep.team2.core.dtos.ExerciseDto;
import de.ep.team2.core.dtos.RegistrationDto;
import de.ep.team2.core.dtos.TrainingsDayDto;
import de.ep.team2.core.entities.ExerciseInstance;
import de.ep.team2.core.entities.TrainingsPlanTemplate;
import de.ep.team2.core.entities.User;
import de.ep.team2.core.entities.UserPlan;
import de.ep.team2.core.enums.ExperienceLevel;
import de.ep.team2.core.enums.TrainingsFocus;
import de.ep.team2.core.enums.WeightType;
import de.ep.team2.core.service.EmailSenderService;
import de.ep.team2.core.service.PlanService;
import de.ep.team2.core.service.StatisticService;
import de.ep.team2.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/user")
@Scope("request")
public class UserController {

    @Autowired
    private TrainingsDayDto dayDto;

    @Autowired
    private EmailSenderService emailSenderService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    /**
     * Brings the User to the Startup page.
     *
     * @return "user_startup_page" to navigate to the page.
     */
    @RequestMapping("/home")
    public String startUp() { return "user_startup_page"; }

    // get Plan and save user data

    /**
     * Brings the User to the User data page where he has to select a plan and can provide his additional data for plan distribution.
     * Binds User object with the user data for plan choosing and a List with all Bullet Plans for the user to choose from to the thymeleaf model.
     *
     * @param model The model thymeleaf uses.
     * @return "user_data" to navigate to the page.
     */
    @GetMapping("/new")
    public String newUser(Model model) {
        UserService service = new UserService();
        PlanService planService = new PlanService();
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        principal.setPassword(null); // so the encoded password isn't provided to the frontend
        model.addAttribute("user", service.getUserByEmail(principal.getEmail()));
        LinkedList<TrainingsPlanTemplate> allOneShotPlans = planService.getOneShotPlansForUser();
        model.addAttribute("bulletPlans", allOneShotPlans);
        return "user_data";
    }

    /**
     * Handles the changes of user data which were made by the user ont he site "user_data"
     * Changes all provided Data in the Backend.
     * Then gets the User a plan regarding his data by redirecting him to "/user/plan/getNormal"
     *
     * @param user User object which holds most of the data from the user.
     * @param focus String which holds the data for the Trainings focus, is parsed to the enum and then appended to the user object.
     * @param experience String which holds the data for the Experience level, is parsed to the enum and then appended to the user object.
     * @return "redirect:/user/plan/getNormal" to redirect the user in the controller which handles this request.
     */
    @PostMapping("/new")
    public String addUserData(@ModelAttribute("user") User user, @RequestParam("focus") String focus,
                              @RequestParam("experienceLevel") String experience) {
        UserService service = new UserService();
        user.setExperience(ExperienceLevel.getValueByName(experience));
        user.setTrainingsFocus(TrainingsFocus.getValueByName(focus));
        service.changeAdvancedUserDetails(user);
        service.changeUserDetails(user);
        return "redirect:/user/plan/getNormal";
    }

    /**
     * todo evtl rework do with post.
     * Assigns the plan with the id {index} to current logged in user. (only if the user doesn't already have a plan.)
     *
     * @param id id of the plan.
     * @param redirectAttributes used to display the error message on a redirected page.
     * @return "redirect:/user/plan" if everything went right so the user can start the training; "redirect:/user/new" to display the error message.
     */
    @GetMapping("/plan/choose/{index}")
    public String assignOneShotUserPlans(@PathVariable("index") Integer id, RedirectAttributes redirectAttributes) {
        PlanService planService = new PlanService();
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            planService.assignPlan(principal.getEmail(), id);
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("errorMsg", exception.getMessage());
            return "redirect:/user/new";
        }
        return "redirect:/user/plan";
    }

    /**
     * Assigns the user a Plan based on his data.
     * Provides an error message according the issue encounterd and redirects to "user/new" to display the message.
     * (Reasons can be user already has plan, no data, no matching plan found.)
     *
     * @param redirectAttributes used to display the error message on a redirected page.
     * @return "redirect:/user/plan" if everything went right so the user can start the training; "redirect:/user/new" to display the error message.
     */
    @GetMapping("/plan/getNormal")
    public String getNormalPlan(RedirectAttributes redirectAttributes) {
        PlanService planService = new PlanService();
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TrainingsPlanTemplate suitedPlan;
        try {
            suitedPlan = planService.getPlanForUser(principal.getEmail());
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMsg", exception.getMessage());
            return "redirect:/user/new";
        }
        if (suitedPlan == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Kein passender Plan gefunden. Versuch es doch mit einem Eintagesplan.");
            return "redirect:/user/new";
        } else {
            return "redirect:/user/plan";
        }
    }

    // Training process

    /**
     * Checks if the TrainingsDay has already started. If not:
     * Checks if the User has a plan, if not redirects him to the page "user_data" to get a new plan.
     * If the user has a PLan starts the Training by creating the TrainingsDayDto and bring the USer to the exercise overview where he can select his exercises.
     *
     * If its already started:
     * Checks with the use of the TrainingsDayDto if all exercises of the current day are done. If thats the case clears the dto
     * and redirects the the startup page for the user. ( cause the training is done)
     * If the not all exercises are completed, forwards the user to the overview page. where he can select the next exercise.
     *
     * @param model Model Thymeleaf uses.
     * @return "user_training_overview" when the user has to select the next exercise; "redirect:/user/new" if the user has no plan; "redirect:/user/home" if the training is done for the day.
     */
    @RequestMapping("/plan")
    public String handleUserTrainingStart(Model model, RedirectAttributes redirectAttributes) {
        PlanService planService = new PlanService();
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // start Training
        if (dayDto.getExercises() == null) {
            try {
                dayDto = planService.fillTrainingsDayDto(principal.getEmail(), dayDto);
            } catch (NoPlanException noPlan) {
                return "redirect:/user/new";
            }
            if (dayDto.isInitialTraining()) {
                for (ExerciseDto exDto : dayDto.getExercises()) {
                    if (exDto.getExercise().getWeightType() == WeightType.SELF_WEIGHT) {
                        exDto.setDone(true);
                    }
                }
            }
        }
        // Training already started
        if (planService.checkIfDayDone(dayDto)) {
            if (dayDto.isInitialTraining()) {
                planService.setUserPlanInitialTrainDone(dayDto.getExercises().getFirst().getIdUserPlan());
            } else {
                StatisticService statisticService = new StatisticService();
                statisticService.increaseDaysDone(principal.getEmail());
            }
            dayDto.clear();
            redirectAttributes.addFlashAttribute("message", "Trainingseinheit abgeschlossen! Weiter so! Bleib dran!");
            return "redirect:/user/home";
        } else {
            model.addAttribute("dayDto", dayDto);
            return "user_training_overview";
        }
    }

    /**
     * Sets the exercise of The current TrainingsDayDto done.
     * Increases the total weight stat of the user by the weight he did.
     *
     * @param doneFlag boolean Flag if done.
     * @param indexInList index of the exercise in the List of the Dto.
     * @return "redirect:/user/plan" to let it check what the next step is.
     */
    @PostMapping("/plan/exercise/done")
    public String exerciseCompleted(@RequestParam("doneFlag") boolean doneFlag, @RequestParam("indexInList") Integer indexInList) {
        ExerciseDto exerciseDto = dayDto.getExercises().get(indexInList);
        exerciseDto.setDone(doneFlag);
        incStats(exerciseDto);
        dayDto.setCurrentCategory(exerciseDto.getCategory());
        dayDto.changeExercise(exerciseDto, indexInList);
        return "redirect:/user/plan";
    }

    private void incStats(ExerciseDto exerciseDto) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserService userService = new UserService();
        StatisticService statisticService = new StatisticService();
        int sum = 0;
        if (exerciseDto.getExercise().getWeightType() == WeightType.SELF_WEIGHT) {
            Integer userWeight = userService.getUserByEmail(principal.getEmail()).getWeightInKg();
            if (userWeight == null) {
                return;
            } else {
                for (int i = 0; i < exerciseDto.getWeights().length; i++) {
                    sum = sum + (userWeight * exerciseDto.getReps()[i]);
                }
            }
        } else {
            for (int i = 0; i < exerciseDto.getWeights().length; i++) {
                sum = sum + (exerciseDto.getWeights()[i] * exerciseDto.getReps()[i]);
            }
        }
        statisticService.increaseWeightDone(principal.getEmail(), sum);
    }

    /**
     * Calculates with the done and required reps if a weight adjustment is necessary.
     * Calls the method in the Plan service to execute the adjustment.
     * redirects the needed parameter to access the exercise page again.
     *
     * @param indexInList to identify the exercise Dto
     * @param currentSet current set to save the progress of the exercise
     * @param repsTodo reps the user should do
     * @param repsDone reps the user has done
     * @param redirectAttributes used to bring the currentSet attribute to the front end
     * @return "redirect:/user/plan/exercise"
     */
    @PostMapping("/plan/exercise/adjust")
    public String exerciseRepsEvaluation(@RequestParam("indexInList") Integer indexInList, @RequestParam("currentSet") Integer currentSet,
                                         @RequestParam("repsTodo") Integer repsTodo, @RequestParam("repsDone") Integer repsDone, RedirectAttributes redirectAttributes,  HttpServletRequest request) {
        if (currentSet == null || repsTodo == null || repsDone == null) {
            request.setAttribute(
                    View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            redirectAttributes.addFlashAttribute("currentSet", currentSet);
            redirectAttributes.addAttribute("index", indexInList);
            redirectAttributes.addAttribute("trainingStarted", true);
            return "redirect:/user/plan/exercise";
        }
        PlanService planService = new PlanService();
        ExerciseDto exerciseDto = dayDto.getExercises().get(indexInList);
        double calc = -1 + ((double) repsDone / (double) repsTodo);
        int repsDifferencePercent = (int) Math.round(calc * 100);
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        planService.adjustWeightsOfUserForExercise(principal.getEmail(), exerciseDto.getIdExerciseInstance(), repsDifferencePercent);
        ExerciseInstance exerciseInstance = planService.getExerciseInstanceById(exerciseDto.getIdExerciseInstance());
        UserPlan userPlan = planService.getUserPlanById(exerciseDto.getIdUserPlan());
        exerciseDto.setWeights(planService.createExerciseDto(exerciseInstance, userPlan, dayDto.getCurrentSession()).getWeights());
        dayDto.changeExercise(exerciseDto, exerciseDto.getIndexInList());
        request.setAttribute(
                View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        redirectAttributes.addFlashAttribute("currentSet", currentSet);
        redirectAttributes.addAttribute("index", indexInList);
        redirectAttributes.addAttribute("trainingStarted", true);
        return "redirect:/user/plan/exercise";
    }

    /**
     * Sets the exercise of The current TrainingsDayDto done. (only used for initial Trainings)
     * And saves the weight done by the user to the database.
     *
     * @param doneFlag boolean Flag if done.
     * @param indexInList index of the exercise in the List of the Dto.
     * @param weightDone weight the user accomplished.
     * @return "redirect:/user/plan" to let it check what the next step is.
     */
    @PostMapping("/plan/exercise/done/init")
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

    /**
     * Brings the user to the page where he can perform the exercise.
     * Determines with the dto if the user is forwarded tot he initial TrainingsPage or not.
     *
     * @param index index of the exercise in the list of the dto
     * @param model model thymeleaf uses.
     * @return "error" when the trainings day hasn't started yet.; "user_first_training_of_plan" when its the initial training to provide the weights.;
     * "user_in_exercise" when its a normal trainings session.
     */
    @PostMapping("/plan/exercise")
    public String openExercise(@RequestParam("index") Integer index,
                               @RequestParam("trainingStarted") Boolean started, Model model) {
        if (dayDto.getExercises() == null) {
            model.addAttribute("error", "No active plan visit plan overview first!");
            return "error";
        } else {
            ExerciseDto exerciseDto = dayDto.getExercises().get(index);
            dayDto.setTrainingStarted(started);
            model.addAttribute("exerciseDto", exerciseDto);
            if (dayDto.isInitialTraining()) {
                return "user_first_training_of_plan";
            } else {
                if(!model.containsAttribute("currentSet")) {
                    model.addAttribute("currentSet", 0);
                }
                return "user_in_exercise";
            }
        }
    }

    // alter user details

    @RequestMapping(value="/editprofile", method = RequestMethod.GET)
    public String showEditUserProfile(Model model) {
        UserService service = new UserService();
        User principal = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = service.getUserByEmail(principal.getEmail());
        model.addAttribute("user", user);
        model.addAttribute("dto", new RegistrationDto());
        return "mod_edit_user_profile";
    }

    @PostMapping(value="/editprofile")
    public String editUserProfile(@ModelAttribute("user") User editUser) {
        UserService service = new UserService();
        User user = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        editUser.setEmail(user.getEmail());
        service.changeAdvancedUserDetails(editUser);
        editUser = service.changeUserDetails(editUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(editUser, editUser.getPassword(),
                editUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/users/";

    }

    @PostMapping(value="/editprofilepassword")
    public String editPassword(@ModelAttribute("dto")RegistrationDto dto, RedirectAttributes redirectAttributes) {
        UserService service = new UserService();
        User user = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if (service.pwMatches(dto.getPassword(), user.getPassword())) {
            user.setPassword(service.encode(dto.getConfirmPassword()));
            user = service.changeUserDetails(user);
            Authentication auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(),
                    user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            String infoMessage = "Passwort wurde aktualisiert!";
            redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
        } else {
            String errorMessage = "Altes Passwort ist falsch!";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/user/editprofile";
    }

    /**
     * adds all stats to the model and returns the user statistic page.
     *
     * @param model model thymeleaf uses.
     * @return "mod_statistics"
     */
    @RequestMapping(value = {"/statistics"}, method = RequestMethod.GET)
    public String statistics(Model model) {
        StatisticService statisticService = new StatisticService();
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("userStats", statisticService.getUserStats(principal.getEmail()));
        return "user_statistics";
    }

    /**
     * returns the stat page of a specific user for a mod.
     *
     * @param id user to get the stats from.
     * @param model model thymeleaf uses.
     * @return "user_statistics"
     */
    @PreAuthorize("hasRole('ROLE_MOD')")
    @RequestMapping(value = {"/statistics/{id}"}, method = RequestMethod.GET)
    public String statisticsOtherUser(@PathVariable int id, Model model) {
        StatisticService statisticService = new StatisticService();
        UserService userService = new UserService();
        User user = userService.getUserByID(id);
        model.addAttribute("userStats", statisticService.getUserStats(user.getEmail()));
        return "user_statistics";
    }

    @RequestMapping(value = {"/contact"}, method = RequestMethod.GET)
    public String contact(Model model) {
        UserService userService = new UserService();
        List<User> mods = userService.getAllMods();
        List<User> validMods = new LinkedList<>();
        for (User mod : mods) {
            if (mod.getFirstName() != null && !mod.getFirstName().equals("") &&
                    mod.getLastName() != null && !mod.getLastName().equals("")) {
                validMods.add(mod);
            }
        }
        model.addAttribute("mods", validMods);
        return "user_contact_trainer";
    }

    @PostMapping("/contact")
    public String contactSubmit(@RequestParam("trainer") Integer id, @RequestParam("subject") String subject,
                                @RequestParam("message") String message, Model model, RedirectAttributes redirectAttributes) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String modMail;
        UserService userService = new UserService();
        if (id == -1) {
            List<User> mods = userService.getAllMods();
            mods.remove(userService.getUserByEmail(DataInit.ADMIN_MAIL));
            Random rnd = new Random();
            int r = rnd.nextInt(mods.size());
            modMail = mods.get(r).getEmail();
        } else {
            User mod = userService.getUserByID(id);
            if (mod != null) {
                if (!mod.getRole().equals("ROLE_MOD")) {
                    model.addAttribute("error", "User is no Mod");
                    return "error";
                }
                modMail = mod.getEmail();
            } else {
                model.addAttribute("error", "Mod not found");
                return "error";
            }
        }
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) {
            model.addAttribute("error", "You need to be logged in");
            return "error";
        }
        String uri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
        String url = uri + "/users/" + principal.getId();
        mailMessage.setTo(modMail);
        mailMessage.setSubject(subject);
        mailMessage.setText("Der Benutzer " + principal.getEmail() + " hat ein Problem mit der Traingsplattform. \n" +
                "Nachricht: \n\n" + message + "\n\nLink zum Profil: " + url + "\n\n Bitte Kontaktiere ihn über seine E-Mail-Adresse.");
        log.info("Email sent to '" + modMail + "' from '" + principal.getEmail() + "' because he has trouble with the system");
        emailSenderService.sendEmail(mailMessage);
        redirectAttributes.addFlashAttribute("message", "Die E-Mail wurde an den/einen Trainer versandt.");
        return "redirect:/user/home";
    }
}

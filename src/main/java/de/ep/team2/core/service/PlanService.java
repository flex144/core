package de.ep.team2.core.service;

import de.ep.team2.core.Exceptions.NoPlanException;
import de.ep.team2.core.dtos.CreatePlanDto;
import de.ep.team2.core.dtos.ExerciseDto;
import de.ep.team2.core.dtos.TrainingsDayDto;
import de.ep.team2.core.entities.*;
import de.ep.team2.core.enums.WeightType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PlanService {

    private static final Logger log = LoggerFactory.getLogger(PlanService.class);

    /**
     * Checks if the Template is already existing by looking at the id of the dto which is null when
     * a new template should be created. Forwards the dto then to suited methods which will add
     * the data to the database.
     *
     * @param dto dto which holds the data provided by the user.
     * @return Returns the same dto which was provided at the beginning if the template hasn't
     * already existed adds the id of the new template to the dto.
     */
    public CreatePlanDto createPlan(CreatePlanDto dto) {
        if (dto.getId() == null) {
            dto.setId(insertNewPlan(dto));
        } else {
            changePlanNameAndFocusOnChange(dto);
            addInstanceAndSessionToExistingPlan(dto, dto.getId());
        }
        return dto;
    }

    /**
     * Deletes a Template and all its Children.
     *
     * @param id id of template to delete.
     * @return List of users whose plan was based on the deleted plan template
     */
    public LinkedList<User> deleteTemplateAndChildrenById(int id) {
        DataBaseService db = DataBaseService.getInstance();
        TrainingsPlanTemplate tempToDelete = db.getPlanTemplateAndSessionsByID(id);
         LinkedList<User> users = db.deleteUserPlansByTemplateId(id);
        for (ExerciseInstance ei : tempToDelete.getExerciseInstances()) {
            deleteExerciseInstanceById(ei.getId());
        }
        db.deletePlanTemplateByID(id);
        return users;
    }

    /**
     * Deletes an exercise instance and its trainings sessions from the database.
     *
     * @param id Id of the exercise instance to delete
     */
    public void deleteExerciseInstanceById(int id) {
        DataBaseService db = DataBaseService.getInstance();
        ExerciseInstance toDelete = db.getExercisInstanceById(id);
        if(toDelete != null) {
            db.deleteWeightsByExId(id);
            for (TrainingsSession session : toDelete.getTrainingsSessions()) {
                db.deleteTrainingsSessionById(session.getId());
            }
            db.deleteExerciseInstanceByID(id);
        }
    }

    /**
     * Returns a List of all templates where the name matches. No children are appended to the returned Templates.
     *
     * @param name name to look for.
     * @return LinkedList of the found Templates, empty list if nothing was found.
     */
    public LinkedList<TrainingsPlanTemplate> getPlanTemplateListByName(String name) {
        DataBaseService db = DataBaseService.getInstance();
        if (name == null || name.equals("")) {
            return db.getAllPlanTemplatesNoChildren();
        } else {
            return db.getOnlyPlanTemplateListByName(name);
        }
    }

    /**
     * Returns a List of the Names of all Plan Templates in the System.
     *
     * @return List of the Names of all Plan Templates as String.
     */
    public List<String> getAllPlanNames() {
        LinkedList<TrainingsPlanTemplate> allTemplates = DataBaseService.getInstance().getAllPlanTemplatesNoChildren();
        LinkedList<String> toReturn = new LinkedList<>();
        for (TrainingsPlanTemplate tpt : allTemplates) {
            toReturn.add(tpt.getName());
        }
        return toReturn;
    }

    /**
     * Returns a Template with a specific id with all its children added to the Object.
     *
     * @param id id of template to return.
     * @return Template object with all children.
     */
    public TrainingsPlanTemplate getPlanTemplateAndSessionsByID(Integer id) {
        TrainingsPlanTemplate toReturn =  DataBaseService.getInstance().getPlanTemplateAndSessionsByID(id);
        if (toReturn == null) {
            throw new IllegalArgumentException("Plan with Id " + id + " not found!");
        } else {
            return toReturn;
        }
    }

    /**
     * Get a List of all Tag names in the database and returns it.
     *
     * @return Linked List of all Tag names.
     */
    public LinkedList<String> getAllTagNames() {
        return DataBaseService.getInstance().getAllTagNames();
    }

    private void changePlanNameAndFocusOnChange(CreatePlanDto dto) {
        DataBaseService db = DataBaseService.getInstance();
        TrainingsPlanTemplate template = db.getOnlyPlanTemplateById(dto.getId());
        if (!template.getName().equals(dto.getPlanName())) {
            if (db.isTemplateInDatabase(dto.getPlanName())) {
                throw new IllegalArgumentException("Plan name wird schon verwendet!");
            } else {
                db.renameTemplate(dto.getPlanName(), dto.getId());
            }
        }
        if (!template.getTrainingsFocus().equals(dto.getTrainingsFocus())) {
            db.changeTrainingsFocus(dto.getTrainingsFocus(), dto.getId());
        }
        if (!template.getTargetGroup().equals(dto.getTargetGroup())) {
            db.changeTargetGroup(dto.getTargetGroup(), dto.getId());
        }
    }

    private Integer insertNewPlan(CreatePlanDto dto) {
        DataBaseService db = DataBaseService.getInstance();
        Integer idTemplate;
        User user = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String mail = user.getEmail();
        idTemplate = db.insertPlanTemplate(dto.getPlanName(), dto.getTrainingsFocus(), dto.getTargetGroup(), mail,
                dto.isOneShot(), dto.getRecomSessionsPerWeek(), dto.getSessionNums(), 1);
        Integer exInstanceId = db.insertExerciseInstance(dto.getExerciseID(), dto.getCategory(),
                dto.getRepetitionMaximum(), dto.getTags(), idTemplate);
        createTrainingsSessions(dto, exInstanceId);
        return idTemplate;
    }

    private void createTrainingsSessions(CreatePlanDto dto, int exInstanceId) {
        DataBaseService db = DataBaseService.getInstance();
        for (int i = 0; i < dto.getSessionNums(); i++) {
            Integer[] reps = parseReps(dto.getSets().get(i));
            // fixes special case where weightDiff input isn't set as List in the Dto, when only one empty weightDiff is provided.
            if (dto.getWeightDiff().size() == 0) {
                dto.setWeightDiff(new LinkedList<String>() {{add("");}});
            }
            Integer[] weightDiff = parseWeightDiff(dto.getWeightDiff().get(i));
            if (!validateWeightWithReps(reps, weightDiff)) {
                throw new IllegalArgumentException("Wiederholungen und Gewichtsunterschiede stimmen nicht überein!");
            }
            if (reps.length > 10) {
                throw new IllegalArgumentException("Zu viele Sätze maximal 10 erlaubt!");
            }
            db.insertTrainingsSession(exInstanceId, i + 1, reps.length, setWeightDiffToReps(reps,weightDiff), reps,
                    dto.getTempo().get(i), dto.getPause().get(i));
        }
    }

    /**
     * Checks if the WeightDiff and the Reps are parsable. returns true if thats the case and throws an exception with
     * an according message if its not possible.
     *
     * @param dto dto which holds the data provided by the user.
     * @return true when it is parsable.
     */
    public boolean checkWeightDiffAndReps(CreatePlanDto dto) {
        for (int i = 0; i < dto.getSessionNums(); i++) {
            Integer[] reps = parseReps(dto.getSets().get(i));
            // fixes special case where weightDiff input isn't set as List in the Dto, when only one empty weightDiff
            // is provided.
            if (dto.getWeightDiff().size() == 0) {
                dto.setWeightDiff(new LinkedList<String>() {{
                    add("");
                }});
            }
            Integer[] weightDiff = parseWeightDiff(dto.getWeightDiff().get(i));
            if (!validateWeightWithReps(reps, weightDiff)) {
                throw new IllegalArgumentException("Wiederholungen und Gewichtsunterschiede stimmen nicht überein!");
            }
            if (reps.length > 10) {
                throw new IllegalArgumentException("Zu viele Sätze maximal 10 erlaubt!");
            }
        }
        return true;
    }

    private void addInstanceAndSessionToExistingPlan(CreatePlanDto dto, Integer idOfTemplate) {
        DataBaseService db = DataBaseService.getInstance();
        Integer exInstanceId = db.insertExerciseInstance(dto.getExerciseID(), dto.getCategory(),
                dto.getRepetitionMaximum(), dto.getTags(), dto.getId());
        createTrainingsSessions(dto, exInstanceId);
        db.increaseNumOfExercises(idOfTemplate);
    }

    private boolean validateWeightWithReps(Integer[] reps, Integer[] weightDiff) {
        if (weightDiff.length == reps.length) {
            return true;
        } else return weightDiff.length == 1 && reps.length > 1;
    }

    private Integer[] setWeightDiffToReps(Integer[] reps, Integer[] weightDiff) {
        if (weightDiff.length == 1 && reps.length > 1) {
            Integer value = weightDiff[0];
            Integer[] toReturn = new Integer[reps.length];
            Arrays.fill(toReturn, value);
            return toReturn;
        } else {
            return weightDiff;
        }
    }

    /**
     * removes all spaces, splits the string at the char '/' and tries to parse the results to
     * integers.
     * NumberFormatException not catched.
     *
     * @param reps String to parse to integers.
     * @return Array representing the String.
     */
    private Integer[] parseReps(String reps) {
        String[] splitted = reps.trim().replaceAll("\\s+", "").split("/");
        Integer[] toReturn = new Integer[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            toReturn[i] = Integer.parseInt(splitted[i].trim());
        }
        return toReturn;
    }

    private Integer[] parseWeightDiff(String weightDiff) {
        if (weightDiff == null || weightDiff.trim().replaceAll("\\s+","").equals("")) {
            return new Integer[]{0};
        } else {
            String[] splitted = weightDiff.trim().replaceAll("\\s+", "").split("/");
            Integer[] toReturn = new Integer[splitted.length];
            for (int i = 0; i < splitted.length; i++) {
                toReturn[i] = Integer.parseInt(splitted[i].trim());
            }
            return toReturn;
        }
    }

    /**
     * Checks if the user with the Mail userMail has a plan and if he hasn't assigns the initial one to him.
     * Then checks if the CurrentSession the plan is in is valid and throws an RunTimeException if not.
     * Searches for the Plan Template the UserPlan is based on.
     * Fills the TrainingsDayDto with the necessary Data.
     * If this isn't the initial training of the plan (currentSession == 0), where the weights are assigned from the user,
     * calculates based on the template, the currentSession and the Weights the user provided the Weights which have
     * to be used on each exercise on each set.
     * Fills the Data for each Exercise in a ExerciseDto and adds it to the List in the TrainingsDayDto.
     *
     * For Exercises with WeightType SELF_WEIGHT, no weight is calculated with the Repetition Maximum but the value of
     * weightDiff in the Plan Template is directly given to the user.
     *
     * @param userMail identifies the user.
     * @param trainingsDayDto dto to be filled with Data provided by the Controller.
     * @return the Dto with Data necessary for Training.
     */
    public TrainingsDayDto fillTrainingsDayDto(String userMail, TrainingsDayDto trainingsDayDto) {
        DataBaseService db = DataBaseService.getInstance();
        UserPlan userPlan = db.getUserPlanByUserMail(userMail);
        if (userPlan == null) {     // if the user has no plan assign one to him
             throw new NoPlanException();
        }
        int currentSession = userPlan.getCurrentSession();
        if (currentSession < 0) {
            throw new IllegalArgumentException("Session number of User Plan corrupted");
        } else if (currentSession > userPlan.getMaxSession()) {
            log.debug("User:" + userMail + ": Plan expired new Plan needed!");
            throw new IllegalArgumentException("Plan expired new Plan needed!");
        } else {
            trainingsDayDto.setExercises(createExerciseDtosForDay(userPlan, currentSession));
            trainingsDayDto.setInitialTraining(!userPlan.isInitialTrainingDone());
            trainingsDayDto.setCurrentSession(currentSession);
            return trainingsDayDto;
        }
    }

    /**
     * Creates the exerciseDto for the traingsDayDto based on an given exercise instance.
     * Sets and calculates all values required in the dto based on the userplan, session and instance.
     *
     * @param exInstance Instance the Dto represents
     * @param userPlan userplan the dto belongs to
     * @param currentSession currents session the plan is in
     * @return the new created dto
     */
    public ExerciseDto createExerciseDto(ExerciseInstance exInstance, UserPlan userPlan, int currentSession) {
        DataBaseService db = DataBaseService.getInstance();
        ExerciseDto newDto = new ExerciseDto();
        Exercise exercise = db.getExerciseById(exInstance.getIsExerciseID());
        TrainingsSession trainingsSession;
        if (!userPlan.isInitialTrainingDone() && currentSession == 0) {
            newDto.setWeights(null);
            newDto.setFirstTraining(true);
            newDto.setWeightDone(0);
            trainingsSession = getSessionByOrdering(1, exInstance.getTrainingsSessions()); // in initial Workout take values from first Trainings session
        } else {
            // when the initial Training was already completed calculate the values for the current Session
            trainingsSession = getSessionByOrdering(currentSession, exInstance.getTrainingsSessions());
            if (exercise.getWeightType() != WeightType.SELF_WEIGHT) {
                // selfweight exercises require no calculation
                newDto.setWeights(calcWeights(db.getWeightForUserPlanExercise(userPlan.getId(), exInstance.getId()), trainingsSession.getSets(), trainingsSession.getWeightDiff()));
            } else {
                newDto.setWeights(trainingsSession.getWeightDiff());
            }
            newDto.setFirstTraining(false);
        }
        // add all Data to the ExerciseDto
        newDto.setIdUserPlan(userPlan.getId());
        newDto.setIdExerciseInstance(exInstance.getId());
        newDto.setExercise(exercise);
        newDto.setCategory(exInstance.getCategory());
        newDto.setTags(exInstance.getTags());
        newDto.setSets(trainingsSession.getSets());
        newDto.setTempo(trainingsSession.getTempo());
        newDto.setPause(trainingsSession.getPauseInSec());
        newDto.setReps(trainingsSession.getReps());
        newDto.setRepMax(exInstance.getRepetitionMaximum());
        return newDto;
    }

    private LinkedList<ExerciseDto> createExerciseDtosForDay(UserPlan userPlan, int currentSession) {
        DataBaseService db = DataBaseService.getInstance();
        // get the Trainings plan Template the User plan is based on
        TrainingsPlanTemplate template = db.getPlanTemplateAndSessionsByID(userPlan.getIdOfTemplate());
        LinkedList<ExerciseDto> toReturn = new LinkedList<>();
        for (ExerciseInstance exInstance : template.getExerciseInstances()) {
            toReturn.add(createExerciseDto(exInstance, userPlan, currentSession));
        }
        return toReturn;
    }

    /**
     * Sets the initial Training for a user plan in the Database done
     *
     * @param userPlanId id to identify plan.
     */
    public void setUserPlanInitialTrainDone(int userPlanId) {
        DataBaseService.getInstance().setInitialTrainDone(userPlanId);
    }

    /**
     * Used for the initial exercise when the User provides his Repetition Maximum for each Exercise.
     * Saves the Weight in the Database.
     *
     * @param exerciseDto Holds the data. Used are idUserPlan, idExerciseInstance and weightDone.
     */
    public void setWeightsOfExercise(ExerciseDto exerciseDto) {
        if (!exerciseDto.getFirstTraining()) {
            throw new IllegalArgumentException("Initial Training already completed!");
        } else if (exerciseDto.getDone()){
            DataBaseService.getInstance().insertWeightsForUserPlan(exerciseDto.getIdUserPlan(),
                    exerciseDto.getIdExerciseInstance(), exerciseDto.getWeightDone());
        }
    }

    /**
     * Confirms a plantemplate so users can use it.
     * @param idOfTemplate id of template
     */
    public void confirmPlan(int idOfTemplate) {
        DataBaseService.getInstance().confirmPlan(idOfTemplate);
    }

    private Integer[] calcWeights(Integer weight, int setsNum, Integer[] weightDiff) {
        Integer[] weights = new Integer[setsNum];
        Arrays.fill(weights, weight);
        for (int i = 0; i < weights.length; i++) {
            weights[i] = (int) Math.round(weights[i] * (1.0 + (weightDiff[i] / 100.0)));
        }
        return weights;
    }

    private TrainingsSession getSessionByOrdering(int ordering, LinkedList<TrainingsSession> sessions) {
        for (TrainingsSession session : sessions) {
            if (session.getOrdering() == ordering) {
                return session;
            }
        }
        throw new IllegalArgumentException("No Session with this ordering available!");
    }

    /**
     * Checks all exercises of a TrainingsDayDto are completed. When they are increments the currentSession
     * stat of the UserPlan in the database. When a Plan expires (reaches its MaxSessions)
     * the plan will be deleted from the db and the user.
     *
     * @param dayDto Dto representing the current Training Day
     * @return true if all exercises are done false if not.
     */
    public boolean checkIfDayDone(TrainingsDayDto dayDto) {
        boolean isDone = true;
        for (ExerciseDto exDto : dayDto.getExercises()) {
            if (!exDto.getDone()) {
                isDone = false;
            }
        }
        if (isDone) {
            DataBaseService.getInstance().increaseCurSession(dayDto.getExercises().getFirst().getIdUserPlan());
        }
        return isDone;
    }

    /**
     * Get all TrainingPlanTemplates without children where the flag oneshot plan is set from the database.
     *
     * @return LinkedList of the templates
     */
    public LinkedList<TrainingsPlanTemplate> getOneShotPlansForUser() {
        DataBaseService db = DataBaseService.getInstance();
        return db.getAllOneShotPlans();
    }

    /**
     * Checks if the necessary user data is missing or if the user already has an active plan.
     * Throws an according exception.
     *
     * Otherwise will search for a plans suited for the user. First try with exact Trainings frequency if nothing was found with frequency +/-1.
     * When still no plan is found returns null.
     * If plans were found chooses a plan at random, assign it to the user in the database and returns it.
     *
     * @param userMail email of the user to identify him.
     * @return null if no suited plan was found; the TrainingsPlanTemplate the plan is based on.
     * @throws IllegalArgumentException when user has no data or an active plan an according exception is thrown.
     */
    public TrainingsPlanTemplate getPlanForUser(String userMail) throws IllegalArgumentException {
        DataBaseService db = DataBaseService.getInstance();
        User user = db.getUserByEmail(userMail);
        if (user.getExperience() != null && user.getTrainingsFocus() != null && user.getTrainingsFrequency() != null) {
            if (db.getUserPlanByUserMail(userMail) != null) {
                throw new IllegalArgumentException("Du musst erst deinen aktuellen Plan beenden bevor du einen neuen erhalten kannst.");
            }
            LinkedList<TrainingsPlanTemplate> suitedPlans = db.getSuitedPlans(false, user.getExperience(),
                    user.getTrainingsFocus(), user.getTrainingsFrequency(), true); // looks for plans with exact Trainings Frequency
            if (suitedPlans == null){
                suitedPlans = db.getSuitedPlans(false, user.getExperience(),
                        user.getTrainingsFocus(), user.getTrainingsFrequency(), false); // looks for plans with similar Trainings Frequency
            }
            if (suitedPlans == null){
                return null;
            } else  {
                Random rand = new Random();
                TrainingsPlanTemplate randomTemplate = suitedPlans.get(rand.nextInt(suitedPlans.size()));
                db.insertUserPlan(userMail, randomTemplate.getId());
                return randomTemplate;
            }
        } else {
            throw new IllegalArgumentException("Du musst erst dein Nutzerdaten angeben bevor wir dir einen Plan zuweisen können.");
        }
    }

    /**
     * Checks if the user already as an active plan and whether the templates and the user with the provided ids exist.
     * Throws an according exception.
     *
     * Assigns the given plan to the user in the database.
     *
     * @param email to identify user.
     * @param id to identify plan.
     * @throws IllegalArgumentException when user has active plan, template or user doesn't exist.
     */
    public void assignPlan(String email, int id) throws  IllegalArgumentException {
        DataBaseService db = DataBaseService.getInstance();
        if (db.getUserPlanByUserMail(email) != null) {
            throw new IllegalArgumentException("Du musst erst deinen aktuellen Plan beenden bevor du einen neuen erhalten kannst.");
        } else if (db.getOnlyPlanTemplateById(id) == null) {
            throw new IllegalArgumentException("Vorlage existiert nicht");
        } else if(db.getUserByEmail(email) == null) {
            throw new IllegalArgumentException("Benutzer existiert nicht");
        } else {
            db.insertUserPlan(email, id);
        }
    }

    /**
     * Checks if conditions are met throws according exception if not.
     * Calculates with the percentage of done repetition the adjustment of the weight and then the new weight
     * and forwards it to the database.
     *
     * @param usermail email to identify the user and his plan
     * @param idOfExInstance id of the exercise instance the weights belong to
     * @param percentRepsDifference percentage of weights done to much positive to few negativ.
     */
    public void adjustWeightsOfUserForExercise(String usermail, int idOfExInstance, int percentRepsDifference) {
        DataBaseService db = DataBaseService.getInstance();
        User user = db.getUserByEmail(usermail);
        if (user == null) {
            throw new IllegalArgumentException("User doesn't exist");
        }
        UserPlan userPlan = db.getUserPlanByUserMail(usermail);
        if (userPlan == null) {
            throw new IllegalArgumentException("User has no plan to adjust");
        }
        Exercise exercise = db.getExerciseById(db.getExercisInstanceById(idOfExInstance).getIsExerciseID());
        if (exercise.getWeightType() == WeightType.SELF_WEIGHT) {
            throw new IllegalArgumentException("Self weight exercises have no weight assigned");
        }
        Integer weight = db.getWeightForUserPlanExercise(userPlan.getId(), idOfExInstance);
        if (weight == null) {
            throw new IllegalArgumentException("User has no weight assigned to this exercise");
        }
        int adjustment;
        int sign = 1;
        if (percentRepsDifference < 0) {
            sign = -1;
            percentRepsDifference = percentRepsDifference * -1;
        }
        if (percentRepsDifference < 10) {
            return;
        } else if (percentRepsDifference < 25) {
            adjustment = 5;
        } else if (percentRepsDifference < 50) {
            adjustment = 10;
        } else {
            adjustment = 15;
        }
        adjustment = adjustment * sign;
        int newWeight = (int) Math.round(weight * (1.0 + (adjustment / 100.0)));
        db.alterWeightForUserPlanExercise(userPlan.getId(), idOfExInstance, newWeight);
    }

    /**
     * Returns an exercise instance object with a given id from the database. null if nothing found.
     * @param idOfExInst id of the instance to search
     * @return found instance or null
     */
    public ExerciseInstance getExerciseInstanceById(int idOfExInst) {
        return DataBaseService.getInstance().getExercisInstanceById(idOfExInst);
    }

    /**
     * Returns a user Plan object with a given id from the database. null if nothing found.
     *
     * @param idUserPlan id of the plan to look for.
     * @return found plan or null
     */
    public UserPlan getUserPlanById(int idUserPlan) {
        return DataBaseService.getInstance().getUserPlanById(idUserPlan);
    }

    //Edit Plan

    public void editExerciseInstance(ExerciseInstance exIn) {
        DataBaseService db = DataBaseService.getInstance();
        for(TrainingsSession session : exIn.getTrainingsSessions()) {
            if(session.getWeightDiff().length == 0) {
                Integer[] newWeightDiff = new Integer[] {0};
                session.setWeightDiff(newWeightDiff);
            }
            session.setWeightDiff(setWeightDiffToReps(session.getReps(), session.getWeightDiff()));
        }
        db.editExerciseInstance(exIn);
    }

    public void editPlanTemplate(TrainingsPlanTemplate tpt) {
        DataBaseService.getInstance().editPlanTemplate(tpt);
    }
}

package de.ep.team2.core.service;

import de.ep.team2.core.dtos.CreatePlanDto;
import de.ep.team2.core.dtos.ExerciseDto;
import de.ep.team2.core.dtos.TrainingsDayDto;
import de.ep.team2.core.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.LinkedList;

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
     */
    public void deleteTemplateAndChildrenById(int id) {
        DataBaseService db = DataBaseService.getInstance();
        TrainingsPlanTemplate tempToDelete = db.getPlanTemplateAndSessionsByID(id);
        for (ExerciseInstance ei : tempToDelete.getExerciseInstances()) {
            for (TrainingsSession ts : ei.getTrainingsSessions()) {
                db.deleteTrainingsSessionById(ts.getId());
            }
            db.deleteExerciseInstanceByID(ei.getId());
        }
        db.deletePlanTemplateByID(id);
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
     * Returns a Template with a specific id with all its children added to the Object.
     *
     * @param id id of template to return.
     * @return Template object with all children.
     */
    public TrainingsPlanTemplate getPlanTemplateAndSessionsByID(Integer id) {
        TrainingsPlanTemplate toReturn =  DataBaseService.getInstance().getPlanTemplateAndSessionsByID(id);
        if (toReturn == null) {
            throw new IllegalArgumentException("Plan with Id " + id + "not found!");
        } else {
            return toReturn;
        }
    }

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
            Integer[] weightDiff = parseWeightDiff(dto.getWeightDiff().get(i));
            if (!validateWeightWithReps(reps, weightDiff)) {
                throw new IllegalArgumentException("reps and weights do not match!");
            }
            db.insertTrainingsSession(exInstanceId, i + 1, reps.length, weightDiff, reps,
                    dto.getTempo().get(i), dto.getPause().get(i));
        }
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
        } else if (weightDiff.length == 1 && reps.length < 1) {
            Integer value = weightDiff[0];
            weightDiff = new Integer[reps.length];
            Arrays.fill(weightDiff, value);
            return true;
        } else {
            return false;
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
     * todo
     *
     * @param userMail
     * @param trainingsDayDto
     * @return
     */
    public TrainingsDayDto fillTrainingsDayDto(String userMail, TrainingsDayDto trainingsDayDto) {
        DataBaseService db = DataBaseService.getInstance();
        UserPlan userPlan = db.getUserPlanByUserMail(userMail);
        if (userPlan == null) {     // if the user has no plan assign one to him
            db.insertUserPlan(userMail, 1); // atm the user just gets the initial plan
            userPlan = db.getUserPlanByUserMail(userMail);
        }
        int currentSession = userPlan.getCurrentSession();
        if (currentSession < 0) {
            throw new IllegalArgumentException("Session number of User Plan corrupted");
        } else if (currentSession > userPlan.getMaxSession()) {
            log.debug("User:" + userMail + ": Plan expired new Plan needed!");
            throw new IllegalArgumentException("Plan expired new Plan needed!");
        } else {
            // get the Trainings plan Template the User plan is based on
            TrainingsPlanTemplate template = db.getPlanTemplateAndSessionsByID(userPlan.getIdOfTemplate());
            LinkedList<ExerciseDto> exerciseDtos = new LinkedList<>();
            for (ExerciseInstance exInstance : template.getExerciseInstances()) {
                // create the ExerciseDto based on each ExerciseInstance and the current TrainingsSession
                ExerciseDto newDto = new ExerciseDto();
                TrainingsSession trainingsSession;
                if (!userPlan.isInitialTrainingDone() && currentSession == 0) {
                    newDto.setWeights(null);
                    newDto.setFirstTraining(true);
                    newDto.setWeightDone(0);
                    trainingsSession = getSessionByOrdering(1, exInstance.getTrainingsSessions()); // in initial Workout take values from first Trainings session
                } else {
                    // when the initial Training was already completed calculate the values for the current Session
                    trainingsSession = getSessionByOrdering(currentSession, exInstance.getTrainingsSessions());
                    newDto.setWeights(calcWeights(db.getWeightForUserPlanExercise(userPlan.getId(), exInstance.getId()), trainingsSession.getSets(), trainingsSession.getWeightDiff()));
                    newDto.setFirstTraining(false);
                }
                // add all Data to the ExerciseDto
                newDto.setIdUserPlan(userPlan.getId());
                newDto.setIdExerciseInstance(exInstance.getId());
                Exercise exercise = db.getExerciseById(exInstance.getIsExerciseID());
                newDto.setExercise(exercise);
                newDto.setCategory(exInstance.getCategory());
                newDto.setSets(trainingsSession.getSets());
                newDto.setTempo(trainingsSession.getTempo());
                newDto.setPause(trainingsSession.getPauseInSec());
                newDto.setReps(trainingsSession.getReps());
                newDto.setRepMax(exInstance.getRepetitionMaximum());
                exerciseDtos.add(newDto);
            }
            trainingsDayDto.setExercises(exerciseDtos);
            trainingsDayDto.setInitialTraining(!userPlan.isInitialTrainingDone());
            trainingsDayDto.setCurrentSession(currentSession);
            return trainingsDayDto;
        }
    }

    public void setUserPlanInitialTrainDone(int userPlanId) {
        DataBaseService.getInstance().setInitialTrainDone(userPlanId);
    }

    public void setWeightsOfExercise(ExerciseDto exerciseDto) {
        if (!exerciseDto.getFirstTraining()) {
            throw new IllegalArgumentException("Initial Training already completed!");
        } else if (exerciseDto.getDone()){
            DataBaseService.getInstance().insertWeightsForUserPlan(exerciseDto.getIdUserPlan(),
                    exerciseDto.getIdExerciseInstance(), exerciseDto.getWeightDone());
        }
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

    public boolean checkIfPlanDone(TrainingsDayDto dayDto) {
        DataBaseService db = DataBaseService.getInstance();
        UserPlan userPlan = db.getUserPlanById(dayDto.getExercises().getFirst().getIdUserPlan());
        return userPlan.getCurrentSession() == userPlan.getMaxSession();
    }
}

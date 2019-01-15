package de.ep.team2.core.service;

import de.ep.team2.core.dtos.CreatePlanDto;
import de.ep.team2.core.dtos.ExerciseDto;
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
            // TODO: 15.01.19 check one value for all and check if same as sets in new method
        }
    }

    // TODO: 14.01.19 Linking fot the 2 events
    public LinkedList<ExerciseDto> createTrainingsDay(String userMail) {
        DataBaseService db = DataBaseService.getInstance();
        UserPlan userPlan = db.getUserPlanByUserMail(userMail);
        Integer currentSession = userPlan.getCurrentSession();
        currentSession++; // rm when link to first train
        if (currentSession == null || currentSession < 0) {
            throw new IllegalArgumentException("Session number of User Plan corrupted");
        } else if (currentSession >= userPlan.getMaxSession()) {
            log.debug("User:" + userMail + ": Plan expired new Plan needed!");
            throw new IllegalArgumentException("Plan expired new Plan needed!");
        } else if (currentSession == 0) {
            log.debug("User:" + userMail + ": Initial Training not completed!");
            throw new IllegalArgumentException("Initial Training not completed!");
        } else {
            TrainingsPlanTemplate template = db.getPlanTemplateAndSessionsByID(userPlan.getIdOfTemplate());
            LinkedList<ExerciseDto> toReturn = new LinkedList<>();
            for (ExerciseInstance exInstance : template.getExerciseInstances()) {
                TrainingsSession trainingsSession = getSessionByOrdering(currentSession, exInstance.getTrainingsSessions());
                Exercise exercise = db.getExerciseById(exInstance.getIsExerciseID());
                Integer[] weights = db.getWeightsForOneDay(userPlan.getId(),exInstance.getId(),currentSession);
                ExerciseDto newDto = new ExerciseDto();
                newDto.setExercise(exercise);
                newDto.setCategory(exInstance.getCategory());
                newDto.setSets(trainingsSession.getSets());
                newDto.setTempo(trainingsSession.getTempo());
                newDto.setPause(trainingsSession.getPauseInSec());
                newDto.setReps(trainingsSession.getReps());
                newDto.setWeights(weights);
                newDto.setFirstTraining(false);
                newDto.setWeightDone(null);
                newDto.setRepMax(exInstance.getRepetitionMaximum());
                toReturn.add(newDto);
            }
            return toReturn;
        }
    }

    private TrainingsSession getSessionByOrdering(int ordering, LinkedList<TrainingsSession> sessions) {
        for (TrainingsSession session : sessions) {
            if (session.getOrdering() == ordering) {
                return session;
            }
        }
        throw new IllegalArgumentException("No Session with this ordering available!");
    }
}

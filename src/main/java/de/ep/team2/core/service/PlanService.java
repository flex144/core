package de.ep.team2.core.service;

import de.ep.team2.core.dtos.CreatePlanDto;
import de.ep.team2.core.entities.ExerciseInstance;
import de.ep.team2.core.entities.TrainingsPlanTemplate;
import de.ep.team2.core.entities.TrainingsSession;
import de.ep.team2.core.entities.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedList;

public class PlanService {

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
        idTemplate = db.insertPlanTemplate(dto.getPlanName(), dto.getTrainingsFocus(), mail,
                dto.isOneShot(), dto.getSessionNums(), 1);
        Integer exInstanceId = db.insertExerciseInstance(dto.getExerciseID(), dto.getCategory(),
                dto.getTags(), idTemplate);
        for (int i = 0; i < dto.getSessionNums(); i++) {
            Integer[] reps = parseSets(dto.getSets().get(i));
            db.insertTrainingsSession(exInstanceId, i + 1, 15, reps.length, reps,
                    dto.getTempo().get(i), dto.getPause().get(i));
        }
        return idTemplate;
    }

    private void addInstanceAndSessionToExistingPlan(CreatePlanDto dto, Integer idOfTemplate) {
        DataBaseService db = DataBaseService.getInstance();
        Integer exInstanceId = db.insertExerciseInstance(dto.getExerciseID(), dto.getCategory(),
                dto.getTags(), dto.getId());
        for (int i = 0; i < dto.getSessionNums(); i++) {
            Integer[] reps = parseSets(dto.getSets().get(i));
            db.insertTrainingsSession(exInstanceId, i + 1, 15, reps.length, reps,
                    dto.getTempo().get(i), dto.getPause().get(i));
        }
        db.increaseNumOfExercises(idOfTemplate);
    }

    public ExerciseInstance getExerciseInstanceById(int id) {
        return DataBaseService.getInstance().getExercisInstanceById(id);
    }

    /**
     * removes all spaces, splits the string at the char '/' and tries to parse the results to
     * integers.
     * NumberFormatException not catched.
     *
     * @param sets String to parse to integers.
     * @return Array representing the String.
     */
    private Integer[] parseSets(String sets) {
        String[] splitted = sets.trim().replaceAll("\\s+", "").split("/");
        Integer[] toReturn = new Integer[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            toReturn[i] = Integer.parseInt(splitted[i].trim());
        }
        return toReturn;
    }
}

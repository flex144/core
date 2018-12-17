package de.ep.team2.core.service;

import de.ep.team2.core.dtos.CreatePlanDto;
import de.ep.team2.core.entities.TrainingsPlanTemplate;
import de.ep.team2.core.entities.TrainingsSession;
import de.ep.team2.core.entities.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedList;

public class PlanService {

    public CreatePlanDto createPlan(CreatePlanDto dto) {
        if (dto.getId() == null) {
            dto.setId(insertNewPlan(dto));
        } else {
            changePlanNameAndFocusOnChange(dto);
            addInstanceAndSessionToExistingPlan(dto, dto.getId());
        }
        return dto;
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
                (dto.getSessionNums() == 1), dto.getSessionNums(), 1);
        Integer exInstanceId = db.insertExerciseInstance(dto.getExerciseID(), dto.getCategory(),
                dto.getDescription(), idTemplate);
        for (int i = 0; i < dto.getSessionNums(); i++) {
            Integer[] reps = parseSets(dto.getSets().get(i));
            db.insertTrainingsSession(exInstanceId, i+1, 15, reps.length, reps,
                    dto.getTempo().get(i), dto.getPause().get(i));
        }
        return idTemplate;
    }

    private void addInstanceAndSessionToExistingPlan(CreatePlanDto dto, Integer idOfTemplate) {
        DataBaseService db = DataBaseService.getInstance();
        Integer exInstanceId = db.insertExerciseInstance(dto.getExerciseID(), dto.getCategory(),
                dto.getDescription(), dto.getId());
        for (int i = 0; i < dto.getSessionNums(); i++) {
            Integer[] reps = parseSets(dto.getSets().get(i));
            db.insertTrainingsSession(exInstanceId, i+1, 15, reps.length, reps,
                    dto.getTempo().get(i), dto.getPause().get(i));
        }
        db.increaseNumOfExercises(idOfTemplate);
    }

    private Integer[] parseSets(String sets) {
        String[] splitted = sets.trim().replaceAll("\\s+", "").split("/");
        Integer[] toReturn = new Integer[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            toReturn[i] = Integer.parseInt(splitted[i].trim());
        }
        return toReturn;
    }
}

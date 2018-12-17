package de.ep.team2.core.service;

import de.ep.team2.core.dtos.CreatePlanDto;
import de.ep.team2.core.entities.TrainingsSession;
import de.ep.team2.core.entities.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedList;

public class PlanService {

    public CreatePlanDto createPlan(CreatePlanDto dto) {
        if (dto.getId() == null) {
            dto.setId(insertNewPlan(dto));
        } else {
            addSessionsAndInstancesToExistingPlan(dto, dto.getId());
        }
        return dto;
    }

    private Integer insertNewPlan(CreatePlanDto dto) {
        DataBaseService db = DataBaseService.getInstance();
        Integer id;
            User user = (User) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            String mail = user.getEmail();
            id = db.insertPlanTemplate(dto.getPlanName(), null, mail,
                    (dto.getSessionNums() == 1), dto.getSessionNums(), 1);
        for (int i = 0; i < dto.getSessionNums(); i++) {
            int idOfTs = db.insertTrainingsSession(id, i + 1);
            Integer[] reps = parseSets(dto.getSets().get(i));
            db.insertExerciseInstance(dto.getExerciseID(), idOfTs, 15, reps.length, reps,
                    dto.getTempo().get(i), dto.getPause().get(i));
        }
        return id;
    }

    private void addSessionsAndInstancesToExistingPlan(CreatePlanDto dto, Integer idOfTemplate) {
        DataBaseService db = DataBaseService.getInstance();
        LinkedList<TrainingsSession> sessions = db.getOnlySessionsOfTemplate(idOfTemplate);
        for (int i = 0; i < dto.getSessionNums(); i++) {
            int idOfTs = sessions.get(i).getId();
            Integer[] reps = parseSets(dto.getSets().get(i));
            db.insertExerciseInstance(dto.getExerciseID(), idOfTs, 15, reps.length, reps,
                    dto.getTempo().get(i),
                    dto.getPause().get(i));
        }
        db.increaseNumOfExercises(idOfTemplate);
    }

    private Integer[] parseSets(String sets) {
        String[] splitted = sets.trim().replaceAll("\\s+","").split("/");
        Integer[] toReturn = new Integer[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            toReturn[i] = Integer.parseInt(splitted[i].trim());
        }
        return toReturn;
    }
}

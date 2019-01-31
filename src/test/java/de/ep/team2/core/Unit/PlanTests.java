package de.ep.team2.core.Unit;

import de.ep.team2.core.dtos.CreatePlanDto;
import de.ep.team2.core.dtos.TrainingsDayDto;
import de.ep.team2.core.entities.TrainingsPlanTemplate;
import de.ep.team2.core.entities.User;
import de.ep.team2.core.entities.UserPlan;
import de.ep.team2.core.enums.ExperienceLevel;
import de.ep.team2.core.enums.TrainingsFocus;
import de.ep.team2.core.service.DataBaseService;
import de.ep.team2.core.service.PlanService;
import de.ep.team2.core.service.UserService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlanTests {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    private PlanService planService = new PlanService();


    @Test
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
    public void createAndDeletePlanTemplateTest() {
        CreatePlanDto createPlanDto = createTestDto();
        assertEquals(2, (int) createPlanDto.getExerciseID());
        Integer id = planService.createPlan(createPlanDto).getId();
        TrainingsPlanTemplate tpt = planService.getPlanTemplateAndSessionsByID(id);
        assertEquals(tpt.getName(), createPlanDto.getPlanName());
        assertEquals(1, tpt.getExerciseInstances().size());
        assertEquals(3, tpt.getExerciseInstances().getFirst().getTrainingsSessions().size());
        assertEquals(20, (int) tpt.getExerciseInstances().getFirst().getTrainingsSessions().get(1).getReps()[1]);
        planService.deleteTemplateAndChildrenById(id);
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Plan with Id " + id + " not found!");
        assertNull(planService.getPlanTemplateAndSessionsByID(id));
    }

    private CreatePlanDto createTestDto() {
        CreatePlanDto createPlanDto = new CreatePlanDto();
        createPlanDto.setPlanName("Testing the Plan creation");
        createPlanDto.setTrainingsFocus(TrainingsFocus.MUSCLE);
        createPlanDto.setTargetGroup(ExperienceLevel.BEGINNER);
        createPlanDto.setId(null); // for creating new plan
        createPlanDto.setSessionNums(3);
        createPlanDto.setCategory("A1");
        createPlanDto.setExerciseName("Liegestütz");
        createPlanDto.nameToId();
        createPlanDto.setOneShot(false);
        createPlanDto.setRepetitionMaximum(15);
        createPlanDto.setRecomSessionsPerWeek(1);
        createPlanDto.setWeightDiff(new LinkedList<>(Arrays.asList("5/10/15","","30")));
        createPlanDto.setSets(new LinkedList<>(Arrays.asList("10/10/10","   15 /   20 / 25","40/40/40 ")));
        createPlanDto.setTempo(new LinkedList<>(Arrays.asList("langsam","mittel","schnell")));
        createPlanDto.setPause(new LinkedList<>(Arrays.asList(60,90,30)));
        createPlanDto.setTags(new LinkedList<>(Arrays.asList("eng","sauber")));
        return createPlanDto;
    }

    @Test
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
    public void testChangeNameAndFocus() {
        CreatePlanDto dto = createTestDto();
        CreatePlanDto returnedDto = planService.createPlan(dto);
        assertEquals("Testing the Plan creation", returnedDto.getPlanName());
        assertEquals(TrainingsFocus.MUSCLE, returnedDto.getTrainingsFocus());
        returnedDto.setPlanName("Anderung");
        returnedDto.setTrainingsFocus(TrainingsFocus.STAMINA);
        returnedDto = planService.createPlan(returnedDto);
        assertEquals("Anderung", returnedDto.getPlanName());
        assertEquals(TrainingsFocus.STAMINA, returnedDto.getTrainingsFocus());
        planService.deleteTemplateAndChildrenById(returnedDto.getId());
    }

    @Test
    public void userPlanDayCycle() {
        String userMail = "felix@gmail.com";
        TrainingsPlanTemplate planBasedOn = planService.getPlanTemplateAndSessionsByID(1); // initial tpt
        DataBaseService.getInstance().insertUserPlan(userMail, 1); // initial tpt
        TrainingsDayDto dayDto = planService.fillTrainingsDayDto(userMail, new TrainingsDayDto());
        assertTrue(dayDto.isInitialTraining());
        assertEquals(0, (int) dayDto.getCurrentSession());
        assertEquals(planBasedOn.getExerciseInstances().size(), dayDto.getExercises().size());
        // completes initial training
        initTrain(dayDto, 50, userMail);
        // completes all Trainings of the User Plan
        for (int i = 0; i < 6; i++) {
            dayDto.clear();
            planService.fillTrainingsDayDto(userMail, dayDto);
            dayDto.getExercises().get(0).setDone(true);
            dayDto.getExercises().get(1).setDone(true);
            planService.checkIfDayDone(dayDto);
        }
        // creates a new User plan
        dayDto.clear();
        DataBaseService.getInstance().insertUserPlan(userMail, 1); // initial tpt
        planService.fillTrainingsDayDto(userMail, dayDto);
        assertTrue(dayDto.isInitialTraining());
    }

    @Test
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
    public void deletePlanOfUser() {
        DataBaseService db = DataBaseService.getInstance();
        String userMail = "PlanTest@Test.com";
        // create user
        int idOfNewUser = db.insertUser(userMail, null, null, "12345");
        // create plan
        DataBaseService.getInstance().insertUserPlan(userMail, 1); // initial tpt
        planService.fillTrainingsDayDto(userMail, new TrainingsDayDto());
        assertNotNull(db.getUserPlanByUserMail(userMail));
        // delete user and plan
        db.deleteUserById(idOfNewUser);
        assertNull(db.getUserPlanByUserMail(userMail));
        assertNull(db.getUserById(idOfNewUser));
    }

    @Test
    public void assignPlan() {
        String userMail = "alex@gmail.com";
        DataBaseService db = DataBaseService.getInstance();
        assertNull(db.getUserPlanByUserMail(userMail));
        try {
            planService.getPlanForUser(userMail);
        } catch (IllegalArgumentException e) {
            assertEquals("Du musst erst dein Nutzerdaten angeben bevor wir dir einen Plan zuweisen können.",
                    e.getMessage());
        }
        assertNull(db.getUserPlanByUserMail(userMail));
        db.setAdvancedUserData(null, null, TrainingsFocus.STAMINA, 2, null, ExperienceLevel.BEGINNER, null, userMail); // no plan for this data in the system
        assertNull(planService.getPlanForUser(userMail));
        assertNull(db.getUserPlanByUserMail(userMail));
        db.setAdvancedUserData(null, null, TrainingsFocus.MUSCLE, 2, null, ExperienceLevel.BEGINNER, null, userMail); // test plan matches
        assertNotNull(planService.getPlanForUser(userMail));
        assertNotNull(db.getUserPlanByUserMail(userMail));
        try {
            planService.getPlanForUser(userMail);
        } catch (IllegalArgumentException e) {
            assertEquals("Du musst erst deinen aktuellen Plan beenden bevor du einen neuen erhalten kannst.",
                    e.getMessage());
        }
        db.deleteUserPlanAndWeightsById(db.getUserPlanByUserMail(userMail).getId());
        assertNull(db.getUserPlanByUserMail(userMail));
        planService.assignPlan(userMail, 2); // id of the one shot test plan
        assertNotNull(db.getUserPlanByUserMail(userMail));
    }

    private TrainingsDayDto initTrain(TrainingsDayDto dayDto, int weightDone, String userMail) {
        for (int i = 0; i < dayDto.getExercises().size(); i++) {
            assertFalse(planService.checkIfDayDone(dayDto));
            dayDto.getExercises().get(i).setDone(true);
            dayDto.getExercises().get(i).setWeightDone(weightDone);
            planService.setWeightsOfExercise(dayDto.getExercises().get(i));
        }
        assertTrue(planService.checkIfDayDone(dayDto));
        planService.setUserPlanInitialTrainDone(dayDto.getExercises().getFirst().getIdUserPlan());
        dayDto.clear();
        planService.fillTrainingsDayDto(userMail, dayDto);
        assertEquals(1, (int) dayDto.getCurrentSession());
        assertFalse(dayDto.isInitialTraining());
        return dayDto;
    }

    @Test
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
    public void adjustWeight() {
        UserService userService = new UserService();
        String userMail = "PlanTester@gg.com";
        userService.createUser(userMail, null, null, userService.encode("test123"));
        PlanService planService = new PlanService();
        planService.assignPlan(userMail, 1);
        TrainingsDayDto dayDto = planService.fillTrainingsDayDto(userMail, new TrainingsDayDto());
        initTrain(dayDto, 100, userMail);
        dayDto.clear();
        planService.fillTrainingsDayDto(userMail, dayDto);
        assertEquals(100, dayDto.getExercises().getFirst().getWeights()[0].intValue());
        planService.adjustWeightsOfUserForExercise(userMail, dayDto.getExercises().getFirst().getIdExerciseInstance(), -50);
        dayDto.clear();
        planService.fillTrainingsDayDto(userMail, dayDto);
        assertEquals(85, dayDto.getExercises().getFirst().getWeights()[0].intValue());
        planService.adjustWeightsOfUserForExercise(userMail, dayDto.getExercises().getFirst().getIdExerciseInstance(), +50);
        dayDto.clear();
        planService.fillTrainingsDayDto(userMail, dayDto);
        assertEquals(98, dayDto.getExercises().getFirst().getWeights()[0].intValue());
        assertEquals(0, dayDto.getExercises().get(1).getWeights()[0].intValue());
        userService.deleteUserByID(userService.getUserByEmail(userMail).getId());
    }
}

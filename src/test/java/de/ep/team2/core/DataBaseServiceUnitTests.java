package de.ep.team2.core;


import de.ep.team2.core.entities.TrainingsPlanTemplate;
import de.ep.team2.core.enums.WeightType;
import de.ep.team2.core.service.DataBaseService;
import de.ep.team2.core.service.UserService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataBaseServiceUnitTests {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    // User

    @Test
    public void getUserById() {
        DataBaseService db = DataBaseService.getInstance();
        assertTrue(db.getUserById(1).toString().contains("1"));
        assertNull(db.getUserById(345435345));
    }

    @Test
    public void getUserByEmail() {
        DataBaseService db = DataBaseService.getInstance();
        assertTrue(db.getUserByEmail("timo@gmail.com").toString().contains("timo"));
        assertNull(db.getUserByEmail("Testtest@rndtest123.com"));
    }

    @Test
    public void addUser() {
        DataBaseService db = DataBaseService.getInstance();
        assertNull(db.getUserByEmail("Hallo@test.com"));
        db.insertUser("Hallo@test.com","Hallo","Test", "123");
        assertTrue(db.getUserByEmail("Hallo@test.com").toString().contains("hallo@test.com"));
        db.deleteUserById(db.getUserByEmail("Hallo@test.com").getId());
    }

    @Test
    public void deleteUserById() {
        UserService userService = new UserService();
        DataBaseService db = DataBaseService.getInstance();
        assertNotNull(db.getUserById(1));
        db.deleteUserById(1);
        assertNull(db.getUserById(1));
        db.insertUser("timo@gmail123.com", "Timo", "Heinrich",
                userService.encode("123"));
    }

    @Test
    public void getAllUsers() {
        assertEquals(4, DataBaseService.getInstance().getAllUsers().size());
    }

    // Exercise

    @Test
    public void getExerciseById() {
        assertTrue(DataBaseService.getInstance().getExerciseById(1).toString().contains("1"));
        assertNull(DataBaseService.getInstance().getExerciseById(345345456));
    }

    @Test
    public void addExercise() {
        DataBaseService db = DataBaseService.getInstance();
        assertTrue(db.getExerciseListByName("HalloTest1234567").isEmpty());
        LinkedList<String[]> test = new LinkedList<>();
        test.add(new String[]{"Test/Test","other"});
        db.insertExercise("HalloTest1234567","Test Description", WeightType.FIXED_WEIGHT,
                null, test);
        assertTrue(db.getExerciseListByName("HalloTest1234567").toString()
                .contains("HalloTest1234567"));
        db.deleteExerciseById(db.getExerciseListByName("HalloTest1234567").get(0).getId());
    }

    @Test
    public void insertSameExercise() {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Exercise name already in use");
        LinkedList<String[]> test = new LinkedList<>();
        test.add(new String[]{"Test/Test","other"});
        DataBaseService.getInstance().insertExercise(
                "Bankdrücken", "Test for Illegal Argument", WeightType.FIXED_WEIGHT,
                "link.de./video",test);
    }

    @Test
    public void deleteExercise() {
        DataBaseService db = DataBaseService.getInstance();
        assertNotNull(db.getExerciseById(2));
        db.deleteExerciseById(2);
        assertNull(db.getExerciseById(2));
        db.insertExercise("TestEx", "To balance the amount of exercises after delete",
                WeightType.FIXED_WEIGHT,
                null, null);
    }

    @Test
    public void getAllExercises() {
        assertEquals(2, DataBaseService.getInstance().getAllExercises().size());
    }

    @Test
    public void getExerciseListByName() {
        DataBaseService db = DataBaseService.getInstance();
        assertTrue(db.getExerciseListByName("fkldsajflkösdjflkösadjf").isEmpty());
        assertNull(db.getExerciseListByName(null));
        assertEquals("Bankdrücken", db.getExerciseListByName("Bankdrücken").get(0).getName());
    }

    @Test
    public void getExerciseByName() {
        assertEquals("Bankdrücken",
                DataBaseService.getInstance().getExerciseByName("Bankdrücken").getName());
    }

    // Trainingsplan Templates

    @Test
    public void getTemplateByID() {
        assertEquals("Test Plan", DataBaseService.getInstance().getPlanTemplateByID(1).getName());
    }

    @Test
    public void insertTemplate() {
        DataBaseService db = DataBaseService.getInstance();
        assertNull(db.getPlanTemplateByID(2));
        db.insertPlanTemplate("Hallo2",null,"felix@gmail.com",false,5,5);
        assertEquals("Hallo2", db.getPlanTemplateByID(2).getName());
    }

    @Test
    public void deleteTemplate() {
        DataBaseService db = DataBaseService.getInstance();
        assertNull(db.getPlanTemplateByID(3));
        db.insertPlanTemplate("Hallo3",null,"felix@gmail.com",false,5,5);
        assertEquals("Hallo3", db.getPlanTemplateByID(3).getName());
        db.deletePlanTemplateByID(3);
        assertNull(db.getPlanTemplateByID(3));
    }

    // Trainings Sessions


    @Test
    public void insertTrainingsSession() {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Order of The Session Wrong! Order Already exists or isn't in the " +
                "valid number range!");
        DataBaseService.getInstance().insertTrainingsSession(1,2);
        DataBaseService.getInstance().insertTrainingsSession(1,2);
    }
}

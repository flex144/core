package de.ep.team2.core.Unit;


import de.ep.team2.core.enums.ExperienceLevel;
import de.ep.team2.core.enums.Gender;
import de.ep.team2.core.enums.TrainingsFocus;
import de.ep.team2.core.enums.WeightType;
import de.ep.team2.core.service.DataBaseService;
import de.ep.team2.core.service.UserService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import static org.junit.Assert.*;

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
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
    public void addUser() {
        DataBaseService db = DataBaseService.getInstance();
        assertNull(db.getUserByEmail("Hallo@test.com"));
        db.insertUser("Hallo@test.com","Hallo","Test", "123");
        assertTrue(db.getUserByEmail("Hallo@test.com").toString().contains("hallo@test.com"));
        db.deleteUserById(db.getUserByEmail("Hallo@test.com").getId());
    }

    @Test
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
    public void deleteUserById() {
        UserService userService = new UserService();
        DataBaseService db = DataBaseService.getInstance();
        Integer id = db.insertUser("timo@gmail123.com", "Timo", "Heinrich",
                userService.encode("123"));
        db.deleteUserById(id);
        assertNull(db.getUserById(id));
    }

    @Test
    public void getAllUsers() {
        assertEquals(5, DataBaseService.getInstance().getAllUsers().size());
    }

    @Test
    public void testAlterAdvancedData() {
        DataBaseService db = DataBaseService.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.set(1998,Calendar.AUGUST,22);
        Date date = cal.getTime();
        Integer id = db.insertUser("test@user.de", "test", "user", "password");
        db.setAdvancedUserData(77,180, TrainingsFocus.STAMINA,2, Gender.MALE, ExperienceLevel.BEGINNER,date,"test@user.de");
        assertEquals(ExperienceLevel.BEGINNER, db.getUserById(id).getExperience());
        assertEquals(Gender.MALE, db.getUserById(id).getGender());
        db.setAdvancedUserData(null,null,null,null,Gender.FEMALE,null,null,"test@user.de");
        assertNull(db.getUserById(id).getExperience());
        assertEquals(Gender.FEMALE, db.getUserById(id).getGender());
        db.setAdvancedUserData(null,null,null,null,Gender.FEMALE,null,null,"felix@gmail.com");
        int felixId = db.getUserByEmail("felix@gmail.com").getId();
        assertEquals(Gender.FEMALE, db.getUserById(felixId).getGender());
    }

    // Exercise

    @Test
    public void getExerciseById() {
        assertTrue(DataBaseService.getInstance().getExerciseById(1).toString().contains("1"));
        assertNull(DataBaseService.getInstance().getExerciseById(345345456));
    }

    @Test
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
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
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
    public void deleteExercise() {
        DataBaseService db = DataBaseService.getInstance();
        Integer id = db.insertExercise("test","test",WeightType.FIXED_WEIGHT,null,null);
        db.deleteExerciseById(id);
        assertNull(db.getExerciseById(id));
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
    public void insertTemplate() {
        DataBaseService db = DataBaseService.getInstance();
        Integer id = db.insertPlanTemplate("Hallo2","muscle","beginner","felix@gmail.com",false,1,5,5);
        assertEquals("Hallo2", db.getOnlyPlanTemplateById(id).getName());
    }

    @Test
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
    public void deleteTemplate() {
        DataBaseService db = DataBaseService.getInstance();
        Integer id = db.insertPlanTemplate("Hallo3","stamina","beginner","felix@gmail.com",false,1,5,5);
        assertEquals("Hallo3", db.getOnlyPlanTemplateById(id).getName());
        db.deletePlanTemplateByID(id);
        assertNull(db.getOnlyPlanTemplateById(id));
    }

    @Test
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
    public void renameTemplate() {
        DataBaseService db = DataBaseService.getInstance();
        Integer id = db.insertPlanTemplate("Hallo3","stamina","beginner","felix@gmail.com",false,1,5,5);
        assertEquals("Hallo3", db.getOnlyPlanTemplateById(id).getName());
        db.renameTemplate("TestTest3",id);
        assertEquals("TestTest3", db.getOnlyPlanTemplateById(id).getName());
        db.deletePlanTemplateByID(id);
    }

    @Test
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
    public void changeTrainingsFocusTemplate() {
        DataBaseService db = DataBaseService.getInstance();
        Integer id = db.insertPlanTemplate("Hallo4","stamina","beginner","felix@gmail.com",false,1,5,5);
        assertEquals("stamina", db.getOnlyPlanTemplateById(id).getTrainingsFocus());
        db.changeTrainingsFocus("muscle",id);
        assertEquals("muscle", db.getOnlyPlanTemplateById(id).getTrainingsFocus());
        db.deletePlanTemplateByID(id);
    }

    /**
     * Tests if the confirmPlan function of the databaseService is working.
     */
    @Test
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
    public void conirmPlanTemplate() {
        DataBaseService db = DataBaseService.getInstance();
        Integer id = db.insertPlanTemplate("Hallo5", "stamina", "beginner",
                "felix@gmail.com", false, 1, 5,5);
        assertEquals(false, db.getOnlyPlanTemplateById(id).isConfirmed());
        db.confirmPlan(id);
        assertEquals(true, db.getOnlyPlanTemplateById(id).isConfirmed());
    }

    // Exercise Instance


    @Test
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
    public void createAndDeleteInstance() {
        DataBaseService db = DataBaseService.getInstance();
        LinkedList<String> tags = new LinkedList<>();
        tags.add("TestTag1");
        tags.add("TestTag2");
        Integer id = db.insertExerciseInstance(1, "A1", 15, tags, 1);
        assertEquals("TestTag1", db.getExercisInstanceById(id).getTags().getFirst());
        db.deleteExerciseInstanceByID(id);
        assertNull(db.getExercisInstanceById(id));
    }

    @Test
    @WithUserDetails(value = "felix@gmail.com", userDetailsServiceBeanName = "userDetailsService")
    public void createAndDeleteSession() {
        DataBaseService db = DataBaseService.getInstance();
        Integer id = db.insertTrainingsSession(1,1,3, new Integer[]{0,0,5}, new Integer[]{15,15,15},"schnell",69);
        assertEquals(69, db.getTrainingsSessionById(id).getPauseInSec());
        db.deleteTrainingsSessionById(id);
        assertNull(db.getTrainingsSessionById(id));
    }
}

package de.ep.team2.core;

import com.sun.javaws.exceptions.InvalidArgumentException;
import de.ep.team2.core.service.DataBaseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CoreApplicationUnitTests {

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
    public void addUser() throws InvalidArgumentException {
        DataBaseService db = DataBaseService.getInstance();
        assertNull(db.getUserByEmail("Hallo@test.com"));
        db.insertUser("Hallo@test.com","Hallo","Test");
        assertTrue(db.getUserByEmail("Hallo@test.com").toString().contains("hallo@test.com"));
        db.deleteUserById(db.getUserByEmail("Hallo@test.com").getId());
    }

    @Test
    public void deleteUserById() throws InvalidArgumentException {
        DataBaseService db = DataBaseService.getInstance();
        assertNotNull(db.getUserById(1));
        db.deleteUserById(1);
        assertNull(db.getUserById(1));
        db.insertUser("timo@gmail123.com", "Timo", "Heinrich");
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
        assertTrue(db.getExercisesByName("HalloTest1234567").isEmpty());
        db.insertExercise("HalloTest1234567","Test Description","Test/Test");
        String test = db.getExercisesByName("HalloTest1234567").toString();
        assertTrue(test.contains("HalloTest1234567") && test.contains("3"));
        db.deleteExerciseById(db.getExercisesByName("HalloTest1234567").get(0).getId());
    }

    @Test
    public void deleteExercise() {
        DataBaseService db = DataBaseService.getInstance();
        assertNotNull(db.getExerciseById(2));
        db.deleteExerciseById(2);
        assertNull(db.getExerciseById(2));
        db.insertExercise("TestEx", "To balance the amount of exercises after delete", null);
    }

    @Test
    public void getAllExercises() {
        assertEquals(2, DataBaseService.getInstance().getAllExercises().size());
    }

    @Test
    public void getExerciseByName() {
        DataBaseService db = DataBaseService.getInstance();
        assertTrue(db.getExercisesByName("fkldsajflkösdjflkösadjf").isEmpty());
        assertNull(db.getExercisesByName(null));
        assertEquals("Bankdrücken", db.getExercisesByName("Bankdrücken").get(0).getName());
    }
}

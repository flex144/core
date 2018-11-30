package de.ep.team2.core;

import de.ep.team2.core.service.DataBaseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CoreApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    //Rest Tests

    @Test
    public void getUserRest() {
        assertTrue(restTemplate.getForObject("/users/1", String.class)
                .contains("Heinrich"));
        assertTrue(restTemplate.getForObject("/users/235", String.class)
                .contains("Benutzer nicht gefunden!"));
        assertTrue(restTemplate.getForObject("/users/alex@gmail.com",
                String.class)
                .contains("Reißig"));
    }

    @Test
    public void deleteUserRest() {
        restTemplate.delete("http://localhost:8080/users/1");
        assertTrue(restTemplate.getForObject("/users/1", String.class)
                .contains("Benutzer nicht gefunden!"));
        HttpEntity<String> response = restTemplate.exchange(
                "http://localhost:8080/users/1", HttpMethod.DELETE, null,
                String.class);
        assertTrue(response.getBody().contains("Benutzer existiert nicht!"));
        HttpEntity<String> response2 = restTemplate.exchange(
                "http://localhost:8080/users/eg93434", HttpMethod.DELETE, null,
                String.class);
        assertTrue(response2.getBody().contains("ID nicht valide!"));
        DataBaseService.getInstance().insertUser("timo@gmail.com", "Timo", "Heinrich");
    }

    @Test
    public void addUserRest() {
        DataBaseService db = DataBaseService.getInstance();
        assertTrue(restTemplate.getForObject("/users/Test@gmail.com",
                String.class)
                .contains("Benutzer nicht gefunden!"));
        // refactor with restTemplate Post
        db.insertUser("Test@gmail.com", null, null);
        assertTrue(restTemplate.getForObject("/users/Test@gmail.com",
                String.class)
                .contains("test@gmail.com"));
        db.deleteUserById(db.getUserByEmail("test@gmail.com").getId());
    }
}

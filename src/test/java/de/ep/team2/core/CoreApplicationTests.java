package de.ep.team2.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CoreApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getUser() {
        assertTrue(restTemplate.getForObject("/users/1", String.class)
                .contains("Heinrich"));
        assertTrue(restTemplate.getForObject("/users/235", String.class)
                .contains("Benutzer nicht gefunden!"));
        assertTrue(restTemplate.getForObject("/users/alex@gmail.com",
                String.class)
                .contains("Reißig"));
    }

    @Test
    public void deleteUser() {
        restTemplate.delete("http://localhost:8080/users/1");
        assertTrue(restTemplate.getForObject("/users/1", String.class)
                .contains("Benutzer nicht gefunden!"));
        HttpEntity<String> response = restTemplate.exchange(
                "http://localhost:8080/users/1", HttpMethod.DELETE, null,
                String.class);
        assertTrue(response.getBody().contains("Benutzer existiert nicht!"));
    }
}

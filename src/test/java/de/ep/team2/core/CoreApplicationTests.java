package de.ep.team2.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CoreApplicationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup (){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser
    @Test
    public void getUser() throws Exception {
        //Get User-profile from existing user with user id
        ResultActions action = mvc.perform(get("/users/1"));
        assertTrue(action.andReturn().getResponse().getContentAsString().contains("Heinrich"));

        //Get User-profile from non-existing user
        action = mvc.perform(get("/users/235"));
        assertTrue(action.andReturn().getResponse().getContentAsString()
                .contains("Benutzer nicht gefunden!"));

        //Get User-profile from existing user with e-mail
        action = mvc.perform(get("/users/alex@gmail.com"));
        assertTrue(action.andReturn().getResponse().getContentAsString().contains("Reißig"));

        /**
        assertTrue(restTemplate.getForObject("/users/1", String.class)
                .contains("Heinrich"));
        assertTrue(restTemplate.getForObject("/users/235", String.class)
                .contains("Benutzer nicht gefunden!"));
        assertTrue(restTemplate.getForObject("/users/alex@gmail.com",
                String.class)
                .contains("Reißig"));
         */
    }

    @WithMockUser
    @Test
    public void deleteUser() throws Exception {
        //Delete User with ID 1
        TestRestTemplate restTemplate = new TestRestTemplate();
        restTemplate.delete("http://localhost:8080/users/1");
        ResultActions action = mvc.perform(get("/users/1"));
        String result = action.andReturn().getResponse().getContentAsString();
        assertTrue(action.andReturn().getResponse().getContentAsString().contains("Benutzer nicht gefunden!"));

        /**
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
         */
    }

    @Test
    public void addUser() {
        /*
        assertTrue(restTemplate.getForObject("/users/Test@gmail.com",
                String.class)
                .contains("Benutzer nicht gefunden!"));
        // refactor with restTemplate Post
        DataBaseService.getInstance().insertUser("Test@gmail.com", null, null, "123");
        assertTrue(restTemplate.getForObject("/users/Test@gmail.com",
                String.class)
                .contains("Test@gmail.com"));
                */
    }
}

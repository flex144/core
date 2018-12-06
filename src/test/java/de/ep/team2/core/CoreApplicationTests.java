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
    }

    /**
     * Gets now tested with Acceptance test.
     */
    @Test
    public void deleteUser() {
    }

    /**
     * Gets now tested with Acceptance test.
     * https://jira.sepws18.padim.fim.uni-passau.de/secure/attachment/10015/acceptance-test-session.txt
     */
    @Test
    public void addUser() {
    }
}

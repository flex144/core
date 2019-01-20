package de.ep.team2.core.Unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ControllerTest {

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

    @Test
    public void getLogin() throws Exception{
        ResultActions action = mvc.perform(get("/"));
        int status = action.andReturn().getResponse().getStatus();
        assertTrue("expected status code = 200; current status code = " + status, status == 200);
    }

    @Test
    public void getUserPageWithoutLogin () throws Exception {
        ResultActions action = mvc.perform(get("/user/home"));
        int status = action.andReturn().getResponse().getStatus();
        assertTrue("expected status code = 302; current status code = " + status, status == 302);
        String redirectURL = action.andReturn().getResponse().getHeader("Location");
        assertTrue("redirect to login page", redirectURL.equals("http://localhost/login"));
    }

    @WithMockUser
    @Test
    public void getUserAccess () throws Exception {
        ResultActions action = mvc.perform(get("/user/home"));
        int status = action.andReturn().getResponse().getStatus();
        assertTrue("expected status = 200, current status = " + status, status == 200);
        action = mvc.perform(get("/mods/home"));
        status = action.andReturn().getResponse().getStatus();
        assertTrue("expected status = 403, current status = " + status, status==403);
    }

}

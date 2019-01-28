package de.ep.team2.core.Unit;

import de.ep.team2.core.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LoginTests {

    @Autowired
    private WebApplicationContext context;

    private UserService userService = new UserService();

    private MockMvc mvc;

    @Before
    public void setup (){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void login() throws Exception {
        //test if existing user can log in
        RequestBuilder requestBuilder = formLogin().user("alex@gmail.com").password("password");
        ResultActions action = mvc.perform(requestBuilder);
        action
            .andDo(print())
            .andExpect(status().is(302));
        String redirectUrl = action.andReturn().getResponse().getHeader("Location");
        assertTrue("redirect to user/home", redirectUrl.equals("user/home"));

        //test if non existing user can log in
        requestBuilder = formLogin().user("test@test.de").password("password");
        action = mvc.perform(requestBuilder);
        action.andDo(print())
                .andExpect(status().is(302));
        redirectUrl = action.andReturn().getResponse().getHeader("Location");
        assertTrue("redirect to /login?error", redirectUrl.equals("/login?error"));
        //test
    }

    /**
     * Test if password gets hashed correctly
     */
    @Test
    public void passwordHashing() {
        String pw = "Test";
        String encryptPw = userService.encode(pw);
        assertTrue(userService.pwMatches(pw, encryptPw));

        String otherPw = "zweiterTest";
        assertFalse(userService.pwMatches(otherPw, encryptPw));
    }
}

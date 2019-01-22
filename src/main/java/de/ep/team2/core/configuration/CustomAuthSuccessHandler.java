package de.ep.team2.core.configuration;

import de.ep.team2.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthSuccessHandler.class);

    /**
     * Handles what should happen upon a successful authentication. Mods get redirected to "/mods/home". Users get
     * redirected to "/user/home".
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);

        boolean mod = false;

        //check whether user has role of mod or user
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if ("ROLE_MOD".equals(auth.getAuthority())) {
                mod = true;
            }
        }

        //redirect accordingly
        if(mod) {
            User loggedInUser = (User) authentication.getPrincipal();
            log.debug("Mod " + loggedInUser.getEmail() + " logged in successfully.");
            response.sendRedirect("mods/home");
        } else {
            User loggedInUser = (User) authentication.getPrincipal();
            log.debug("User " + loggedInUser.getEmail() + " logged in successfully.");
            response.sendRedirect("user/home");
        }
    }
}

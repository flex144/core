package de.ep.team2.core.configuration;

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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);

        boolean mod = false;

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if ("ROLE_MOD".equals(auth.getAuthority())) {
                mod = true;
            }
        }

        if(mod) {
            response.sendRedirect("/mods/home");
        } else {
            response.sendRedirect("/user/home");
        }
    }
}

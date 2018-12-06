package de.ep.team2.core.configuration;

import de.ep.team2.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomAuthSuccessHandler customAuthSuccessHandler;

    @Autowired
    DataSource dataSource;

    @Autowired
    UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("select email, password, enabled from users where email=?")
                .authoritiesByUsernameQuery("select email, role from users where email=?");
    }

    /**
     * Method configures Spring Security. It secures that only authenticated users can access the website, except
     * the paths "/login" and "/registration". "/resources/**" is excluded so that Stylesheets can be loaded.
     * "/user/**" and "/users/**" can be accessed by any authenticated user (User or Mod), whereas "/mods/**" can
     * only be accessed by users with the role of a moderator.
     * @param httpSecurity
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .authorizeRequests()
                    .antMatchers("/mods/**").hasRole("MOD")
                    .antMatchers("/user/**").authenticated()
                    .antMatchers("/users/**").authenticated()
                    .anyRequest().authenticated()
                .and()
                    .authorizeRequests()
                    .antMatchers("/resources/**", "/registration").permitAll()
                    .anyRequest().permitAll()
                .and()
                    .formLogin().successHandler(customAuthSuccessHandler)
                    .loginPage("/login")
                    .permitAll()
                .and()
                    .logout()
                    .permitAll();
    }

}

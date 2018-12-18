package de.ep.team2.core.configuration;

import de.ep.team2.core.entities.User;
import de.ep.team2.core.service.UserService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class UserDetailsImpl implements UserDetailsService {

    private UserService userService = new UserService();

    /**
     * Looks for User in the database via email, since we have no username.
     *
     * @param email Email from the searched user.
     * @return Searched User.
     * @throws UsernameNotFoundException
     */
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(email);
        return user;
     }


}

package de.ep.team2.core.configuration;

import de.ep.team2.core.entities.User;
import de.ep.team2.core.service.UserService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class UserDetailsImpl implements UserDetailsService {

    private UserService userService = new UserService();

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(username);
        return user;
     }


}

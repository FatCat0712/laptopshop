package vn.hoidanit.laptopshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.hoidanit.laptopshop.domain.User;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(username);
        if(user == null) {
            throw new UsernameNotFoundException("Could not find any user with email " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())));
    }


}

package com.fightnet.services;

import com.fightnet.dataAccess.RoleDAO;
import com.fightnet.dataAccess.UserDAO;
import com.fightnet.models.AppUser;
import com.fightnet.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserDAO userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleDAO roleDAO;

    @Override
    public final UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final AppUser appUser = userRepository.findByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(appUser.getUsername(), appUser.getPassword(), appUser.getRoles());
    }

    public final String saveUser(final AppUser user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "Sorry but user with this username already exists";
        }
        user.setRoles(Collections.singleton(roleDAO.findByName("ADMIN")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "successfully";
    }

    public final String deleteUserByUsername(final String username) {
        final AppUser appUser = userRepository.findByUsername(username);
        if (appUser == null) {
            return "User with this username dose not exist";
        }
        userRepository.delete(appUser);
        return "Deleted";
    }

    public Iterable<Role> getAllRoles() {
        return roleDAO.findAll();
    }

    public Iterable<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    public AppUser getUserByUsername(final String username) {
        return userRepository.findByUsername(username);
    }
}

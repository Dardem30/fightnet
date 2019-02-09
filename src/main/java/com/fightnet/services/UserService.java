package com.fightnet.services;

import com.auth0.jwt.JWT;
import com.fightnet.dataAccess.RoleDAO;
import com.fightnet.dataAccess.UserDAO;
import com.fightnet.models.AppUser;
import com.fightnet.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.fightnet.security.SecurityConstants.EXPIRATION_TIME;
import static com.fightnet.security.SecurityConstants.SECRET;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final AuthenticationManager authenticationManager;
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
        user.setRoles(Collections.singleton(roleDAO.findByName("ROLE_USER")));
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

    public String authenticate(final AppUser appUser) {
        final AppUser user = userRepository.findByUsername(appUser.getUsername());
        if (user != null) {
            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getRoles()));
            return JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(HMAC512(SECRET));
        }
        return null;
    }
}

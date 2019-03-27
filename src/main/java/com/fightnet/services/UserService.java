package com.fightnet.services;

import com.auth0.jwt.JWT;
import com.fightnet.controllers.dto.BookedUser;
import com.fightnet.controllers.search.UserSearchCriteria;
import com.fightnet.models.*;
import com.fightnet.security.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.util.*;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.fightnet.security.SecurityConstants.EXPIRATION_TIME;
import static com.fightnet.security.SecurityConstants.SECRET;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private final MongoOperations operations;
    private final ModelMapper mapper;

    @Override
    public final UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final AppUser appUser = findUserByEmail(email);
        if (appUser == null) {
            throw new UsernameNotFoundException(email);
        }
        return new User(appUser.getUsername(), appUser.getPassword(), appUser.getRoles());
    }

    public final String saveUser(final String email, final String code) throws Exception {
        final AppUser appUser = findUserByEmail(email);
        if (appUser != null && code != null && !code.equals("") && code.equals(appUser.getCode())) {
            appUser.setRegistered(true);
            appUser.setCode(null);
            operations.save(appUser);
            return "successfully";
        } else {
            throw new Exception();
        }
    }

    public final String deleteUserByEmail(final String email) {
        final AppUser appUser = findUserByEmail(email);
        if (appUser == null) {
            return "User with this email dose not exist";
        }
        operations.remove(appUser);
        return "Deleted";
    }

    public String authenticate(final AppUser appUser) {
        final AppUser user = findUserByEmail(appUser.getEmail());
        if (user != null && user.isRegistered()) {
            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), user.getRoles()));
            return JWT.create()
                    .withSubject(user.getEmail())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(HMAC512(SECRET));
        }
        return null;
    }

    public String sendCode(final AppUser user) throws Exception {
        if (findUserByEmail(user.getEmail()) != null) {
            throw new Exception();
        }
        user.setCreateTime(new Date());
        user.setRoles(Collections.singleton(operations.findOne(Query.query(new Criteria().and("name").is("ROLE_USER")), Role.class)));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRegistered(false);
        user.setCode(RandomStringUtils.randomAlphanumeric(10));
        operations.save(user);
        emailService.sendCodeMessage(user.getEmail(), "Fightnet регистрация", "Код: " + user.getCode());
        return "successfully";
    }

    public AppUser findUserByEmail(final String email) {
        return operations.findById(email, AppUser.class);
    }

    public void saveVideo(final MultipartFile file, final String email1, String email2) throws Exception {
        final String[] fileParts = file.getOriginalFilename().split("\\.");
        final String videoUrl = "videos/" + UUID.randomUUID().toString() + "." + fileParts[fileParts.length - 1];
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());
             final FileOutputStream outputStream = new FileOutputStream(videoUrl)) {
            IOUtils.copy(inputStream, outputStream);
        }
        final AppUser fighter1 = findUserByEmail(email1);
        ;
        final AppUser fighter2 = findUserByEmail(email2);
        ;
        final Set<Video> fighter1Videos = fighter1.getVideos() == null ? new HashSet<>() : fighter1.getVideos();
        final Set<Video> fighter2Videos = fighter2.getVideos() == null ? new HashSet<>() : fighter2.getVideos();
        final Video video = new Video();
        video.setFighter1(fighter1);
        video.setFighter2(fighter2);
        video.setLoaded(false);
        video.setApproved(false);
        video.setUrl(videoUrl);
        operations.save(video);
        fighter1Videos.add(video);
        fighter2Videos.add(video);
        operations.save(fighter1);
        operations.save(fighter2);
    }

    public List<AppUser> list(final UserSearchCriteria searchCriteria) {
        final Criteria criteria = new Criteria();
        criteria.and("registered").is(true);
        criteria.and("email").ne(searchCriteria.getSearcherEmail());
        if (searchCriteria.getName() != null) {
            criteria.and("name").regex(searchCriteria.getName(), "i");
        }
        if (searchCriteria.getDescription() != null) {
            criteria.and("description").regex(searchCriteria.getDescription(), "i");
        }
        if (searchCriteria.getSurname() != null) {
            criteria.and("surname").regex(searchCriteria.getSurname(), "i");
        }
        if (searchCriteria.getCountry() != null) {
            criteria.and("country.id").is(searchCriteria.getCountry());
        }
        if (searchCriteria.getCity() != null) {
            criteria.and("city.id").is(searchCriteria.getCity());
        }
        return operations.find(Query.query(criteria).limit(10), AppUser.class);
    }

    public void bookPerson(final String currentUserEmail, final String personEmail) {
        BookedPersons persons = new BookedPersons();
        persons.setUser1(currentUserEmail);
        persons.setUser2(personEmail);
        operations.save(persons);
    }

    public void unBookPerson(final String currentUserEmail, final String personEmail) {
        operations.findAndRemove(Query.query(new Criteria().and("user1").is(currentUserEmail).and("user2").is(personEmail)), BookedPersons.class);
    }

    public List<BookedUser> getBookedPersons(final String currentUserEmail) {
        final List<BookedPersons> persons = operations.find(Query.query(new Criteria().and("user1").is(currentUserEmail)), BookedPersons.class);
        final List<BookedUser> result = new ArrayList<>();
        for (BookedPersons person : persons) {
            result.add(mapper.map(findUserByEmail(person.getUser2()), BookedUser.class));
        }
        return result;
    }

    public void createUpdateInvitation(final Invites invite) {
        operations.save(invite);
    }

    public List<Country> findAllCountries() {
        return operations.find(new Query().with(new Sort(Sort.Direction.ASC, "name")), Country.class);
    }
    public List<Invites> getInvitesForUser(final String email) {
        return operations.find(Query.query(new Criteria().and("fighterInvited.$id").is(email).and("accepted").is(false)), Invites.class);
    }


    public List<Invites> getMarkers() {
        final Query query = new Query(new Criteria().and("accepted").is(true));
        query.fields().include("latitude").include("longitude");
        return operations.find(query, Invites.class);
    }
}

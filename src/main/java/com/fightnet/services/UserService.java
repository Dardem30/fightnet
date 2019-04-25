package com.fightnet.services;

import com.auth0.jwt.JWT;
import com.fightnet.FightnetApplication;
import com.fightnet.controllers.dto.BookedUser;
import com.fightnet.controllers.dto.InvitesDTO;
import com.fightnet.controllers.dto.VideoDTO;
import com.fightnet.controllers.search.UserSearchCriteria;
import com.fightnet.models.*;
import com.fightnet.security.mail.EmailService;
import com.restfb.BinaryAttachment;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.FacebookType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.fightnet.security.SecurityConstants.EXPIRATION_TIME;
import static com.fightnet.security.SecurityConstants.SECRET;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final static SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private final MongoOperations operations;
    private final ModelMapper mapper;
    private final SftpService sftpService;

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

    public void saveVideo(final MultipartFile file, final String email1, final String email2, final String style) {
        final AppUser fighter1 = findUserByEmail(email1);
        final AppUser fighter2 = findUserByEmail(email2);
        try (final InputStream inputStream = file.getInputStream()) {
            sftpService.sendVideo((FileInputStream) inputStream, fighter1.getName() + " " + fighter1.getSurname() + " (" + fighter1.getEmail() + ")",
                    fighter2.getName() + " " + fighter2.getSurname() + " (" + fighter2.getEmail() + ") style (" + style + ")");
        } catch (Exception e) {
            log.error("Error during trying to send video on review", e);
        }
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
        if (invite.getId() == null) {
            invite.setId(UUID.randomUUID());
        }
        operations.save(invite);
    }

    public List<Country> findAllCountries() {
        return operations.find(new Query().with(new Sort(Sort.Direction.ASC, "name")), Country.class);
    }

    public List<Invites> getInvitesForUser(final String email) {
        return operations.find(Query.query(new Criteria().and("fighterInvited.$id").is(email).and("accepted").is(false)), Invites.class);
    }


    public List<InvitesDTO> getMarkers() {
        return operations.find(new Query(new Criteria().and("accepted").is(true)), Invites.class).stream().map(invite -> mapper.map(invite, InvitesDTO.class)).collect(Collectors.toList());
    }

    public void acceptInvite(final Invites invite) {
        final Notification notification = new Notification();
        final String text = "User " + invite.getFighterInvited().getName() + " " +
                invite.getFighterInvited().getSurname() + " accept your invitation on date " +
                formatter.format(invite.getDate()) + ". Fight style: " + invite.getFightStyle();
        notification.setEmail(invite.getFighterInviter().getEmail());
        notification.setText(text);
        notification.setLatitude(invite.getLatitude());
        notification.setLongitude(invite.getLongitude());
        operations.save(notification);
        createUpdateInvitation(invite);
    }

    public List<Notification> getNotifications(final String email) {
        return operations.find(Query.query(new Criteria().and("email").is(email)), Notification.class);
    }

    public List<InvitesDTO> getPlannedFights(final String email) {
        final Criteria criteria = new Criteria();
        criteria.and("accepted").is(true);
        criteria.orOperator(Criteria.where("fighterInviter.$id").is(email), Criteria.where("fighterInvited.$id").is(email));
        return operations.find(Query.query(criteria), Invites.class).stream().map(invite -> mapper.map(invite, InvitesDTO.class)).collect(Collectors.toList());
    }

    public void deleteInvitation(final UUID inviteId) {
        operations.remove(new Invites(inviteId));
    }

    private String uploadVideoToFacebook(final MultipartFile file) throws Exception {
        final FacebookClient client = new DefaultFacebookClient(FightnetApplication.facebookToken, Version.VERSION_2_8);

        final FacebookType response = client.publish("me/videos", FacebookType.class,
                BinaryAttachment.with(file.getOriginalFilename(), file.getInputStream()));
        return response.getId();
    }

    public void saveVideoToFacebook(final MultipartFile file) throws Exception {
        log.info("Uploading video to facebook....");
        final String videoId = uploadVideoToFacebook(file);
        if (videoId != null) {
            final Pattern pattern = Pattern.compile("\\(.*?\\)");
            final Matcher matcher = pattern.matcher(file.getOriginalFilename());
            matcher.find();
            final String email1 = matcher.group().replace("(", "").replace(")", "");
            matcher.find();
            final String email2 = matcher.group().replace("(", "").replace(")", "");
            matcher.find();
            final String style = matcher.group().replace("(", "").replace(")", "");
            final Video video = new Video();
            video.setUrl("https://www.facebook.com/2105724756385939/videos/" + videoId);
            video.setStyle(style);
            video.setFighter1(operations.findById(email1, AppUser.class));
            video.setFighter2(operations.findById(email2, AppUser.class));
            video.setVoteStarts(new Date());
            operations.save(video);
            log.info("video was successfully uploaded to facebook");
        } else {
            log.error("Error during trying to upload video");
        }
    }

    public List<VideoDTO> getVideos() {
        return operations.findAll(Video.class).stream().map(video -> mapper.map(video, VideoDTO.class)).collect(Collectors.toList());
    }

    public void vote(final Video video) {
        operations.save(video);
    }
}

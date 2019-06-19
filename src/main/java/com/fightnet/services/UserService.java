package com.fightnet.services;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fightnet.FightnetApplication;
import com.fightnet.controllers.dto.BookedUser;
import com.fightnet.controllers.dto.InvitesDTO;
import com.fightnet.controllers.dto.VideoDTO;
import com.fightnet.controllers.search.MapSearchCriteria;
import com.fightnet.controllers.search.SearchResponse;
import com.fightnet.controllers.search.UserSearchCriteria;
import com.fightnet.controllers.search.VideoSearchCriteria;
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

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.fightnet.security.SecurityConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final static SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private final MongoOperations operations;
    private final ModelMapper mapper;
    private final GoogleDriveService googleDriveService;

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

    public void saveVideo(final MultipartFile file, final String email1, final String email2, final String style) throws Exception {
        final AppUser fighter1 = findUserByEmail(email1);
        final AppUser fighter2 = findUserByEmail(email2);
        final String[] extension = file.getOriginalFilename().split("\\.");
        googleDriveService.sendFile(file, fighter1.getName() + " " + fighter1.getSurname() + " (" + fighter1.getEmail() + ")" + " " +
                fighter2.getName() + " " + fighter2.getSurname() + " (" + fighter2.getEmail() + ") style (" + style + ")" + "." + extension[extension.length - 1]);
    }

    public void savePhoto(final MultipartFile file, final String email) throws Exception {
        final String[] extension = file.getOriginalFilename().split("\\.");
        googleDriveService.sendFile(file, "(" + email + ")" + " " + RandomStringUtils.randomAlphanumeric(4) + "." + extension[extension.length - 1]);
    }

    public SearchResponse<AppUser> list(final UserSearchCriteria searchCriteria) {
        final Criteria criteria = new Criteria();
        final SearchResponse<AppUser> response = new SearchResponse<>();
        criteria.and("registered").is(true);
        criteria.and("email").ne(searchCriteria.getSearcherEmail());
        if (searchCriteria.getName() != null) {
            criteria.orOperator(Criteria.where("name").regex(searchCriteria.getName(), "i"), Criteria.where("surname").regex(searchCriteria.getName(), "i"));
        }
        if (searchCriteria.getDescription() != null) {
            criteria.and("description").regex(searchCriteria.getDescription(), "i");
        }
        if (searchCriteria.getCountry() != null) {
            criteria.and("country").is(searchCriteria.getCountry());
        }
        if (searchCriteria.getCity() != null) {
            criteria.and("city").is(searchCriteria.getCity());
        }
        if (searchCriteria.getHeight() != null) {
            criteria.and("growth").is(searchCriteria.getHeight());
        }
        if (searchCriteria.getWidth() != null) {
            criteria.and("weight").is(searchCriteria.getWidth());
        }
        if (searchCriteria.getPreferredKind() != null) {
            criteria.and("preferredKind").is(searchCriteria.getPreferredKind());
        }
        final Query query = new Query(criteria);
        query.fields().include("email").include("name").include("surname").include("city").include("country").include("description").include("mainPhoto");
        response.setCount(operations.count(query, AppUser.class));
        response.setRecords(operations.find(query.skip(pageSize * (searchCriteria.getPageNum() - 1)).limit(pageSize), AppUser.class));
        return response;
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

    private void createUpdateInvitation(final Invites invite) {
        if (invite.getId() == null) {
            invite.setId(UUID.randomUUID());
        }
        operations.save(invite);
    }

    public void invite(final Invites invite) {
        final AppUser user = findUserByEmail(invite.getFighterInvited().getEmail());
        user.setNotifications(user.getNotifications() == null ? 1 : user.getNotifications() + 1);
        operations.save(user);
        final Notification notification = new Notification();
        notification.setEmail(user.getEmail());
        notification.setText(invite.getFighterInviter().getName() + " " +
                invite.getFighterInviter().getSurname() + " invites you on date " +
                formatter.format(invite.getDate()) + ". Fight style: " + invite.getFightStyle());
        notification.setLongitude(invite.getLongitude());
        notification.setLatitude(invite.getLatitude());
        notification.setReaded(false);
        notification.setCreateTime(new Date());
        operations.save(notification);
        createUpdateInvitation(invite);
    }

    public List<Country> findAllCountries() {
        return operations.find(new Query().with(new Sort(Sort.Direction.ASC, "name")), Country.class);
    }

    public SearchResponse<InvitesDTO> getInvitesForUser(final String email, final int page) {
        final SearchResponse<InvitesDTO> response = new SearchResponse<>();
        final Query query = Query.query(new Criteria().and("fighterInvited._id").is(email).and("accepted").is(false));
        response.setCount(operations.count(query, Invites.class));
        response.setRecords(operations.find(query.skip(pageSize * (page - 1)).limit(pageSize), Invites.class).stream().map(invite -> mapper.map(invite, InvitesDTO.class)).collect(Collectors.toList()));
        return response;
    }


    public List<InvitesDTO> getMarkers(final MapSearchCriteria searchCriteria) {
        final Criteria criteria = new Criteria();
        criteria.and("accepted").is(true);
        if (searchCriteria.getName() != null) {
            criteria.orOperator(Criteria.where("fighterInviter.name").regex(searchCriteria.getName(), "i"),
                    Criteria.where("fighterInvited.name").regex(searchCriteria.getName(), "i"),
                    Criteria.where("fighterInviter.surname").regex(searchCriteria.getName(), "i"),
                    Criteria.where("fighterInvited.surname").regex(searchCriteria.getName(), "i"));
        }
        if (searchCriteria.getFightStyle() != null) {
            criteria.and("fightStyle").is(searchCriteria.getFightStyle());
        }
        if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            criteria.and("date").gte(searchCriteria.getStartDate()).lte(searchCriteria.getEndDate());
        } else if (searchCriteria.getStartDate() != null) {
            criteria.and("date").gte(searchCriteria.getStartDate());
        } else if (searchCriteria.getEndDate() != null) {
            criteria.and("date").lte(searchCriteria.getStartDate());
        }
        return operations.find(new Query(criteria), Invites.class).stream().map(invite -> mapper.map(invite, InvitesDTO.class)).collect(Collectors.toList());
    }

    public void acceptInvite(final Invites invite) {
        final Notification notification = new Notification();
        notification.setEmail(invite.getFighterInviter().getEmail());
        notification.setText(invite.getFighterInvited().getName() + " " +
                invite.getFighterInvited().getSurname() + " accept your invitation on date " +
                formatter.format(invite.getDate()) + ". Fight style: " + invite.getFightStyle());
        notification.setLongitude(invite.getLongitude());
        notification.setLatitude(invite.getLatitude());
        notification.setReaded(false);
        operations.save(notification);
        createUpdateInvitation(invite);
    }

    public List<Notification> getNotifications(final String email) {
        return operations.find(Query.query(new Criteria().and("email").is(email)), Notification.class);
    }

    public List<InvitesDTO> getPlannedFights(final String email) {
        final Criteria criteria = new Criteria();
        criteria.and("accepted").is(true);
        criteria.orOperator(Criteria.where("fighterInviter._id").is(email), Criteria.where("fighterInvited._id").is(email));
        return operations.find(Query.query(criteria), Invites.class).stream().map(invite -> mapper.map(invite, InvitesDTO.class)).collect(Collectors.toList());
    }

    public void deleteInvitation(final UUID inviteId) {
        operations.remove(new Invites(inviteId));
    }

    private String uploadToFacebook(final MultipartFile file, final String uploadPath) throws Exception {
        final FacebookClient client = new DefaultFacebookClient(FightnetApplication.facebookToken, Version.VERSION_2_8);

        final FacebookType response = client.publish(uploadPath, FacebookType.class,
                BinaryAttachment.with(file.getOriginalFilename(), file.getInputStream()));
        return response.getId();
    }

    public void saveVideoToFacebook(final MultipartFile file) throws Exception {
        log.info("Uploading video to facebook....");
        final String videoId = uploadToFacebook(file, "me/videos");
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

    public void savePhotoToFacebook(final MultipartFile file) throws Exception {
        log.info("Uploading photo to facebook....");
        final String photoId = uploadToFacebook(file, "me/photos");
        if (photoId != null) {
            final String photoUrl = "https://www.facebook.com/2105724756385939/photos/" + photoId;
            final Pattern pattern = Pattern.compile("\\(.*?\\)");
            final Matcher matcher = pattern.matcher(file.getOriginalFilename());
            matcher.find();
            final AppUser user = operations.findById(matcher.group().replace("(", "").replace(")", ""), AppUser.class);
            final Set<String> photos = user.getPhotos() == null ? new HashSet<>() : user.getPhotos();
            if (user.getMainPhoto() == null) {
                user.setMainPhoto(photoUrl);
            }
            user.setPhotos(photos);
            photos.add(photoUrl);
            operations.save(user);
            log.info("Successfully uploaded photo to facebook");
        } else {
            log.error("Error during trying to upload photo");
        }
    }

    public SearchResponse<VideoDTO> getVideos(final VideoSearchCriteria searchCriteria) {
        final Criteria criteria = new Criteria();
        final SearchResponse<VideoDTO> response = new SearchResponse<>();
        if (searchCriteria.getStyle() != null) {
            criteria.and("style").is(searchCriteria.getStyle());
        }
        if (searchCriteria.getName() != null) {
            criteria.orOperator(Criteria.where("fighter1.name").regex(searchCriteria.getName(), "i"),
                    Criteria.where("fighter2.name").regex(searchCriteria.getName(), "i"),
                    Criteria.where("fighter1.surname").regex(searchCriteria.getName(), "i"),
                    Criteria.where("fighter2.surname").regex(searchCriteria.getName(), "i"));
        }
        final Query query = new Query(criteria);
        response.setCount(operations.count(query, Video.class));
        response.setRecords(operations.find(query.skip(pageSize * (searchCriteria.getPageNum() - 1)).limit(pageSize), Video.class)
                .stream().map(video -> mapper.map(video, VideoDTO.class)).collect(Collectors.toList()));
        return response;
    }

    public void vote(final Video video) {
        operations.save(video);
    }

    public void resetNotifications(final String email) {
        final AppUser user = findUserByEmail(email);
        user.setNotifications(0);
        for (final Notification notification: operations.find(Query.query(new Criteria().and("email").is(email).and("readed").is(false)), Notification.class)) {
            notification.setReaded(true);
            operations.save(notification);
        }
        operations.save(user);
    }

    public void resetMessages(final String email) {
        final AppUser user = findUserByEmail(email);
        user.setUnreadedMessages(0);
        operations.save(user);
    }

    public List<City> getCities(final String country) {
        return operations.find(Query.query(new Criteria().and("country").is(country)), City.class);
    }

    public Map<String, String> getCommentsPhotos(final JsonNode jsonEmails) {
        final List<String> emails = new ArrayList<>();
        for (final JsonNode email : jsonEmails) {
            emails.add(email.asText());
        }
        final Query query = new Query(new Criteria().and("email").in(emails));
        query.fields().include("mainPhoto").include("email");
        return operations.find(query, AppUser.class).stream().collect(Collectors.toMap(AppUser::getEmail, AppUser::getMainPhoto));
    }

    public void updateChangableInfoToUser(final AppUser user) {
        final AppUser rootUser = operations.findById(user.getEmail(), AppUser.class);
        rootUser.setCity(user.getCity());
        rootUser.setCountry(user.getCountry());
        rootUser.setWeight(user.getWeight());
        rootUser.setGrowth(user.getGrowth());
        rootUser.setDescription(user.getDescription());
        rootUser.setPreferredKind(user.getPreferredKind());
        operations.save(rootUser);
    }
}

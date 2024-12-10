package service;

import domain.factory.UserFactory;
import domain.model.user.*;
import dto.RegistrationDTO;
import dto.SessionDTO;
import dto.UserDTO;
import exception.UserException;
import repository.UserRepository;
import util.CollectionUtils;
import util.LoggerUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // CREATE METHODS
    public void registerUser(RegistrationDTO validatedDTO) {
        if (validatedDTO == null) {
            throw new IllegalArgumentException("RegistrationDTO cannot be null.");
        }

        // creating user instance
        User user = UserFactory.createUser(validatedDTO);

        // attempting to save validated user to file storage with retries if necessary
        save(user, "An unexpected error occurred when saving your data. Please try again later.");

        LoggerUtil.getInstance().logInfo(String.format("User with email '%s' has successfully been registered.", validatedDTO.getEmail()));
    }

    public void assignConferenceToOrganizer(String organizerId, String conferenceId) {
        if (organizerId == null || conferenceId == null) {
            throw new IllegalArgumentException("Organizer and conference id parameters cannot be null.");
        }

        // retrieving user
        User user = findById(organizerId);

        // validating user is an organizer
        validateRole(user, UserRole.ORGANIZER);

        // updating organizer's data
        Organizer organizer = (Organizer) user;
        organizer.addConference(conferenceId);

        save(organizer, "An unexpected error occurred when saving conference to your managed conferences. Please try again later.");
    }

    public void addConferenceToAttendee(String id, String conferenceId) {
        if (id == null || conferenceId == null || id.isEmpty() || conferenceId.isEmpty()) {
            throw new IllegalArgumentException("Attendee id and conference id cannot be null or empty.");
        }

        User user = findById(id);

        validateRole(user, UserRole.ATTENDEE);

        // registering attendee to conference
        Attendee attendee = (Attendee) user;
        attendee.addRegisteredConference(conferenceId);

        save(attendee, "An unexpected error occurred when registering attendee to conference. Please try again later.");

        LoggerUtil.getInstance().logInfo(String.format("Successfully added conference with id '%s' to attendee '%s' registered conferences.", conferenceId, attendee.getName()));
    }

    public void addSessionToAttendee(String id, SessionDTO sessionDTO) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Invalid attendee id.");
        }

        if (sessionDTO == null) {
            throw new IllegalArgumentException("Invalid session data.");
        }

        User user = findById(id);

        validateRole(user, UserRole.ATTENDEE);

        // add session to attendee's personal schedule
        Attendee attendee = (Attendee) user;
        attendee.addSession(sessionDTO.getId(), LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime()));

        // save updated attendee data to storage
        save(attendee, "An unexpected error occurred when registering session to attendee's schedule. Please try again later.");

        LoggerUtil.getInstance().logInfo(String.format("Successfully added session '%s' to attendee '%s' schedule.", sessionDTO.getName(), attendee.getName()));
    }

    public void assignNewSessionForSpeaker(SessionDTO sessionDTO) {
        if (sessionDTO == null) {
            throw new IllegalArgumentException("SessionDTO cannot be null.");
        }

        // retrieving user
        User user = findById(sessionDTO.getSpeakerId());

        // validating user is a speaker
        validateRole(user, UserRole.SPEAKER);

        // update speaker's data by assigning new session
        LocalDateTime sessionStart = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime());
        LocalDateTime sessionEnd = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime());
        Speaker speaker = (Speaker) user;
        speaker.assignSession(sessionDTO.getId(), sessionStart, sessionEnd);

        save(speaker, "An unexpected error occurred when assigning session to speaker's schedule. Please try again later.");
    }

    public void addFeedbackToSpeaker(String speakerId, String feedbackId) {
        if (speakerId == null || feedbackId == null || speakerId.isEmpty() || feedbackId.isEmpty()) {
            throw new IllegalArgumentException("Invalid speaker id and/or feedback id.");
        }

        User user = findById(speakerId);

        validateRole(user, UserRole.SPEAKER);

        Speaker speaker = (Speaker) user;
        speaker.addFeedback(feedbackId);

        save(speaker, "An unexpected error occurred when saving feedback to speaker data. Please try again later.");
    }

    // READ METHODS
    public List<UserDTO> findAllById(Set<String> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("The set of ids cannot be null.");
        }

        // batch fetch all users matching the set of ids
        List<Optional<User>> userOptionals = userRepository.findAllById(ids);

        // extract all valid users
        List<User> users = CollectionUtils.extractValidEntities(userOptionals);

        return users.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public String getNameById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }

        return userRepository
                .findById(id)
                .map(User::getName)
                .orElseThrow(() -> new UserException(String.format("User with id '%s' does not exist.", id)));
    }

    public UserDTO getBydId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("User id cannot be null or empty.");
        }
        return userRepository
                .findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new UserException(String.format("User with id '%s' does not exist.", id)));
    }

    public UserDTO getByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty.");
        }
        return userRepository
                .findByEmail(email)
                .map(this::mapToDTO)
                .orElseThrow(() -> new UserException(String.format("User with email '%s' could not be found.", email)));
    }

    public UserDTO getAuthenticatedByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        return userRepository
                .findByEmail(email)
                .map(this::mapToAuthenticatedDTO)
                .orElseThrow(() -> new UserException(String.format("User with email '%s' could not be found.", email)));
    }

    public List<UserDTO> findAllSpeakers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == UserRole.SPEAKER)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> findAllAttendees() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == UserRole.ATTENDEE)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Set<String> findManagedConferencesForOrganizer(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }

        Optional<User> organizerOptional = userRepository.findByEmail(email);
        if (organizerOptional.isEmpty()) {
            LoggerUtil.getInstance().logError(String.format("Failed to find managed conferences: User with email '%s' does not exist.", email));
            throw new UserException((String.format("User with email '%s' does not exist.", email)));
        }

        User user = organizerOptional.get();
        validateRole(user, UserRole.ORGANIZER);

        Organizer organizer = (Organizer) user;
        return organizer.getManagedConferences();
    }

    public Map<String, String> findSpeakerBiosById(Set<String> speakerIds) {
        if (speakerIds == null) {
            throw new IllegalArgumentException("Invalid speaker ids.");
        }

        Map<String, String> speakerIdToBio = new HashMap<>();
        speakerIds.stream()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .forEach(userOptional -> {
                    User user = userOptional.get();
                    if (user instanceof Speaker) speakerIdToBio.put(user.getId(), ((Speaker) user).getBio());
                });

        return speakerIdToBio;
    }

    public boolean isEmailRegistered(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }

        return userRepository
                .findByEmail(email)
                .isPresent();
    }

    public Set<String> findRegisteredConferencesForAttendee(String attendeeId) {
        if (attendeeId == null || attendeeId.isEmpty()) {
            throw new IllegalArgumentException("Attendee Id cannot be null or empty.");
        }

        User user = findById(attendeeId);

        validateRole(user, UserRole.ATTENDEE);

        Attendee attendee = (Attendee) user;
        return attendee.getRegisteredConferences();
    }

    public Set<String> findAssignedSessionsForSpeaker(String speakerId) {
        if (speakerId == null || speakerId.isEmpty()) {
            throw new IllegalArgumentException("Invalid speaker id.");
        }


        User user = findById(speakerId);

        validateRole(user, UserRole.SPEAKER);

        Speaker speaker = (Speaker) user;
        return speaker.getAssignedSessionIds();
    }

    public void updateSpeakerBio(String speakerId, String newBio) {
        if (speakerId == null || newBio == null || speakerId.isEmpty() || newBio.isEmpty()) {
            throw new IllegalArgumentException("Invalid speaker id.");
        }


        User user = findById(speakerId);

        validateRole(user, UserRole.SPEAKER);

        Speaker speaker = (Speaker) user;
        speaker.setBio(newBio);

        save(speaker, "An unexpected error occurred when saving your new bio. Please try again later.");
    }

    public void unassignConferenceFromOrganizer(String id, String conferenceId) {
        if (id == null || id.isEmpty() || conferenceId == null || conferenceId.isEmpty()) {
            throw new IllegalArgumentException("Organizer id and conference id are required to remove conference from organizer's managed conferences and cannot be null or empty.");
        }

        User user = findById(id);

        validateRole(user, UserRole.ORGANIZER);

        // remove conference from organizer
        Organizer organizer = (Organizer) user;
        organizer.removeConference(conferenceId);

        // save updated organizer data
        save(organizer, "An unexpected error occurred when unassigning conference from organizer's managed conferences. Please try again later.");

        LoggerUtil.getInstance().logInfo(String.format("Successfully removed conference with id '%s' from organizer '%s' managed conferences.", conferenceId, organizer.getName()));
    }

    public void removeConferenceFromAttendee(String id, String conferenceId) {
        if (id == null || conferenceId == null || id.isEmpty() || conferenceId.isEmpty()) {
            throw new IllegalArgumentException("Attendee id and conference id cannot be null or empty.");
        }

        User user = findById(id);

        validateRole(user, UserRole.ATTENDEE);

        // removing conference from attendee
        Attendee attendee = (Attendee) user;
        attendee.removeRegisteredConference(conferenceId);

        // saving updated attendee data
        save(attendee, "An unexpected error occurred when removing conference from the attendee's registered conferences. Please try again later.");

        LoggerUtil.getInstance().logInfo(String.format("Successfully removed conference '%s' from attendee '%s' registered conferences.", conferenceId, attendee.getName()));
    }

    public void removeSessionFromAttendee(String id, String sessionId) {
        if (id == null || id.isEmpty() || sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Invalid attendee id and/or session id.");
        }

        User user = findById(id);

        validateRole(user, UserRole.ATTENDEE);

        // removing session from attendee's schedule
        Attendee attendee = (Attendee) user;
        attendee.removeSession(sessionId);

        // saving updated attendee data
        save(attendee, "An unexpected error occurred when removing session from attendee's schedule. Please try again later.");

        LoggerUtil.getInstance().logInfo(String.format("Successfully unregistered attendee '%s' from session with id '%s'.", id, sessionId));
    }

    public void unassignSessionFromSpeaker(String id, String sessionId) {
        if (id == null || id.isEmpty() || sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Speaker id and session id are required to remove session from speaker's schedule and cannot be null or empty.");
        }

        // find user
        User user = findById(id);

        // validate user's role
        validateRole(user, UserRole.SPEAKER);

        // removing session from speaker's schedule
        Speaker speaker = (Speaker) user;
        speaker.unassignSession(sessionId);

        // saving updated speaker
        save(speaker, "An unexpected error occurred when unassigning session from speaker's schedule. Please try again later.");

        LoggerUtil.getInstance().logInfo(String.format("Successfully unassigned session with id '%s' from speaker '%s'.", sessionId, speaker.getName()));
    }



    // helpers
    private void save(User user, String errorMessage) {
        boolean isSaved = userRepository.save(user, user.getId());
        if (!isSaved) {
            LoggerUtil.getInstance().logError(String.format("Saving failure: User with id '%s' could not be saved.", user.getId()));
            throw new UserException(errorMessage);
        }
    }

    private User findById(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserException(String.format("User with id '%s' does not exist.", id));
        }
        return userOptional.get();
    }

    private void validateRole(User user, UserRole requiredRole) {
        if (user.getRole() != requiredRole) {
            throw new UserException(String.format("User with id '%s' does not have %s permissions.", user.getId(), requiredRole.getDisplayName()));
        }
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.withoutPassword(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getRole()
        );
    }

    private UserDTO mapToAuthenticatedDTO(User user) {
        return UserDTO.withPassword(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getHashedPassword(),
            user.getRole()
        );
    }

}

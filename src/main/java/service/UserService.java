package service;

import domain.factory.UserFactory;
import domain.model.*;
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
        boolean isSavedToFile = userRepository.save(user, user.getId());
        if (!isSavedToFile) {
            LoggerUtil.getInstance().logError("User registration failed. Could not save user to file storage.");
            throw new UserException("An unexpected error has occurred while saving your data. Please try again later.");
        }
        LoggerUtil.getInstance().logInfo(String.format("User with email '%s' has successfully been registered.", validatedDTO.getEmail()));
    }

    public void assignConferenceToOrganizer(String organizerId, String conferenceId) {
        if (organizerId == null || conferenceId == null) {
            throw new IllegalArgumentException("Organizer and conference id parameters cannot be null.");
        }

        // retrieving user
        Optional<User> userOptional = userRepository.findById(organizerId);
        if (userOptional.isEmpty()) {
            LoggerUtil.getInstance().logWarning(String.format("Id provided '%s' does not belong to any registered user.", organizerId));
            throw new UserException(String.format("User with id '%s' does not exist.", organizerId));
        }

        // validating user is an organizer
        User user = userOptional.get();
        if (user.getRole() != UserRole.ORGANIZER) {
            LoggerUtil.getInstance().logWarning(String.format("Id provided belongs to a user with the role of '%s'. Required role: Organizer.", user.getRole().getDisplayName()));
            throw new UserException(String.format("User with id '%s' does not have organizer permissions.", organizerId));
        }

        // updating organizer's data
        Organizer organizer = (Organizer) user;
        organizer.addConference(conferenceId);

        boolean isUserUpdated = userRepository.save(organizer, organizerId);
        if (!isUserUpdated) {
            throw new UserException("An unexpected error occurred when saving conference to your managed conferences. Please try again later.");
        }
    }

    public void addConferenceToAttendee(String id, String conferenceId) {
        if (id == null || conferenceId == null || id.isEmpty() || conferenceId.isEmpty()) {
            throw new IllegalArgumentException("Attendee id and conference id cannot be null or empty.");
        }

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserException(String.format("User with id '%s' does not exist.", id));
        }

        User user = userOptional.get();
        if (user.getRole() != UserRole.ATTENDEE) {
            throw new UserException(String.format("User with id '%s' does not have attendee permissions.", id));
        }

        Attendee attendee = (Attendee) user;
        attendee.addRegisteredConference(conferenceId);

        boolean isUpdated = userRepository.save(attendee, attendee.getId());
        if (!isUpdated) {
            throw new UserException("An unexpected error occurred when saving updated attendee data. Please try again later.");
        }

        LoggerUtil.getInstance().logInfo(String.format("Successfully added conference with id '%s' to attendee '%s' registered conferences.", conferenceId, attendee.getName()));
    }

    public void addSessionToAttendee(String id, String sessionId, LocalDateTime sessionStartTime) {
        if (id == null || sessionId == null || id.isEmpty() || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Invalid attendee id and/or session id.");
        }

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserException(String.format("User with id '%s' does not exist.", id));
        }

        User user = userOptional.get();
        if (user.getRole() != UserRole.ATTENDEE) {
            throw new UserException(String.format("User with id '%s' does not have attendee permissions.", id));
        }


        Attendee attendee = (Attendee) user;
        attendee.addSession(sessionId, sessionStartTime);

        save(attendee);

        LoggerUtil.getInstance().logInfo(String.format("Successfully added session '%s' to attendee '%s' schedule.", sessionId, id));
    }

    public void assignNewSessionForSpeaker(SessionDTO sessionDTO) {
        if (sessionDTO == null) {
            throw new IllegalArgumentException("SessionDTO cannot be null.");
        }

        String speakerId = sessionDTO.getSpeakerId();

        // retrieving user
        Optional<User> userOptional = userRepository.findById(speakerId);
        if (userOptional.isEmpty()) {
            LoggerUtil.getInstance().logError(String.format("Id provided '%s' does not belong to any registered user.", speakerId));
            throw new UserException(String.format("User with id '%s' does not exist.", speakerId));
        }

        // validating user is a speaker
        User user = userOptional.get();
        if (user.getRole() != UserRole.SPEAKER) {
            LoggerUtil.getInstance().logError(String.format("Id provided belongs to a user with the role of '%s'.  Required role: Speaker.", user.getRole().getDisplayName()));
            throw new UserException(String.format("User with id '%s' does not have speaker permissions.", speakerId));
        }

        // update speaker's data by assigning new session
        LocalDateTime sessionStart = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime());
        LocalDateTime sessionEnd = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime());
        Speaker speaker = (Speaker) user;
        speaker.assignSession(sessionDTO.getId(), sessionStart, sessionEnd);

        // saving updated speaker user
        boolean isUserUpdated = userRepository.save(speaker, speakerId);
        if (!isUserUpdated) {
            throw new UserException("Failed to save session to speaker's assigned sessions. Please try again later.");
        }
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
        if (user.getRole() != UserRole.ORGANIZER) {
            LoggerUtil.getInstance().logWarning(String.format("Failed to find managed conferences for organizer: Email provided belongs to a user with the role of '%s'. Required role: Organizer.", user.getRole().getDisplayName()));
            throw new UserException(String.format("User with email '%s' does not have organizer permissions.", email));
        }

        Organizer organizer = (Organizer) user;
        return organizer.getManagedConferences();
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

        Optional<User> userOptional = userRepository.findById(attendeeId);
        if (userOptional.isEmpty()) {
            throw new UserException(String.format("User with id '%s' does not exist.", attendeeId));
        }

        User user = userOptional.get();
        if (user.getRole() != UserRole.ATTENDEE) {
            throw new UserException(String.format("User with id '%s' does not have attendee permissions.", attendeeId));
        }

        Attendee attendee = (Attendee) user;
        return attendee.getRegisteredConferences();
    }

    public void unassignConferenceFromOrgaizer(String id, String conferenceId) {
        if (id == null || id.isEmpty() || conferenceId == null || conferenceId.isEmpty()) {
            throw new IllegalArgumentException("Organizer id and conference id are required to remove conference from organizer's managed conferences and cannot be null or empty.");
        }

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserException(String.format("User with id '%s' does not exist.", id));
        }

        User user = userOptional.get();
        if (user.getRole() != UserRole.SPEAKER) {
            throw new UserException(String.format("User with id '%s' does not have speaker permissions.", id));
        }

        Organizer organizer = (Organizer) user;
        organizer.removeConference(conferenceId);

        boolean isOrganizerUpdated = userRepository.save(organizer, organizer.getId());
        if (!isOrganizerUpdated) {
            throw new UserException("An unexpected error occurred when unassigning conference from organizer's managed conferences. Please try again later.");
        }

        LoggerUtil.getInstance().logInfo(String.format("Successfully removed conference with id '%s' from organizer '%s' managed conferences.", conferenceId, organizer.getName()));
    }

    public void removeConferenceFromAttendee(String id, String conferenceId) {
        if (id == null || conferenceId == null || id.isEmpty() || conferenceId.isEmpty()) {
            throw new IllegalArgumentException("Attendee id and conference id cannot be null or empty.");
        }

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserException(String.format("User with id '%s' does not exist.", id));
        }

        User user = userOptional.get();
        if (user.getRole() != UserRole.ATTENDEE) {
            throw new UserException(String.format("User with id '%s' does not have attendee permissions.", id));
        }

        Attendee attendee = (Attendee) user;
        attendee.removeRegisteredConference(conferenceId);

        boolean isAttendeeUpdated = userRepository.save(attendee, attendee.getId());
        if (!isAttendeeUpdated) {
            throw new UserException("An unexpected error occurred when saving attendee data after removing registered conference. Please try again later.");
        }

        LoggerUtil.getInstance().logInfo(String.format("Successfully removed conference '%s' from attendee '%s' registered conferences.", conferenceId, attendee.getName()));
    }

    public void unassignSessionFromSpeaker(String id, String sessionId) {
        if (id == null || id.isEmpty() || sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Speaker id and session id are required to remove session from speaker's schedule and cannot be null or empty.");
        }

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserException(String.format("User with id '%s' does not exist.", id));
        }

        User user = userOptional.get();
        if (user.getRole() != UserRole.SPEAKER) {
            throw new UserException(String.format("User with id '%s' does not have speaker permissions.", id));
        }

        Speaker speaker = (Speaker) user;
        speaker.unassignSession(sessionId);

        boolean isSpeakerUpdated = userRepository.save(speaker, speaker.getId());
        if (!isSpeakerUpdated) {
            throw new UserException("An unexpected error occurred when unassigning session from speaker's schedule. Please try again later.");
        }

        LoggerUtil.getInstance().logInfo(String.format("Successfully unassigned session with id '%s' from speaker '%s'.", sessionId, speaker.getName()));
    }

    private void save(User user) {
        boolean isSaved = userRepository.save(user, user.getId());
        if (!isSaved) {
            throw new UserException("An unexpected error occurred when saving data. Please try again later.");
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

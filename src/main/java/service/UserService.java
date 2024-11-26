package service;

import domain.factory.UserFactory;
import domain.model.Organizer;
import domain.model.Speaker;
import domain.model.User;
import domain.model.UserRole;
import dto.RegistrationDTO;
import dto.UserDTO;
import exception.InvalidUserRoleException;
import exception.SavingDataException;
import exception.UserNotFoundException;
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
            LoggerUtil.getInstance().logError("Registration DTO cannot be null.");
            return;
        }

        // creating user instance
        User user = UserFactory.createUser(validatedDTO);

        // attempting to save validated user to file storage with retries if necessary
        boolean isSavedToFile = userRepository.save(user, user.getId());
        if (!isSavedToFile) {
            LoggerUtil.getInstance().logError("User registration failed. Could not save user to file storage.");
            throw new SavingDataException("An unexpected error occurred while saving data for user with email '" + validatedDTO.getEmail() + "'.");
        }
        LoggerUtil.getInstance().logInfo("User with email '" + validatedDTO.getEmail() + "' has successfully been registered.");
    }

    public void addNewManagedConferenceForOrganizer(String organizerId, String conferenceId) {
        if (organizerId == null || conferenceId == null) {
            LoggerUtil.getInstance().logError("Organizer id and conference id parameters cannot be null.");
            return;
        }

        // retrieving user
        Optional<User> userOptional = userRepository.findById(organizerId);
        if (userOptional.isEmpty()) {
            LoggerUtil.getInstance().logWarning("Id provided '" + organizerId + "' does not belong to any registered user.");
            throw new UserNotFoundException("User with id '" + organizerId + "' does not exist.");
        }

        // validating user is an organizer
        User user = userOptional.get();
        if (user.getRole() != UserRole.ORGANIZER) {
            LoggerUtil.getInstance().logWarning("Id provided belongs to a user with the role of " + user.getRole().getDisplayName() + ". Required role: Organizer.");
            throw new InvalidUserRoleException("User with id '" + organizerId + "' does not have organizer permissions");
        }

        // updating organizer's data
        Organizer organizer = (Organizer) user;
        organizer.addConference(conferenceId);

        boolean isUserUpdated = userRepository.save(organizer, organizerId);
        if (!isUserUpdated) {
            throw new SavingDataException("Failed to save conference to your managed conferences. Please try again later.");
        }
    }

    public void assignNewSessionForSpeaker(String speakerId, String sessionId, LocalDateTime startTime, LocalDateTime endTime) {
        if (speakerId == null || sessionId == null) {
            LoggerUtil.getInstance().logError("Speaker id and session id parameters cannot be null.");
            return;
        }

        // retrieving user
        Optional<User> userOptional = userRepository.findById(speakerId);
        if (userOptional.isEmpty()) {
            LoggerUtil.getInstance().logError(String.format("Id provided '%s' does not belong to any registered user.", speakerId));
            throw new UserNotFoundException(String.format("User with id '%s' does not exist.", speakerId));
        }

        // validating user is a speaker
        User user = userOptional.get();
        if (user.getRole() != UserRole.SPEAKER) {
            LoggerUtil.getInstance().logError(String.format("Id provided belongs to a user with the role of '%s'.  Required role: Speaker.", user.getRole().getDisplayName()));
            throw new InvalidUserRoleException(String.format("User with id '%s' does not have speaker permissions.", speakerId));
        }

        // update speaker's data by assigning new session
        Speaker speaker = (Speaker) user;
        speaker.assignSession(sessionId, startTime, endTime);

        // saving updated speaker user
        boolean isUserUpdated = userRepository.save(speaker, speakerId);
        if (!isUserUpdated) {
            throw new SavingDataException("Failed to save session to speaker's assigned session. Please try again later.");
        }
    }


    // READ METHODS
    public List<UserDTO> findAllById(Set<String> ids) {
        if (ids == null) {
            return Collections.emptyList();
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
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id '%s' does not exist.", id)));
    }

    public Map<String, String> findNamesByIds(Set<String> ids) {
        if (ids == null) {
            return Collections.emptyMap();
        }

        Map<String, String> namesByIds = new HashMap<>();

        // batch fetch and extract valid users matching ids
        List<Optional<User>> userOptionals = userRepository.findAllById(ids);

        userOptionals.forEach(optional ->
                System.out.println(optional.map(value -> "User found: " + value).orElse("User not found"))
        );


        System.out.println("User optionals in findNameByIds: " + userOptionals.size());

        List<User> users = CollectionUtils.extractValidEntities(userOptionals);

        System.out.println("Valid users in findNameByIds: " + users.size());

        // populating map
        users.forEach(user -> namesByIds.put(user.getId(), user.getName()));

        System.out.println("Map size after populating: " + namesByIds.size());
        return namesByIds;
    }

    public UserDTO getBydId(String id) {
        return userRepository
                .findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id '%s' does not exist.", id)));
    }

    public UserDTO getByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .map(this::mapToDTO)
                .orElseThrow(() -> new UserNotFoundException("User with email '" + email + "' could not be found."));
    }

    public UserDTO getAuthenticatedByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .map(this::mapToAuthenticatedDTO)
                .orElseThrow(() -> new UserNotFoundException("User with email '" + email + "' could not be found."));
    }

    public List<UserDTO> findAllSpeakers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == UserRole.SPEAKER)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Set<String> findManagedConferencesForOrganizer(String email) {
        if (email == null) {
            return Collections.emptySet();
        }

        Optional<User> organizerOptional = userRepository.findByEmail(email);
        if (organizerOptional.isEmpty()) {
            LoggerUtil.getInstance().logError(String.format("User with email '%s' does not exist.", email));
            throw new UserNotFoundException((String.format("User with email '%s' does not exist.", email)));
        }

        User user = organizerOptional.get();
        if (user.getRole() != UserRole.ORGANIZER) {
            LoggerUtil.getInstance().logWarning("Email provided belongs to a user with the role of " + user.getRole().getDisplayName() + ". Required role: Organizer.");
            throw new InvalidUserRoleException(String.format("User with email '%s' does not have organizer permissions.", email));
        }

        Organizer organizer = (Organizer) user;
        return organizer.getManagedConferences();
    }

    public boolean isEmailRegistered(String email) {
        if (email == null) {
            return false;
        }

        return userRepository
                .findByEmail(email)
                .isPresent();
    }

    public boolean isSpeakerAvailable(String id, LocalDateTime sessionStart, LocalDateTime sessionEnd) {
        if (id == null) {
            return false;
        }

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        if (user.getRole() != UserRole.SPEAKER) {
            return false;
        }

        Speaker speaker = (Speaker) user;
        return speaker.isAvailable(sessionStart, sessionEnd);
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

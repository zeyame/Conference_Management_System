package service;

import domain.factory.UserFactory;
import domain.model.Organizer;
import domain.model.User;
import domain.model.UserRole;
import dto.RegistrationDTO;
import dto.UserDTO;
import exception.InvalidUserRoleException;
import exception.SavingDataException;
import exception.UserNotFoundException;
import repository.UserRepository;
import util.JsonFileHandler;
import util.LoggerUtil;

import java.util.Optional;
import java.util.Set;

public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addNewManagedConferenceForOrganizer(String organizerId, String conferenceId) {
        Optional<User> userOptional = userRepository.findById(organizerId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getRole() != UserRole.ORGANIZER) {
                LoggerUtil.getInstance().logWarning("Id provided belongs to a user with the role of " + user.getRole().getDisplayName() + ". Required role: Organizer.");
                throw new InvalidUserRoleException("User with id '" + organizerId + "' does not have organizer permissions");
            }
            Organizer organizer = (Organizer) user;
            organizer.addConference(conferenceId);

            boolean isUserUpdated = userRepository.save(organizer);
            if (!isUserUpdated) {
                throw new SavingDataException("Failed to save conference to your managed conferences. Please try again later.");
            }
        } else {
            LoggerUtil.getInstance().logWarning("Id provided '" + organizerId + "' does not belong to any registered user.");
            throw new UserNotFoundException("User with id '" + organizerId + "' does not exist.");
        }
    }

    public Optional<UserDTO> findByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.map(this::mapToDTO);
    }

    public Optional<UserDTO> findAuthenticatedByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(this::mapToAuthenticatedDTO);
    }

    public Set<String> findManagedConferencesForOrganizer(String email) {
        Optional<User> organizerOptional = userRepository.findByEmail(email);
        if (organizerOptional.isPresent()) {
            User user = organizerOptional.get();
            if (user.getRole() != UserRole.ORGANIZER) {
                LoggerUtil.getInstance().logWarning("Email provided belongs to a user with the role of " + user.getRole().getDisplayName() + ". Required role: Organizer.");
                throw new InvalidUserRoleException("User with email '" + email + "' does not have organizer permissions.");
            }
            Organizer organizer = (Organizer) user;
            return organizer.getManagedConferences();
        } else {
            LoggerUtil.getInstance().logWarning("Email provided '" + email + "' does not belong to any registered user.");
            throw new UserNotFoundException("User with email '" + email + "' could not be found.");
        }
    }

    public boolean isEmailRegistered(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }


    public void registerUser(RegistrationDTO validatedDTO) {
        // creating user instance
        User user = UserFactory.createUser(validatedDTO);

        // attempting to save validated user to file storage with retries if necessary
        boolean isSavedToFile = userRepository.save(user);
        if (!isSavedToFile) {
            LoggerUtil.getInstance().logError("User registration failed. Could not save user to file storage.");
            throw new SavingDataException("An unexpected error occurred while saving your data. Please try again later.");
        }
        LoggerUtil.getInstance().logInfo("User with email '" + validatedDTO.getEmail() + "' has successfully been registered.");
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

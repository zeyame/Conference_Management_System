package service;

import domain.factory.UserFactory;
import domain.model.Organizer;
import domain.model.User;
import domain.model.UserRole;
import dto.RegistrationDTO;
import dto.UserDTO;
import exception.InvalidUserRoleException;
import exception.UserNotFoundException;
import exception.UserRegistrationException;
import repository.UserRepository;
import util.LoggerUtil;

import java.util.Optional;
import java.util.Set;

public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getRole() != UserRole.ORGANIZER) {
                LoggerUtil.getInstance().logError("Email provided belongs to a user with the role of " + user.getRole().getDisplayName() + ". Required role: Organizer.");
                throw new InvalidUserRoleException("User with email '" + email + "' does not have organizer permissions.");
            }
            Organizer organizer = (Organizer) user;
            return organizer.getManagedConferences();
        } else {
            throw new UserNotFoundException("User with email '" + email + "' could not be found.");
        }
    }

    public boolean isEmailRegistered(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }


    public void registerUser(RegistrationDTO validatedDTO) {
        boolean isSavedToFile = false;
        int retryCount = 0;
        int maxRetries = 3;

        // creating user instance
        User user = UserFactory.createUser(validatedDTO);

        while (retryCount < maxRetries && !isSavedToFile) {
            isSavedToFile = userRepository.save(user);
            if (!isSavedToFile) {
                retryCount++;
                LoggerUtil.getInstance().logError("Attempt " + retryCount + " to save user to file failed.");

                // waiting for some time before retrying again
                try {
                    Thread.sleep((long) Math.pow(2, retryCount) * 100);
                } catch (InterruptedException e) {
                    // if thread is interrupted during retries
                    Thread.currentThread().interrupt();
                    LoggerUtil.getInstance().logError("Retry operation to save user to file storage was interrupted.");
                    throw UserRegistrationException.savingData();
                }
            }
        }

        if (!isSavedToFile) {
            userRepository.removeFromMemory(user);
            LoggerUtil.getInstance().logError("User registration failed. Could not save user to file storage after " + retryCount + " attempts.");
            throw UserRegistrationException.savingData();
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

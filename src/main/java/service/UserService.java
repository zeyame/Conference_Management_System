package service;

import domain.factory.UserFactory;
import domain.model.User;
import domain.model.UserRole;
import dto.AuthenticatedUserDTO;
import dto.RegistrationDTO;
import exception.UserRegistrationException;
import repository.UserRepository;
import util.LoggerUtil;

import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<AuthenticatedUserDTO> findAuthenticatedByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.flatMap(this::mapToAuthenticatedDTO);
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
    }

    public boolean isEmailRegistered(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    private Optional<AuthenticatedUserDTO> mapToAuthenticatedDTO(User user) {
        if (user == null) return Optional.empty();

        String id = user.getId();
        String email = user.getEmail();
        String hashedPassword = user.getHashedPassword();

        return Optional.of(new AuthenticatedUserDTO(id, email, hashedPassword));
    }
}

package service;

import domain.factory.UserFactory;
import domain.model.User;
import dto.RegistrationDTO;
import repository.UserRepository;

import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(RegistrationDTO validatedDTO) {
        User user = UserFactory.createUser(validatedDTO);
        userRepository.save(user);
    }

    public boolean isEmailRegistered(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }
}

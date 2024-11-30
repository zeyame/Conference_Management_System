package controller;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.UserRole;
import dto.RegistrationDTO;
import dto.UserDTO;
import exception.PasswordException;
import exception.UserException;
import org.mindrot.jbcrypt.BCrypt;
import response.ResponseEntity;
import service.UserService;
import util.file.JsonFileHandler;
import view.*;
import util.email.EmailService;
import util.LoggerUtil;
import util.PasswordUtil;
import util.ui.UIFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MainController {
    private final UserService userService;
    private final EmailService emailService;

    public MainController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    public ResponseEntity<Boolean> validateRegistration(RegistrationDTO registrationDTO) {
        UserRole userRole = registrationDTO.getUserRole();

        // check if an account with email already exists
        String email = registrationDTO.getEmail();
        if (userService.isEmailRegistered(email)) {
            return ResponseEntity.error("An account with this email already exists.");
        }

        // password complexity check
        String password = new String(registrationDTO.getPassword());
        String errorMessage = PasswordUtil.checkPasswordComplexity(password);
        if (errorMessage != null) {
            return ResponseEntity.error(errorMessage);
        }

        if (userRole == UserRole.ORGANIZER) {
            String employeeId = registrationDTO.getEmployeeId();
            if (!validateEmployeeId(employeeId)) {
                return ResponseEntity.error("Invalid Employee ID.");
            }
        }
        
        return ResponseEntity.success(true);
    }

    public ResponseEntity<Boolean> validateLogin(String email, char[] password) {
        try {
            // fetch user from the service layer
            UserDTO authenticatedUser = userService.getAuthenticatedByEmail(email);

            // verify the password
            String hashedPassword = authenticatedUser.getHashedPassword();
            boolean isPasswordValid = PasswordUtil.verifyPassword(password, hashedPassword);

            // log success if no exceptions thrown and return response
            LoggerUtil.getInstance().logInfo(String.format("Login check carried out for user with email '%s'.", email));
            return ResponseEntity.success(isPasswordValid);
        } catch (IllegalArgumentException | UserException e) {
            return ResponseEntity.error(e.getMessage());
        }
    }


    public ResponseEntity<Void> registerUser(RegistrationDTO registrationDTO) {
        // hash the password before saving user data to storage
        char[] plainPassword = registrationDTO.getPassword();
        try {
            String hashedPassword = PasswordUtil.hashPassword(plainPassword);

            // store the hashed password in the registration dto
            char[] hashedPasswordChars = hashedPassword.toCharArray();
            registrationDTO.setPassword(hashedPasswordChars);

            userService.registerUser(registrationDTO);

            emailService.sendWelcomeEmail(registrationDTO.getEmail(), registrationDTO.getName(), registrationDTO.getUserRole());

            LoggerUtil.getInstance().logInfo("Registration is successful for user: \n" + registrationDTO);
            return ResponseEntity.success();
        } catch (PasswordException | UserException e) {
            LoggerUtil.getInstance().logError("Failed to register user: " + e.getMessage());
            return ResponseEntity.error("An unexpected error occurred when registering your data. Please try again later.");
        }
    }

    public ResponseEntity<Void> loginUser(String email) {
        try {
            UserDTO userDTO = userService.getByEmail(email);

            UserUI userUI = UIFactory.createUserUI(userDTO);
            userUI.display();

            LoggerUtil.getInstance().logInfo("User with email '" + email + "' has been successfully logged in.");
            return ResponseEntity.success();
        } catch (IllegalArgumentException e) {
            LoggerUtil.getInstance().logError("Login failed: " + e.getMessage());
            return ResponseEntity.error("Login failed due to invalid data.");
        } catch (UserException e) {
            LoggerUtil.getInstance().logError(String.format("Login failed: User with email '%s' could not be found.", email));
            return ResponseEntity.error("Email or password incorrect.");
        }
    }

    public void navigateToLoginPage() {
        new LoginUI(this);
    }

    public void navigateToRegistrationPage() {
        new RegistrationUI(this);
    }

    private boolean validateEmployeeId(String employeeId) {
        final String filePath = "src/main/resources/data/employeeids.json";
        Optional<Set<String>> optionalEmployeeIds = JsonFileHandler.loadData(filePath, new TypeReference<Set<String>>() {});

        if (optionalEmployeeIds.isEmpty()) {
            LoggerUtil.getInstance().logError("Failed to load employee IDs from file: " + filePath);
            return false;
        }

        Set<String> employeeIds = optionalEmployeeIds.get();
        return employeeIds.contains(employeeId);
    }
}

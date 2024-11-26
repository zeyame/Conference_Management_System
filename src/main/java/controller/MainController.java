package controller;

import dto.RegistrationDTO;
import dto.UserDTO;
import exception.SavingDataException;
import exception.UserLoginException;
import exception.UserNotFoundException;
import response.ResponseEntity;
import service.UserService;
import view.*;
import util.email.EmailService;
import util.LoggerUtil;
import util.PasswordUtil;
import util.ui.UIFactory;

public class MainController {
    private final UserService userService;
    private final EmailService emailService;

    public MainController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    public ResponseEntity<Boolean> validateRegistration(RegistrationDTO registrationDTO) {
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
            LoggerUtil.getInstance().logInfo("Login check carried out for user with email: '" + email + "'.");
            return ResponseEntity.success(isPasswordValid);
        } catch (UserNotFoundException e) {
            LoggerUtil.getInstance().logError("Login failed: User with email '" + email + "' not found.");
            return ResponseEntity.error("Email or password incorrect.");
        } catch (UserLoginException | IllegalArgumentException e) {
            LoggerUtil.getInstance().logError("Error validating login for email '" + email + "': " + e.getMessage());
            return ResponseEntity.error("An unexpected error occurred. Please try again later.");
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

            LoggerUtil.getInstance().logInfo("Password for user with email '" + registrationDTO.getEmail() + "' was successfully hashed.");

            userService.registerUser(registrationDTO);

            emailService.sendWelcomeEmail(registrationDTO.getEmail(), registrationDTO.getName());

            LoggerUtil.getInstance().logInfo("Registration is successful for user: \n" + registrationDTO);
            return ResponseEntity.success();
        }
        // thrown by PasswordService.hashPassword()
        catch (UserLoginException | IllegalArgumentException e) {
            LoggerUtil.getInstance().logError("Registration failed: Hashing password for user with email '" + registrationDTO.getEmail() + "' failed.");
            return ResponseEntity.error("An unexpected error occurred when registering you to the system. Please try again later.");
        }
        // thrown by userService.registerUser()
        catch (SavingDataException e) {
            return ResponseEntity.error("An unexpected error occurred when saving your data. Please try again later.");
        }
    }

    public ResponseEntity<Void> loginUser(String email) {
        try {
            UserDTO userDTO = userService.getByEmail(email);

            UserUI userUI = UIFactory.createUserUI(userDTO);
            userUI.display();

            LoggerUtil.getInstance().logInfo("User with email '" + email + "' has been successfully logged in.");
            return ResponseEntity.success();
        } catch (UserNotFoundException e) {
            LoggerUtil.getInstance().logError("User with email '" + email + "' could not be found. Login failed.");
            return ResponseEntity.error("Email or password incorrect.");
        }
    }

    public void navigateToLoginPage() {
        new LoginUI(this);
    }

    public void navigateToRegistrationPage() {
        new RegistrationUI(this);
    }
}

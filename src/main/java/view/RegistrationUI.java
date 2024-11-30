package view;

import controller.MainController;
import domain.model.UserRole;
import dto.RegistrationDTO;
import exception.FormValidationException;
import response.ResponseEntity;
import util.ui.UIComponentFactory;
import util.validation.FormValidator;

import javax.mail.internet.AddressException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.mail.internet.InternetAddress;

public class RegistrationUI extends JFrame {
    // controller to process UI input
    private final MainController mainController;

    // role selection panel to handle the user role
    private UIComponentFactory.RoleSelectionPanel roleSelectionPanel;

    // buttons
    private JButton loginButton;
    private JButton registerButton;

    // fields
    private JTextField nameField;
    private JTextField emailField;
    private JTextField speakerBioField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegistrationUI(MainController mainController) {
        this.mainController = mainController;

        // frame configuration
        setTitle("Registration Page");
        setSize(new Dimension(1000, 600));
        setResizable(false);
        setLayout(new BorderLayout());

        // centering page on screen
        setLocationRelativeTo(null);

        // title panel
        JPanel titlePanel = UIComponentFactory.createTitlePanel();
        JLabel titleLabel = UIComponentFactory.createTitleLabel("Register for an account here");
        titlePanel.add(titleLabel);

        // main panel
        JPanel mainPanel = UIComponentFactory.createMainPanel();

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        placeComponents(mainPanel);
        setUpListeners();

        setVisible(true);
    }

    // creating the UI components
    private void placeComponents(JPanel mainPanel) {
        // role selection
        roleSelectionPanel = new UIComponentFactory.RoleSelectionPanel();
        roleSelectionPanel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));

        // registration form
        JPanel registrationFormPanel = createRegistrationFormPanel();
        registrationFormPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // login option
        JPanel loginOptionPanel = createLoginOptionPanel();

        mainPanel.add(roleSelectionPanel);
        mainPanel.add(registrationFormPanel);
        mainPanel.add(loginOptionPanel);
    }

    private JPanel createRegistrationFormPanel() {
        final int FIELD_COLUMN_SIZE = 17;

        JPanel registrationPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);  // add uniform padding around components

        // Name label and field
        gbc.gridx = 0; gbc.gridy = 0;
        registrationPanel.add(new JLabel("Name"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(FIELD_COLUMN_SIZE);
        registrationPanel.add(nameField, gbc);

        // Email label and field
        gbc.gridx = 0; gbc.gridy = 1;
        registrationPanel.add(new JLabel("Email"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(FIELD_COLUMN_SIZE);
        registrationPanel.add(emailField, gbc);

        // Password label and field
        gbc.gridx = 0; gbc.gridy = 2;
        registrationPanel.add(new JLabel("Password"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(FIELD_COLUMN_SIZE);
        registrationPanel.add(passwordField, gbc);

        // Confirm Password label and field
        gbc.gridx = 0; gbc.gridy = 3;
        registrationPanel.add(new JLabel("Confirm Password"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(FIELD_COLUMN_SIZE);
        registrationPanel.add(confirmPasswordField, gbc);

        // Register button
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setPreferredSize(new Dimension(100, 40));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 114));

        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(90, 30));
        registerButton.setFocusPainted(false);

        buttonPanel.add(registerButton);
        registrationPanel.add(buttonPanel, gbc);

        return registrationPanel;
    }


    private JPanel createLoginOptionPanel() {
        JPanel loginOptionPanel = new JPanel();
        loginOptionPanel.setLayout(new BoxLayout(loginOptionPanel, BoxLayout.Y_AXIS));

        JLabel loginMessage = new JLabel("Already have an account? Login here.");
        loginMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(90, 27));
        loginButton.setFocusPainted(false);

        loginOptionPanel.add(loginMessage);
        loginOptionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        loginOptionPanel.add(loginButton);

        return loginOptionPanel;
    }

    // initializing component interactivity
    private void setUpListeners() {
        loginButton.addActionListener(this::handleLoginClick);
        registerButton.addActionListener(this::handleRegisterClick);
    }

    private void handleLoginClick(ActionEvent e) {
        mainController.navigateToLoginPage();
        dispose();
    }

    private void handleRegisterClick(ActionEvent e) {
        String email = emailField.getText();
        String name = nameField.getText();
        String speakerBio = speakerBioField.getText();
        char[] password = passwordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();
        UserRole userRole = roleSelectionPanel.getSelectedRole();

        // validate user input
        try {
            FormValidator.validateRegistrationForm(email, name, password, confirmPassword, userRole);
        } catch (FormValidationException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // once form is validated, register user to the system
        RegistrationDTO registrationDTO = new RegistrationDTO(email, name, speakerBio,password, userRole);

        ResponseEntity<Boolean> validationResponse = mainController.validateRegistration(registrationDTO);
        if (!validationResponse.isSuccess()) {
            JOptionPane.showMessageDialog(this, validationResponse.getErrorMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ResponseEntity<Void> registrationResponse = mainController.registerUser(registrationDTO);
        if (!registrationResponse.isSuccess()) {
            JOptionPane.showMessageDialog(this, registrationResponse.getErrorMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        clearFormData();
        JOptionPane.showMessageDialog(this, "Registration successful. You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    private void clearFormData() {
        roleSelectionPanel.clearSelection();
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

}
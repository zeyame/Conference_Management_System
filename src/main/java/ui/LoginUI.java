package ui;

import controller.MainController;
import exception.FormValidationException;
import exception.UserLoginException;
import response.ResponseEntity;
import util.UIComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginUI extends JFrame {
    // controller to process UI input
    private final MainController mainController;

    // role selection panel to handle the user role
    private UIComponentFactory.RoleSelectionPanel roleSelectionPanel;

    // buttons
    private JButton loginButton;
    private JButton registerButton;

    // fields
    private JTextField emailField;
    private JPasswordField passwordField;


    public LoginUI(MainController mainController) {
        this.mainController = mainController;

        // frame configuration
        setTitle("Login page");
        setSize(new Dimension(1000, 600));
        setResizable(false);
        setLayout(new BorderLayout());

        // centering the UI
        setLocationRelativeTo(null);

        // title panel
        JPanel titlePanel = UIComponentFactory.createTitlePanel();
        JLabel titleLabel = UIComponentFactory.createTitleLabel("Welcome to UH Conference Management System");
        titlePanel.add(titleLabel);

        // main panel
        JPanel mainPanel = UIComponentFactory.createMainPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 150, 0));

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        placeComponents(mainPanel);
        setUpListeners();

        setVisible(true);
    }

    // creating the UI components
    private void placeComponents(JPanel mainPanel) {
        // create the login title label
        JLabel loginTitleLabel = new JLabel("Login to your account");
        loginTitleLabel.setFont(new Font("Sans serif", Font.BOLD, 18));
        loginTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginTitleLabel.setBorder(BorderFactory.createEmptyBorder(80, 0, 0, 0));

        // create the login form
        JPanel loginFormPanel = createLoginFormPanel();

        // option to register for a new account
        JPanel registerOptionPanel = createRegisterOptionPanel();
        registerOptionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        // placing the components
        mainPanel.add(loginTitleLabel);
        mainPanel.add(loginFormPanel);
        mainPanel.add(registerOptionPanel);
    }

    private JPanel createLoginFormPanel() {
        JPanel loginFormPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);  // Add uniform padding around components

        // Email label and field
        gbc.gridx = 0; gbc.gridy = 1;
        loginFormPanel.add(new JLabel("Email"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(17);
        loginFormPanel.add(emailField, gbc);

        // Password label and field
        gbc.gridx = 0; gbc.gridy = 2;
        loginFormPanel.add(new JLabel("Password"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(17);
        loginFormPanel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setPreferredSize(new Dimension(100, 40));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 65));

        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(90, 30));
        loginButton.setFocusPainted(false);

        buttonPanel.add(loginButton);
        loginFormPanel.add(buttonPanel, gbc);

        return loginFormPanel;
    }

    private JPanel createRegisterOptionPanel() {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));

        JLabel registerMessage = new JLabel("Don't have an account? Register now.");
        registerMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerButton = new JButton("Register");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(90, 27));

        registerPanel.add(registerMessage);
        registerPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        registerPanel.add(registerButton);

        return registerPanel;
    }


    // initializing component interactivity
    private void setUpListeners() {
        registerButton.addActionListener(this::handleRegisterClick);
        loginButton.addActionListener(this::handleLoginClick);
    }

    private void handleRegisterClick(ActionEvent e) {
        mainController.navigateToRegistrationPage();
        dispose();
    }

    private void handleLoginClick(ActionEvent e) {
        String email = emailField.getText();
        char[] password = passwordField.getPassword();

        try {
            validateLoginForm(email, password);
        } catch (FormValidationException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ResponseEntity<Boolean> validateLoginResponse = mainController.validateLogin(email, password);
        if (!validateLoginResponse.isSuccess()) {
            JOptionPane.showMessageDialog(this, validateLoginResponse.getErrorMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isLoginValid = validateLoginResponse.getData();
        if (!isLoginValid) {
            JOptionPane.showMessageDialog(this, "Email or password incorrect.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ResponseEntity<Void> loginResponse = mainController.loginUser(email);
        if (!loginResponse.isSuccess()) {
            JOptionPane.showMessageDialog(this, loginResponse.getErrorMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dispose();
    }

    private void validateLoginForm(String email, char[] password) {
        if (email.isEmpty() || password.length == 0) {
            throw new FormValidationException("All fields must be filled out.");
        }
    }
}

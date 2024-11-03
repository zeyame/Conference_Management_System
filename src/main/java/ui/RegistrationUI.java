package ui;

import controller.MainController;
import util.UIComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegistrationUI extends JFrame {
    private final MainController mainController;
    private JButton loginButton;
    private JButton registerButton;

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
        JPanel rolePanel = UIComponentFactory.createRoleSelectionPanel();
        rolePanel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));

        // registration form
        JPanel registrationFormPanel = createRegistrationFormPanel();
        registrationFormPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // login option
        JPanel loginOptionPanel = createLoginOptionPanel();

        mainPanel.add(rolePanel);
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
        JTextField nameTextField = new JTextField(FIELD_COLUMN_SIZE);
        registrationPanel.add(nameTextField, gbc);

        // Email label and field
        gbc.gridx = 0; gbc.gridy = 1;
        registrationPanel.add(new JLabel("Email"), gbc);

        gbc.gridx = 1;
        JTextField emailTextField = new JTextField(FIELD_COLUMN_SIZE);
        registrationPanel.add(emailTextField, gbc);

        // Password label and field
        gbc.gridx = 0; gbc.gridy = 2;
        registrationPanel.add(new JLabel("Password"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(FIELD_COLUMN_SIZE);
        registrationPanel.add(passwordField, gbc);

        // Confirm Password label and field
        gbc.gridx = 0; gbc.gridy = 3;
        registrationPanel.add(new JLabel("Confirm Password"), gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(FIELD_COLUMN_SIZE);
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
    }

    private void handleLoginClick(ActionEvent e) {
        mainController.navigateToLogin();
        dispose();
    }

}

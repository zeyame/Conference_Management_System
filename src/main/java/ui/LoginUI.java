package ui;

import com.sun.tools.javac.Main;
import controller.MainController;
import util.UIComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginUI extends JFrame {
    private final MainController mainController;
    private JButton loginButton;
    private JButton registerButton;

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
        // role selection
        JPanel rolePanel = UIComponentFactory.createRoleSelectionPanel();
        rolePanel.setBorder(BorderFactory.createEmptyBorder(90, 0, 0, 0));

        // create the login form (username, password, login)
        JPanel loginFormPanel = createLoginFormPanel();

        // option to register a new account
        JPanel registerOptionPanel = createRegisterOptionPanel();

        // placing the components
        mainPanel.add(rolePanel);
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
        JTextField emailTextField = new JTextField(17);
        loginFormPanel.add(emailTextField, gbc);

        // Password label and field
        gbc.gridx = 0; gbc.gridy = 2;
        loginFormPanel.add(new JLabel("Password"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(17);
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
        registerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        registerPanel.add(registerButton);

        return registerPanel;
    }


    // initializing component interactivity
    private void setUpListeners() {
        registerButton.addActionListener(this::handleRegisterClick);
    }

    private void handleRegisterClick(ActionEvent e) {
        mainController.navigateToRegister();
        dispose();
    }



}

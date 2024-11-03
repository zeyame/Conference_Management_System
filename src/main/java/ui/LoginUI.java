package ui;

import util.UIComponentFactory;

import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {
    public LoginUI() {
        setTitle("Login page");
        setSize(new Dimension(1000, 600));
        setResizable(false);
        setLayout(new BorderLayout());

        // centering the UI
        setLocationRelativeTo(null);

        // title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Welcome to UH Conference Management System.");
        titleLabel.setFont(new Font("Sans Serif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(25, 55, 0, 0));
        titlePanel.add(titleLabel);

        // main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(400, 200));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 90, 0));      // moving main panel 90 rows north

        add(Box.createRigidArea(new Dimension(0, 40)));
        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        placeComponents(mainPanel);
        setVisible(true);
    }

    private void placeComponents(JPanel mainPanel) {
        // role selection
        JPanel rolePanel = UIComponentFactory.getRoleSelectionPanel();

        // create the login form (username, password, login)
        JPanel loginFormPanel = getLoginFormPanel();
        loginFormPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // option to register a new account
        JPanel registerOptionPanel = getRegisterPanel();

        // placing the components
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(rolePanel);
        mainPanel.add(loginFormPanel);
        mainPanel.add(registerOptionPanel);
        mainPanel.add(Box.createVerticalGlue());
    }

    private JPanel getLoginFormPanel() {
        // login form panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

        // user id field
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));

        JLabel usernameLabel = new JLabel("User Id");
        JTextField usernameTextField = new JTextField(20);
        usernameTextField.setMaximumSize(new Dimension(180, 50));

        usernamePanel.add(usernameLabel);
        usernamePanel.add(Box.createRigidArea(new Dimension(33, 0)));
        usernamePanel.add(usernameTextField);

        // password field
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));

        JLabel passwordLabel = new JLabel("Password");
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(180, 60));

        passwordPanel.add(passwordLabel);
        passwordPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        passwordPanel.add(passwordField);

        // login button
        JPanel loginButtonPanel = new JPanel();
        loginButtonPanel.setLayout(new BoxLayout(loginButtonPanel, BoxLayout.X_AXIS));

        JButton loginButton = new JButton("Login");
        loginButton.setMaximumSize(new Dimension(new Dimension(100, 40)));
        loginButton.setFocusPainted(false);
        loginButtonPanel.add(loginButton);

        loginPanel.add(usernamePanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        loginPanel.add(passwordPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 13)));
        loginPanel.add(loginButtonPanel);

        return loginPanel;
    }

    private JPanel getRegisterPanel() {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));

        JLabel registerMessage = new JLabel("Don't have an account? Register now.");
        registerMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton registerButton = new JButton("Register");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(90, 27));

        registerPanel.add(registerMessage);
        registerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        registerPanel.add(registerButton);

        return registerPanel;
    }


}

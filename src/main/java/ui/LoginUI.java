package ui;

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

    private void placeComponents(JPanel panel) {
        // role selection
        JPanel rolePanel = getRoleSelectionPanel();

        // create the login form (username, password, login)
        JPanel loginFormPanel = getLoginFormPanel();

        // placing the components
        panel.add(Box.createVerticalGlue());
        panel.add(rolePanel);
        panel.add(loginFormPanel);
        panel.add(Box.createVerticalGlue());
    }

    private JPanel getRoleSelectionPanel() {
        JPanel roleSelectionPanel = new JPanel();
        roleSelectionPanel.setLayout(new BoxLayout(roleSelectionPanel, BoxLayout.Y_AXIS));

        // role selection subtitle
        JPanel roleLabelPanel = new JPanel();
        roleLabelPanel.setLayout(new BoxLayout(roleLabelPanel, BoxLayout.X_AXIS));
        JLabel roleLabel = new JLabel("Choose your role");

        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align

        roleLabelPanel.add(roleLabel);

        // radio buttons
        JPanel radioButtonPanel = getRadioButtonPanel();

        roleSelectionPanel.add(roleLabelPanel);
        roleSelectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        roleSelectionPanel.add(radioButtonPanel);
        roleSelectionPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        return roleSelectionPanel;
    }

    private JPanel getRadioButtonPanel() {
        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.X_AXIS));

        // create radio buttons
        JRadioButton organizerRadioButton = new JRadioButton("Organizer");
        JRadioButton attendeeRadioButton = new JRadioButton("Attendee");
        JRadioButton speakerRadioButton = new JRadioButton("Speaker");

        // remove the focus indicator around the buttons
        organizerRadioButton.setFocusPainted(false);
        attendeeRadioButton.setFocusPainted(false);
        speakerRadioButton.setFocusPainted(false);

        // group the radio buttons
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(organizerRadioButton);
        buttonGroup.add(attendeeRadioButton);
        buttonGroup.add(speakerRadioButton);

        radioButtonPanel.add(organizerRadioButton);
        radioButtonPanel.add(attendeeRadioButton);
        radioButtonPanel.add(speakerRadioButton);
        return radioButtonPanel;
    }

    private JPanel getLoginFormPanel() {
        // login form panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

        // username field
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
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton loginButton = new JButton("Login");
        loginButton.setMaximumSize(new Dimension(new Dimension(100, 40)));
        buttonPanel.add(loginButton);

        // option to register a new account
        JPanel registrationPanel = getRegistrationPanel();

        loginPanel.add(usernamePanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        loginPanel.add(passwordPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        loginPanel.add(buttonPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        loginPanel.add(registrationPanel);

        return loginPanel;
    }

    private JPanel getRegistrationPanel() {
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

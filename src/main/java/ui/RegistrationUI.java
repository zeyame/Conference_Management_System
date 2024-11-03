package ui;

import util.UIComponentFactory;

import javax.swing.*;
import java.awt.*;

public class RegistrationUI extends JFrame {

    public RegistrationUI() {
        setTitle("Registration Page");
        setSize(new Dimension(1000, 600));
        setResizable(false);
        setLayout(new BorderLayout());

        // centering page on screen
        setLocationRelativeTo(null);

        // title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Register for an account here.");
        titleLabel.setFont(new Font("Sans Serif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(25, 30, 0, 0));
        titlePanel.add(titleLabel);

        // main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(400, 200));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 180, 0));

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        placeComponents(mainPanel);

        setVisible(true);
    }

    private void placeComponents(JPanel mainPanel) {
        // role selection
        JPanel rolePanel = UIComponentFactory.getRoleSelectionPanel();
        rolePanel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));

        // registration form
        JPanel registrationFormPanel = getRegistrationFormPanel();
        registrationFormPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // login option
        JPanel loginOptionPanel = getLoginOptionPanel();

        mainPanel.add(rolePanel);
        mainPanel.add(registrationFormPanel);
        mainPanel.add(loginOptionPanel);
    }

    private JPanel getRegistrationFormPanel() {
        JPanel registrationPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);  // Add uniform padding around components

        // Name label and field
        gbc.gridx = 0; gbc.gridy = 0;
        registrationPanel.add(new JLabel("Name"), gbc);

        gbc.gridx = 1;
        JTextField nameTextField = new JTextField(17);
        registrationPanel.add(nameTextField, gbc);

        // Email label and field
        gbc.gridx = 0; gbc.gridy = 1;
        registrationPanel.add(new JLabel("Email"), gbc);

        gbc.gridx = 1;
        JTextField emailTextField = new JTextField(17);
        registrationPanel.add(emailTextField, gbc);

        // Password label and field
        gbc.gridx = 0; gbc.gridy = 2;
        registrationPanel.add(new JLabel("Password"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(17);
        registrationPanel.add(passwordField, gbc);

        // Confirm Password label and field
        gbc.gridx = 0; gbc.gridy = 3;
        registrationPanel.add(new JLabel("Confirm Password"), gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(17);
        registrationPanel.add(confirmPasswordField, gbc);

        // Register button
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setPreferredSize(new Dimension(100, 40));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 114));
        JButton registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(90, 30));
        registerButton.setFocusPainted(false);

        buttonPanel.add(registerButton);
        registrationPanel.add(buttonPanel, gbc);

        return registrationPanel;
    }


    private JPanel getLoginOptionPanel() {
        JPanel loginOptionPanel = new JPanel();
        loginOptionPanel.setLayout(new BoxLayout(loginOptionPanel, BoxLayout.Y_AXIS));

        JLabel loginMessage = new JLabel("Already have an account? Login here.");
        loginMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(90, 27));
        loginButton.setFocusPainted(false);

        loginOptionPanel.add(loginMessage);
        loginOptionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        loginOptionPanel.add(loginButton);

        return loginOptionPanel;
    }
}

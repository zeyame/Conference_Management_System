package view.authentication;

import controller.MainController;
import domain.model.user.UserRole;
import dto.RegistrationDTO;
import exception.FormValidationException;
import response.ResponseEntity;
import util.LoggerUtil;
import util.ui.UIComponentFactory;
import util.validation.FormValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegistrationUI extends JFrame {
    // controller to process UI input
    private final MainController mainController;

    // role selection panel to handle the user role
    private UIComponentFactory.RoleSelectionPanel roleSelectionPanel;

    // main panel
    private final JPanel mainPanel;

    // buttons
    private JButton loginButton;
    private JButton registerButton;

    // fields
    private JTextField nameField;
    private JTextField emailField;
    private JTextField speakerBioField;
    private JTextField employeeIdField;
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
        mainPanel = UIComponentFactory.createMainPanel();

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        placeComponents(mainPanel, false, false);

        setVisible(true);
    }

    // creating the UI components
    private void placeComponents(JPanel mainPanel, boolean addSpeakerBio, boolean addEmployeeId) {
        // clear existing components (except roleSelectionPanel)
        mainPanel.removeAll();

        //  retain existing roleSelectionPanel
        if (roleSelectionPanel == null) {
            roleSelectionPanel = new UIComponentFactory.RoleSelectionPanel();
            roleSelectionPanel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));
            roleSelectionPanel.addRoleSelectionListener(this::handleRoleSelection);
        }

        // registration form
        JPanel registrationFormPanel = createRegistrationFormPanel(addSpeakerBio, addEmployeeId);
        registrationFormPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // login option
        JPanel loginOptionPanel = createLoginOptionPanel();

        // add the retained roleSelectionPanel and other components
        mainPanel.add(roleSelectionPanel);
        mainPanel.add(registrationFormPanel);
        mainPanel.add(loginOptionPanel);

        // refresh UI
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createRegistrationFormPanel(boolean addSpeakerBio, boolean addEmployeeId) {
        final int FIELD_COLUMN_SIZE = 17;
        int y = 0;

        JPanel registrationPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);  // add uniform padding around components

        // Name label and field
        gbc.gridx = 0; gbc.gridy = y++;
        registrationPanel.add(new JLabel("Name"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(FIELD_COLUMN_SIZE);
        registrationPanel.add(nameField, gbc);

        // Email label and field
        gbc.gridx = 0; gbc.gridy = y++;
        registrationPanel.add(new JLabel("Email"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(FIELD_COLUMN_SIZE);
        registrationPanel.add(emailField, gbc);

        if (addSpeakerBio) {
            // bio label and field
            gbc.gridx = 0; gbc.gridy = y++;
            registrationPanel.add(new JLabel("Bio"), gbc);

            gbc.gridx = 1;
            speakerBioField = new JTextField(FIELD_COLUMN_SIZE);
            registrationPanel.add(speakerBioField, gbc);
        } else if (addEmployeeId) {
            // employee id label and field
            gbc.gridx = 0; gbc.gridy = y++;
            registrationPanel.add(new JLabel("Employee ID"), gbc);

            gbc.gridx = 1;
            employeeIdField = new JTextField(FIELD_COLUMN_SIZE);
            registrationPanel.add(employeeIdField, gbc);
        }

        // Password label and field
        gbc.gridx = 0; gbc.gridy = y++;
        registrationPanel.add(new JLabel("Password"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(FIELD_COLUMN_SIZE);
        registrationPanel.add(passwordField, gbc);

        // Confirm Password label and field
        gbc.gridx = 0; gbc.gridy = y++;
        registrationPanel.add(new JLabel("Confirm Password"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(FIELD_COLUMN_SIZE);
        registrationPanel.add(confirmPasswordField, gbc);

        // Register button
        gbc.gridx = 1; gbc.gridy = y;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setPreferredSize(new Dimension(100, 40));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 114));

        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(90, 30));
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(this::handleRegisterClick);

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
        loginButton.addActionListener(this::handleLoginClick);

        loginOptionPanel.add(loginMessage);
        loginOptionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        loginOptionPanel.add(loginButton);

        return loginOptionPanel;
    }

    private void handleRoleSelection() {
        UserRole selectedRole = roleSelectionPanel.getSelectedRole();
        placeComponents(mainPanel, selectedRole == UserRole.SPEAKER, selectedRole == UserRole.ORGANIZER);
    }

    private void handleLoginClick(ActionEvent e) {
        mainController.navigateToLoginPage();
        dispose();
    }

    private void handleRegisterClick(ActionEvent e) {
        LoggerUtil.getInstance().logInfo("Request to register user received.");

        String email = emailField.getText();
        String name = nameField.getText();
        String speakerBio = speakerBioField != null ? speakerBioField.getText() : "";
        String employeeId = employeeIdField != null ? employeeIdField.getText() : "";
        char[] password = passwordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();
        UserRole userRole = roleSelectionPanel.getSelectedRole();

        // validate user input
        try {
            FormValidator.validateRegistrationForm(email, name, speakerBio, employeeId, password, confirmPassword, userRole);
        } catch (FormValidationException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // once form is validated, register user to the system
        RegistrationDTO registrationDTO = new RegistrationDTO(email, name, speakerBio, employeeId, password, userRole);

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
        if (speakerBioField != null) speakerBioField.setText("");
        if (employeeIdField != null) employeeIdField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

}
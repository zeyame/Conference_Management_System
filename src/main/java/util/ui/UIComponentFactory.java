package util.ui;

import domain.model.UserRole;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UIComponentFactory {

    // private no-arg constructor to suppress instantiability
    private UIComponentFactory() {}

    public static JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(400, 200));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 120, 0));
        return mainPanel;
    }

    public static JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 0));
        return titlePanel;
    }

    public static JLabel createTitleLabel(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Sans Serif", Font.BOLD, 24));
        return titleLabel;
    }

    public static JPanel createWelcomePanel(String userName) {
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        JLabel welcomeLabel = new JLabel("Welcome, " + userName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomePanel.add(welcomeLabel);

        return welcomePanel;
    }

    public static JPanel createHeaderPanel(String title, JButton backButton) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0,0));

        headerPanel.add(backButton);

        // spacing between back button and title
        headerPanel.add(Box.createRigidArea(new Dimension(390, 0)));

        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 24));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0,0, 0));

        headerPanel.add(headerLabel);

        return headerPanel;
    }

    public static JButton createBackButton(ActionListener backAction) {
        JButton backButton = new JButton("←"); // left arrow button
        backButton.setToolTipText("Go back");
        backButton.setFocusable(false);

        // Adjust font size to make the button smaller
        backButton.setFont(backButton.getFont().deriveFont(12f)); // Smaller font size (adjust as needed)

        // Set fixed button size
        Dimension buttonSize = new Dimension(30, 30); // Adjust width and height as needed
        backButton.setPreferredSize(buttonSize);
        backButton.setMinimumSize(buttonSize);
        backButton.setMaximumSize(buttonSize);

        // Add border for spacing inside button
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Attach action listener
        backButton.addActionListener(backAction);

        return backButton;
    }

    public static JSpinner createDateSpinner() {
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(150, 30));
        return dateSpinner;
    }

    public static JSpinner createTimeSpinner() {
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setPreferredSize(new Dimension(150, 30));
        return timeSpinner;
    }

    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setFocusPainted(false);
        return button;
    }

    public static JPanel createButtonPanel(JButton button) {
        // Bottom panel for the Add Conference button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add padding above and below

        button.setPreferredSize(new Dimension(180, 35));
        button.setFocusPainted(false);
        buttonPanel.add(button);

        return buttonPanel;
    }

    public static JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    public static void addLabelToPanel(JPanel panel, String text, Font font, GridBagConstraints gbc, int x, int y, int width) {
        JLabel label = createStyledLabel(text, font);
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        panel.add(label, gbc);
    }

    public static void addTextAreaToPanel(JPanel panel, String text, Font font, GridBagConstraints gbc, int gridx, int gridy, int gridwidth) {
        JLabel label = new JLabel("<html><div style='width: 300px'>" + text + "</div></html>");
        label.setFont(font);

        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(label, gbc);
    }

    public static GridBagConstraints createDefaultGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }


    // nested static class to create and manage the role selection panel used in LoginUI and RegistrationUI
    public static class RoleSelectionPanel extends JPanel {
        ButtonGroup roleGroup;
        JRadioButton organizerRole;
        JRadioButton attendeeRole;
        JRadioButton speakerRole;

        public RoleSelectionPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            // Create and add the role selection label
            JLabel roleLabel = new JLabel("Choose your role");
            roleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(roleLabel);

            // Add some space below the label
            add(Box.createRigidArea(new Dimension(0, 10)));

            // Create and add the radio buttons
            organizerRole = new JRadioButton("Organizer");
            attendeeRole = new JRadioButton("Attendee");
            speakerRole = new JRadioButton("Speaker");

            // Remove the focus indicator
            organizerRole.setFocusPainted(false);
            attendeeRole.setFocusPainted(false);
            speakerRole.setFocusPainted(false);

            attendeeRole.setSelected(true);     // make attendee role as the default role

            // Group the radio buttons
            roleGroup = new ButtonGroup();
            roleGroup.add(organizerRole);
            roleGroup.add(attendeeRole);
            roleGroup.add(speakerRole);

            // Create a panel for the radio buttons and add them
            JPanel radioButtonPanel = new JPanel();
            radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.X_AXIS));
            radioButtonPanel.add(organizerRole);
            radioButtonPanel.add(attendeeRole);
            radioButtonPanel.add(speakerRole);

            // Add the radio button panel to the main panel
            add(radioButtonPanel);

            // Add some space below the radio buttons
            add(Box.createRigidArea(new Dimension(0, 20)));
        }

        public void addRoleSelectionListener(Runnable handler) {
            organizerRole.addActionListener(e -> handler.run());
            speakerRole.addActionListener(e -> handler.run());
            attendeeRole.addActionListener(e -> handler.run());
        }

        public UserRole getSelectedRole() {
            if (organizerRole.isSelected()) return UserRole.ORGANIZER;
            else if (attendeeRole.isSelected()) return UserRole.ATTENDEE;
            else if (speakerRole.isSelected()) return UserRole.SPEAKER;
            else return null;
        }

        public void setSelectedRole(UserRole role) {
            switch (role) {
                case ORGANIZER -> organizerRole.setSelected(true);
                case ATTENDEE -> attendeeRole.setSelected(true);
                case SPEAKER -> speakerRole.setSelected(true);
                default -> throw new IllegalArgumentException("User role must be one of: Organizer, Attendee, or Speaker.");
            }
        }

        public void clearSelection() {
            roleGroup.clearSelection();
        }
    }
}

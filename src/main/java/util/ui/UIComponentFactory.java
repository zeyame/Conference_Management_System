package util.ui;

import domain.model.UserRole;
import dto.ConferenceDTO;
import dto.SessionDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

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

    public static JPanel createHeaderPanel(String title, ActionListener backAction, int leftPadding) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0,0));

        headerPanel.add(createBackButton(backAction));

        // spacing between back button and title
        headerPanel.add(Box.createRigidArea(new Dimension(leftPadding, 0)));

        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 24));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0,0, 0));

        headerPanel.add(headerLabel);

        return headerPanel;
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
        button.setPreferredSize(new Dimension(190, 40));
        button.setFocusPainted(false);
        return button;
    }

    public static JPanel createButtonPanel(JButton button) {
        // Bottom panel for the Add Conference button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add padding above and below

        button.setPreferredSize(new Dimension(240, 35));
        button.setFocusPainted(false);
        buttonPanel.add(button);

        return buttonPanel;
    }

    public static JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    public static JPanel createEmptyStatePanel(String message) {
        JPanel emptyStatePanel = new JPanel();
        JLabel emptyStateMessage = new JLabel(message);
        emptyStateMessage.setFont(new Font("Sans serif", Font.PLAIN, 16));
        emptyStatePanel.add(emptyStateMessage);
        emptyStatePanel.setBorder(BorderFactory.createEmptyBorder(270, 0, 0, 0));
        return emptyStatePanel;
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

    public static JPanel createConferenceDetailsPanel(ConferenceDTO conferenceDTO, String organizerName) {
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = UIComponentFactory.createDefaultGridBagConstraints();

        // increase vertical spacing with larger top/bottom insets
        gbc.insets = new Insets(15, 5, 15, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // adding header with extra bottom spacing
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 25, 5);  // Extra bottom padding for header
        UIComponentFactory.addLabelToPanel(detailsPanel, "Conference Details", new Font("Arial", Font.BOLD, 20), gbc, 0, 0, 2);

        // reset gridwidth and restore normal insets for subsequent rows
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 5, 15, 5);

        // adding organizer
        addLabelToPanel(detailsPanel, "Organized by: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 1, 1);
        addLabelToPanel(detailsPanel, organizerName, new Font("Arial", Font.PLAIN, 18), gbc, 1, 1, 1);

        // adding description
        addLabelToPanel(detailsPanel, "Description:", new Font("Arial", Font.PLAIN, 18), gbc, 0, 2, 1);
        addTextAreaToPanel(detailsPanel, conferenceDTO.getDescription(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 2, 1);

        // adding start date
        addLabelToPanel(detailsPanel, "Start Date: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 3, 1);
        addLabelToPanel(detailsPanel, conferenceDTO.getStartDate().toString(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 3, 1);

        // adding end date
        addLabelToPanel(detailsPanel, "End Date: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 4, 1);
        addLabelToPanel(detailsPanel, conferenceDTO.getEndDate().toString(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 4, 1);

        detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 150));

        return detailsPanel;
    }


    public static JPanel createSessionDetailsPanel(SessionDTO sessionDTO) {
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = UIComponentFactory.createDefaultGridBagConstraints();

        // Increase vertical spacing with larger top/bottom insets
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // adding header with extra bottom spacing
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 25, 5);  // Extra bottom padding for header
        UIComponentFactory.addLabelToPanel(detailsPanel, "Session Details", new Font("Arial", Font.BOLD, 20), gbc, 0, 0, 2);

        // Reset gridwidth and restore normal insets for subsequent rows
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 5, 15, 5);

        // adding speaker
        UIComponentFactory.addLabelToPanel(detailsPanel, "Speaker: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 1, 1);
        UIComponentFactory.addLabelToPanel(detailsPanel, sessionDTO.getSpeakerName(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 1, 1);

        // adding description
        UIComponentFactory.addLabelToPanel(detailsPanel, "Description:", new Font("Arial", Font.PLAIN, 18), gbc, 0, 2, 1);
        UIComponentFactory.addTextAreaToPanel(detailsPanel, sessionDTO.getDescription(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 2, 1);

        // adding room
        UIComponentFactory.addLabelToPanel(detailsPanel, "Room: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 3, 1);
        UIComponentFactory.addLabelToPanel(detailsPanel, sessionDTO.getRoom(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 3, 1);

        // adding session date
        UIComponentFactory.addLabelToPanel(detailsPanel, "Date: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 4, 1);
        UIComponentFactory.addLabelToPanel(detailsPanel, sessionDTO.getDate().toString(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 4, 1);

        // adding start time
        UIComponentFactory.addLabelToPanel(detailsPanel, "Start Time: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 5, 1);
        UIComponentFactory.addLabelToPanel(detailsPanel, sessionDTO.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")), new Font("Arial", Font.PLAIN, 18), gbc, 1, 5, 1);

        // adding end time
        UIComponentFactory.addLabelToPanel(detailsPanel, "End Time: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 6, 1);
        UIComponentFactory.addLabelToPanel(detailsPanel, sessionDTO.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")), new Font("Arial", Font.PLAIN, 18), gbc, 1, 6, 1);

        detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 30));

        return detailsPanel;
    }

    public static JSplitPane createSplitPane(String leftSideTitle, String rightSideTitle, List<SessionDTO> leftSideSessions, List<SessionDTO> rightSideSessions, String buttonText, ActionListener buttonHandler) {
        // create panels for ongoing and upcoming conferences
        JPanel registeredPanel = createSessionsScrollPanePanel(leftSideTitle, leftSideSessions, buttonText, buttonHandler);
        JPanel unregisteredPanel = createSessionsScrollPanePanel(rightSideTitle, rightSideSessions, buttonText, buttonHandler);

        Dimension equalSize = new Dimension(400, 0);
        registeredPanel.setPreferredSize(equalSize);
        unregisteredPanel.setPreferredSize(equalSize);

        // create a split pane with the two panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, registeredPanel, unregisteredPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(5);
        return splitPane;
    }

    public static JPanel createSessionsScrollPanePanel(String title, List<SessionDTO> sessionDTOs, String buttonText, ActionListener buttonHandler) {
        JPanel sessionsPanel = new JPanel();
        sessionsPanel.setLayout(new BorderLayout());

        // header label
        JLabel headerLabel = new JLabel(title, SwingConstants.CENTER);
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        sessionsPanel.add(headerLabel, BorderLayout.NORTH);

        // Scrollable list of sessions
        JScrollPane scrollPane = createSessionsScrollPane(sessionDTOs, buttonText, buttonHandler);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        sessionsPanel.add(scrollPane, BorderLayout.CENTER);

        return sessionsPanel;
    }

    public static JScrollPane createSessionsScrollPane(List<SessionDTO> sessionDTOs, String buttonText, ActionListener buttonHandler) {
        JPanel sessionsPanel = new JPanel();
        sessionsPanel.setLayout(new BoxLayout(sessionsPanel, BoxLayout.Y_AXIS));
        sessionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 0));

        for (SessionDTO sessionDTO : sessionDTOs) {
            sessionsPanel.add(UIComponentFactory.createSessionPanel(sessionDTO, buttonHandler, buttonText));
            sessionsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JScrollPane scrollPane = new JScrollPane(sessionsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(7);
        return scrollPane;
    }


    private static JPanel createSessionPanel(SessionDTO session, ActionListener buttonListener, String buttonText) {
        JPanel sessionPanel = new JPanel();
        sessionPanel.setLayout(new BoxLayout(sessionPanel, BoxLayout.Y_AXIS));

        // session name
        JLabel nameLabel = new JLabel("Name: " + session.getName());
        nameLabel.setFont(new Font("Sans serif", Font.BOLD, 16));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // session date and time
        JLabel dateLabel = new JLabel("Date: " + session.getDate().toString());
        JLabel timeLabel = new JLabel("Time: " + session.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + session.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // manage or view session button
        JButton button = new JButton(buttonText);
        button.setFocusPainted(false);
        button.putClientProperty("sessionId", session.getId());
        button.addActionListener(buttonListener);

        // Add components to session panel
        sessionPanel.add(nameLabel);
        sessionPanel.add(dateLabel);
        sessionPanel.add(timeLabel);
        sessionPanel.add(button);

        return sessionPanel;
    }

    public static JScrollPane createConferenceScrollPane(List<ConferenceDTO> conferences, ActionListener handleViewOrManageConferenceButton, String buttonText) {
        JPanel conferencesPanel = new JPanel();
        conferencesPanel.setLayout(new BoxLayout(conferencesPanel, BoxLayout.Y_AXIS));
        conferencesPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 0));

        for (ConferenceDTO conferenceDTO : conferences) {
            conferencesPanel.add(UIComponentFactory.createConferencePanel(conferenceDTO, handleViewOrManageConferenceButton, buttonText));
            conferencesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JScrollPane scrollPane = new JScrollPane(conferencesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(7);
        return scrollPane;
    }


    private static JPanel createConferencePanel(ConferenceDTO conference, ActionListener listener, String buttonText) {
        JPanel conferencePanel = new JPanel();
        conferencePanel.setLayout(new BoxLayout(conferencePanel, BoxLayout.Y_AXIS));

        // name of conference
        JLabel nameLabel = new JLabel(conference.getName());
        nameLabel.setFont(new Font("Sans serif", Font.BOLD, 16));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // start date of conference
        JLabel dateLabel = new JLabel("Date: " + conference.getStartDate().toString());
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // manage conference button
        JButton manageButton = new JButton(buttonText);
        manageButton.setFocusPainted(false);
        manageButton.putClientProperty("conferenceId", conference.getId());
        manageButton.addActionListener(listener);

        conferencePanel.add(nameLabel);
        conferencePanel.add(dateLabel);
        conferencePanel.add(manageButton);

        return conferencePanel;
    }

    private static JButton createBackButton(ActionListener backAction) {
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

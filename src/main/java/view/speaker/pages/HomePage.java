package view.speaker.pages;

import dto.SessionDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.navigation.Navigator;
import view.event.UIEventMediator;
import view.observers.SessionEventObserver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.List;

public class HomePage extends JPanel {

    private final UserDTO speaker;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;

    // fetched data
    private List<SessionDTO> assignedSessions;

    public HomePage(UserDTO speaker, UIEventMediator eventMediator, Navigator navigator) {
        this.speaker = speaker;
        this.eventMediator = eventMediator;
        this.navigator = navigator;

        setLayout(new BorderLayout());

        fetchAssignedSessions();

        createPageContent();
    }

    private void createPageContent() {
        JPanel headerPanel = createHomePageHeader();
        add(headerPanel, BorderLayout.NORTH);

        // add scrollable container with conferences or display no upcoming conferences message
        if (!assignedSessions.isEmpty()) {
            JScrollPane scrollPane = UIComponentFactory.createSessionsScrollPane(assignedSessions,"View Session", this::handleViewSessionButton);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            add(scrollPane, BorderLayout.CENTER);
        } else {
            JPanel emptyStatePanel = UIComponentFactory.createEmptyStatePanel("It looks like there are no upcoming sessions " +
                    "assigned to you by the organizers. You will be notified as soon as you are expected to speak at a session.", 60);
            add(emptyStatePanel, BorderLayout.CENTER);
        }
    }

    private JPanel createHomePageHeader() {
        // header panel using BorderLayout to position back button and title
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));

        // page title in the center
        JLabel headerLabel = new JLabel("Your Upcoming Sessions");
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton updateBioButton = new JButton("Update Bio");
        updateBioButton.setFocusPainted(false);
        updateBioButton.addActionListener(this::handleUpdateBioButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(this::handleLogoutButton);

        headerPanel.add(Box.createRigidArea(new Dimension(500, 0)));
        headerPanel.add(headerLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(380, 0)));
        headerPanel.add(updateBioButton);
        headerPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        headerPanel.add(logoutButton);

        return headerPanel;
    }

    private void fetchAssignedSessions() {
        eventMediator.publishEvent(
                SessionEventObserver.class,
                observer -> observer.onGetSpeakerSessions(speaker.getId(), this::onSessionsFetched)
        );
    }

    private void onSessionsFetched(List<SessionDTO> sessionDTOs, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        this.assignedSessions = filterUpcomingSessions(sessionDTOs);
    }

    private void handleUpdateBioButton(ActionEvent e) {
        UpdateBioPage updateBioPage = new UpdateBioPage(speaker, eventMediator, navigator);
        navigator.navigateTo(updateBioPage);
    }

    private void handleViewSessionButton(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        String sessionId = (String) button.getClientProperty("sessionId");
        System.out.println("Session ID clicked: " + sessionId);

        ViewAssignedSession viewSessionPage = new ViewAssignedSession(speaker, sessionId, eventMediator, navigator);
        navigator.navigateTo(viewSessionPage);
    }

    private void handleLogoutButton(ActionEvent e) {
        int choice = showConfirmDialog();

        if (choice == JOptionPane.YES_OPTION) {
            navigator.logout();
        }
    }

    private int showConfirmDialog() {
        return JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private List<SessionDTO> filterUpcomingSessions(List<SessionDTO> sessionDTOs) {
        return sessionDTOs.stream()
                .filter(sessionDTO -> LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime()).isAfter(LocalDateTime.now()))
                .toList();
    }
}

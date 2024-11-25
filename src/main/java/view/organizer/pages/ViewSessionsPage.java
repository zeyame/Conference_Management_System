package view.organizer.pages;

import dto.SessionDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ViewSessionsPage {
    private final OrganizerObserver organizerObserver;
    private final String conferenceId;
    private final String conferenceName;
    private final List<SessionDTO> sessions;

    // main panel
    private final JPanel mainContentPanel;

    // buttons
    private final JButton addSessionButton;
    private final JButton backButton;

    public ViewSessionsPage(OrganizerObserver organizerObserver, String conferenceId, String conferenceName, List<SessionDTO> sessions) {
        this.organizerObserver = organizerObserver;
        this.conferenceId = conferenceId;
        this.conferenceName = conferenceName;
        this.sessions = sessions;

        // initializing components
        this.mainContentPanel = new JPanel(new BorderLayout());
        this.addSessionButton = new JButton("Add Session");
        this.backButton = UIComponentFactory.createBackButton(e -> organizerObserver.onNavigateBackRequest());

        // set up listener
        this.addSessionButton.addActionListener(e -> organizerObserver.onAddSessionRequest(this.conferenceId, this.conferenceName));
    }

    public JPanel createPageContent() {
        // creating main components
        JPanel headerPanel = UIComponentFactory
                .createHeaderPanel(String.format("Sessions registered for '%s'", this.conferenceName), backButton);
        JScrollPane scrollPane = createSessionsScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JPanel addSessionButtonPanel = UIComponentFactory.createButtonPanel(addSessionButton);

        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);
        mainContentPanel.add(addSessionButtonPanel, BorderLayout.SOUTH);

        return mainContentPanel;
    }

    private JScrollPane createSessionsScrollPane() {
        JPanel sessionsPanel = new JPanel();
        sessionsPanel.setLayout(new BoxLayout(sessionsPanel, BoxLayout.Y_AXIS));
        sessionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 0));

        for (SessionDTO session : sessions) {
            sessionsPanel.add(createSessionPanel(session));
            sessionsPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add spacing between sessions
        }

        JScrollPane scrollPane = new JScrollPane(sessionsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(7);
        return scrollPane;
    }

    private JPanel createSessionPanel(SessionDTO session) {
        JPanel sessionPanel = new JPanel();
        sessionPanel.setLayout(new BoxLayout(sessionPanel, BoxLayout.Y_AXIS));

        // Session name
        JLabel nameLabel = new JLabel("Name: " + session.getName());
        nameLabel.setFont(new Font("Sans serif", Font.BOLD, 16));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // Session date and time
        JLabel dateLabel = new JLabel("Date: " + session.getDate().toString());
        JLabel timeLabel = new JLabel("Time: " + session.getStartTime() + " - " + session.getEndTime());
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // Manage session button
        JButton manageButton = new JButton("Manage Session");
        manageButton.setFocusPainted(false);
        manageButton.putClientProperty("sessionId", session.getId());
        manageButton.addActionListener(this::handleManageSessionButton);

        // Add components to session panel
        sessionPanel.add(nameLabel);
        sessionPanel.add(dateLabel);
        sessionPanel.add(timeLabel);
        sessionPanel.add(manageButton);

        return sessionPanel;
    }

    private void handleManageSessionButton(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        String sessionId = (String) sourceButton.getClientProperty("sessionId");

        // publish event to organizer observer for manage session click
        organizerObserver.onManageSessionRequest(sessionId);
    }
}

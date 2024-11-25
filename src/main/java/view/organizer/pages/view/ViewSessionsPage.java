package view.organizer.pages.view;

import dto.SessionDTO;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ViewSessionsPage extends ViewListPage<SessionDTO> {
    private final String conferenceId;

    public ViewSessionsPage(OrganizerObserver organizerObserver, String conferenceId, String conferenceName, List<SessionDTO> sessions) {
        super(organizerObserver, sessions, conferenceName);
        this.conferenceId = conferenceId;

        // initializing components
        JButton addSessionButton = new JButton("Add Session");

        // set up listener
        addSessionButton.addActionListener(e -> organizerObserver.onAddSessionRequest(this.conferenceId, this.conferenceName));
    }

    @Override
    protected String getPageTitle() {
        return String.format("Sessions registered for '%s'", this.conferenceName);
    }


    @Override
    protected JScrollPane createItemsScrollPane() {
        JPanel sessionsPanel = new JPanel();
        sessionsPanel.setLayout(new BoxLayout(sessionsPanel, BoxLayout.Y_AXIS));
        sessionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 0));

        for (SessionDTO session : items) {
            sessionsPanel.add(createItemPanel(session));
            sessionsPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add spacing between sessions
        }

        JScrollPane scrollPane = new JScrollPane(sessionsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(7);
        return scrollPane;
    }

    @Override
    protected JPanel createItemPanel(SessionDTO session) {
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

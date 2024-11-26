package view.organizer.pages.view;

import dto.SessionDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ViewSessionsPage extends ViewListPage<SessionDTO> {
    private final String conferenceId;
    private final JButton addSessionButton;

    public ViewSessionsPage(OrganizerObserver organizerObserver, String conferenceId, String eventName, List<SessionDTO> sessions) {
        super(organizerObserver, eventName, sessions);
        this.conferenceId = conferenceId;

        // initializing components
        addSessionButton = new JButton("Add Session");

        // set up listener
        addSessionButton.addActionListener(e -> organizerObserver.onAddSessionRequest(this.conferenceId, this.eventName));
    }

    @Override
    public JPanel createPageContent() {
        JPanel mainContentPanel = super.createPageContent();
        JPanel addSessionButtonPanel = UIComponentFactory.createButtonPanel(addSessionButton);
        mainContentPanel.add(addSessionButtonPanel, BorderLayout.SOUTH);
        return mainContentPanel;
    }

    @Override
    protected String getPageTitle() {
        return String.format("Sessions registered for '%s'", this.eventName);
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

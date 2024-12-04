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
        addSessionButton.addActionListener(e -> organizerObserver.onAddSessionRequest(this.conferenceId));
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
        return UIComponentFactory.createSessionPanel(session, this::handleManageSessionButton, "Manage Session");
    }

    private void handleManageSessionButton(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        String sessionId = (String) sourceButton.getClientProperty("sessionId");

        // publish event to organizer observer for manage session click
        organizerObserver.onManageSessionRequest(sessionId);
    }
}

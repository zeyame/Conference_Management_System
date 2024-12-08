package view.organizer.pages.view;

import dto.SessionDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewSessionsPage extends ViewListPage<SessionDTO> {
    private final String conferenceId;
    private final JButton addSessionButton;

    private Map<String, SessionDTO> upcomingSessions = new HashMap<>();
    private Map<String, SessionDTO> pastSessions = new HashMap<>();

    public ViewSessionsPage(OrganizerObserver organizerObserver, String conferenceId, String eventName, List<SessionDTO> sessions) {
        super(organizerObserver, eventName, sessions);
        this.conferenceId = conferenceId;

        // initializing components
        addSessionButton = new JButton("Add Session");

        // filter sessions
        filterUpcomingSessions(sessions);
        filterPastSessions(sessions);

        // set up listener
        addSessionButton.addActionListener(e -> organizerObserver.onAddSessionRequest(this.conferenceId));
    }

    @Override
    public JPanel createPageContent() {
        // header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(String.format("Sessions in '%s'", eventName), e -> organizerObserver.onNavigateBackRequest(), 400);
        mainContentPanel.add(headerPanel, BorderLayout.NORTH);

        // split panel for registered and unregistered sessions
        if (upcomingSessions.isEmpty() && pastSessions.isEmpty()) {
            JPanel emptyStatePanel = UIComponentFactory.createEmptyStatePanel(getEmptyItemsMessage(), 250);
            emptyStatePanel.setBorder(BorderFactory.createEmptyBorder(270, 200, 0, 0));
            mainContentPanel.add(emptyStatePanel, BorderLayout.CENTER);
        } else {
            JSplitPane splitPane = UIComponentFactory.createSplitPane(
                    "Upcoming",
                    "Past",
                    upcomingSessions.values().stream().toList(),
                    pastSessions.values().stream().toList(),
                    "Manage Session",
                    this::handleManageSessionButton
            );
            splitPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            mainContentPanel.add(splitPane, BorderLayout.CENTER);
        }

        JPanel buttonPanel = UIComponentFactory.createButtonPanel(addSessionButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 50));
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainContentPanel;
    }

    @Override
    protected String getPageTitle() {
        return String.format("Sessions registered for '%s'", this.eventName);
    }

    @Override
    protected String getEmptyItemsMessage() {
        return "There are currently no sessions for this conference. You can add sessions to get started.";
    }
    @Override
    protected JPanel createItemPanel(SessionDTO session) {
        return new JPanel();
    }

    private void filterUpcomingSessions(List<SessionDTO> sessionDTOS) {
        sessionDTOS.stream()
                .filter(this::isUpcoming)
                .forEach(sessionDTO -> upcomingSessions.put(sessionDTO.getId(), sessionDTO));
    }

    private void filterPastSessions(List<SessionDTO> sessionDTOS) {
        sessionDTOS.stream()
                .filter(sessionDTO -> !isUpcoming(sessionDTO))
                .forEach(sessionDTO -> pastSessions.put(sessionDTO.getId(), sessionDTO));
    }

    private boolean isUpcoming(SessionDTO sessionDTO) {
        return LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime()).isAfter(LocalDateTime.now());
    }

    private void handleManageSessionButton(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        String sessionId = (String) sourceButton.getClientProperty("sessionId");

        organizerObserver.onManageUpcomingSessionRequest(sessionId);
    }
}

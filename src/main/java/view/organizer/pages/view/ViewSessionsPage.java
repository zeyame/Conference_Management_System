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
        JSplitPane splitPane = createSplitPane();
        splitPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        mainContentPanel.add(splitPane, BorderLayout.CENTER);

        return mainContentPanel;
    }

    private JSplitPane createSplitPane() {
        // create panels for ongoing and upcoming conferences
        JPanel registeredPanel = createSessionPanel("Upcoming", upcomingSessions.values().stream().toList());
        JPanel unregisteredPanel = createSessionPanel("Past", pastSessions.values().stream().toList());

        Dimension equalSize = new Dimension(400, 0);
        registeredPanel.setPreferredSize(equalSize);
        unregisteredPanel.setPreferredSize(equalSize);

        // create a split pane with the two panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, registeredPanel, unregisteredPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(5);
        return splitPane;
    }

    private JPanel createSessionPanel(String title, List<SessionDTO> sessionDTOs) {
        JPanel sessionsPanel = new JPanel();
        sessionsPanel.setLayout(new BorderLayout());

        // header label
        JLabel headerLabel = new JLabel(title, SwingConstants.CENTER);
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        sessionsPanel.add(headerLabel, BorderLayout.NORTH);

        // Scrollable list of conferences
        JScrollPane scrollPane = createSessionsScrollPane(sessionDTOs);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        sessionsPanel.add(scrollPane, BorderLayout.CENTER);

        return sessionsPanel;
    }

    private JScrollPane createSessionsScrollPane(List<SessionDTO> sessionDTOs) {
        JPanel sessionsPanel = new JPanel();
        sessionsPanel.setLayout(new BoxLayout(sessionsPanel, BoxLayout.Y_AXIS));
        sessionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 0));

        for (SessionDTO sessionDTO : sessionDTOs) {
            sessionsPanel.add(UIComponentFactory.createSessionPanel(sessionDTO, this::handleManageSessionButton, "Manage Session"));
            sessionsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JScrollPane scrollPane = new JScrollPane(sessionsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(7);
        return scrollPane;
    }


    @Override
    protected String getPageTitle() {
        return String.format("Sessions registered for '%s'", this.eventName);
    }

    @Override
    protected JPanel createItemPanel(SessionDTO session) {
        return UIComponentFactory.createSessionPanel(session, this::handleManageSessionButton, "Manage Session");
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

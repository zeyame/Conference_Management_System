package view.attendee.pages;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.DataCallback.ViewRegisteredConferencesCallback;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.observers.ConferenceEventObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ViewRegisteredConferences extends JPanel implements ViewRegisteredConferencesCallback {

    private final UserDTO attendee;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;
    private final JButton backButton;
    private List<ConferenceDTO> ongoingConferences;
    private List<ConferenceDTO> upcomingConferences;

    public ViewRegisteredConferences(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator) {
        this.attendee = attendee;
        this.eventMediator = eventMediator;
        this.navigator = navigator;
        this.backButton = UIComponentFactory.createBackButton(e -> navigator.navigateBack());

        // publish an event to fetch registered conferences for attendee
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onGetRegisteredConferences(attendee.getId(), this)
        );

        createPageContent();
    }

    private void createPageContent() {
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel("Your Registered Conferences", backButton, 480);
        add(headerPanel, BorderLayout.NORTH);

        // Split panel for ongoing and upcoming conferences
        JSplitPane splitPane = createSplitPane();
        splitPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(splitPane, BorderLayout.CENTER);
    }

    private JSplitPane createSplitPane() {
        // create panels for ongoing and upcoming conferences
        JPanel ongoingPanel = createConferencePanel("Ongoing", ongoingConferences);
        JPanel upcomingPanel = createConferencePanel("Upcoming", upcomingConferences);

        // create a split pane with the two panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ongoingPanel, upcomingPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(5);
        return splitPane;
    }

    private JPanel createConferencePanel(String title, List<ConferenceDTO> conferences) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // header label
        JLabel headerLabel = new JLabel(title, SwingConstants.CENTER);
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(headerLabel, BorderLayout.NORTH);

        // Scrollable list of conferences
        JScrollPane scrollPane = UIComponentFactory.createConferenceScrollPane(conferences, this::handleViewConferenceButton, "View Conference");
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 10, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    @Override
    public void onRegisteredConferencesFetched(List<ConferenceDTO> registeredConferences) {
        // split the fetched registered conferences into ongoing and upcoming
        ongoingConferences = filterOngoingConferences(registeredConferences);
        upcomingConferences = filterUpcomingConferences(registeredConferences);
    }

    @Override
    public void onError(String errorMessage) {
        showError(errorMessage);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handleViewConferenceButton(ActionEvent e) {
        // Handle viewing a single conference
    }

    private List<ConferenceDTO> filterUpcomingConferences(List<ConferenceDTO> conferences) {
        LocalDateTime now = LocalDateTime.now();

        return conferences.stream()
                .filter(conference -> LocalDateTime.of(conference.getStartDate(), LocalTime.MIN).isAfter(now))
                .toList();
    }

    private List<ConferenceDTO> filterOngoingConferences(List<ConferenceDTO> conferences) {
        LocalDateTime now = LocalDateTime.now();

        return conferences.stream()
                .filter(conference -> {
                    LocalDateTime startDateTime = LocalDateTime.of(conference.getStartDate(), LocalTime.MIN);
                    LocalDateTime endDateTime = LocalDateTime.of(conference.getEndDate(), LocalTime.MAX);

                    return now.isAfter(startDateTime) && now.isBefore(endDateTime);
                }).toList();
    }
}
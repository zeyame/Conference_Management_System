package view.attendee.pages;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.DataCallback.HomePageDataCallback;
import view.attendee.UIEventMediator;
import view.attendee.observers.ConferenceEventObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class HomePage extends JPanel implements HomePageDataCallback {

    private final UserDTO attendee;
    private final UIEventMediator eventMediator;
    private final JButton viewRegisteredConferences;
    private List<ConferenceDTO> upcomingConferences;

    public HomePage(UserDTO userDTO, UIEventMediator eventMediator) {
        this.attendee = userDTO;
        this.eventMediator = eventMediator;
        this.viewRegisteredConferences = new JButton("Your Registered Conferences");

        // publishing an event to fetch upcoming conferences
        this.eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onGetUpcomingConferences(attendee.getId(), this)
        );

        createPageContent();
    }

    @Override
    public void onUpcomingConferencesFetched(List<ConferenceDTO> conferenceDTOs) {
        this.upcomingConferences = conferenceDTOs;
    }

    @Override
    public void onError(String errorMessage) {
        showError(errorMessage);
    }

    public void createPageContent() {
        setLayout(new BorderLayout());

        // add header with back button
        add(createHomePageHeader(), BorderLayout.NORTH);

        // add scrollable container with conferences
        JScrollPane scrollPane = createConferenceScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);

        // "View Registered Conferences" button
        JPanel viewRegisteredConferencesPanel = UIComponentFactory.createButtonPanel(viewRegisteredConferences);
        add(viewRegisteredConferencesPanel, BorderLayout.SOUTH);
    }


    private JPanel createHomePageHeader() {
        // header panel using BorderLayout to position back button and title
        JPanel headerPanel = new JPanel(new BorderLayout());

        // page title in the center
        JLabel headerLabel = new JLabel("Upcoming Conferences");
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        return headerPanel;
    }


    private JScrollPane createConferenceScrollPane() {
        JPanel conferencesPanel = new JPanel();
        conferencesPanel.setLayout(new BoxLayout(conferencesPanel, BoxLayout.Y_AXIS));
        conferencesPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 0));

        for (ConferenceDTO conferenceDTO : upcomingConferences) {
            conferencesPanel.add(UIComponentFactory.createConferencePanel(conferenceDTO, this::handleViewConferenceButton, "View Conference"));
            conferencesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JScrollPane scrollPane = new JScrollPane(conferencesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(7);
        return scrollPane;
    }

    private void handleViewConferenceButton(ActionEvent e) {

    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

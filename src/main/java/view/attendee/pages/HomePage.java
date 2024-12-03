package view.attendee.pages;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.DataCallback.HomePageDataCallback;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.observers.ConferenceEventObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class HomePage extends JPanel implements HomePageDataCallback {

    private final UserDTO attendee;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;

    private final JButton viewRegisteredConferences;
    private List<ConferenceDTO> upcomingConferences;

    public HomePage(UserDTO userDTO, UIEventMediator eventMediator, Navigator navigator) {
        this.attendee = userDTO;
        this.eventMediator = eventMediator;
        this.navigator = navigator;

        // set up view registered conferences button
        this.viewRegisteredConferences = new JButton("Your Registered Conferences");
        this.viewRegisteredConferences.addActionListener(this::handleViewRegisteredConferences);

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
        JScrollPane scrollPane = UIComponentFactory.createConferenceScrollPane(upcomingConferences, this::handleViewConferenceButton, "View Conference");
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

    private void handleViewConferenceButton(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        String conferenceId = (String) sourceButton.getClientProperty("conferenceId");

        ViewUpcomingConferencePage viewUpcomingConferencePage = new ViewUpcomingConferencePage(this.attendee, conferenceId, this.eventMediator, this.navigator);
        navigator.navigateTo(viewUpcomingConferencePage);
    }

    private void handleViewRegisteredConferences(ActionEvent e) {
        // navigate to View Registered Conferences Page
        ViewRegisteredConferences viewRegisteredConferencesPage = new ViewRegisteredConferences(attendee, this.eventMediator, this.navigator);
        navigator.navigateTo(viewRegisteredConferencesPage);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

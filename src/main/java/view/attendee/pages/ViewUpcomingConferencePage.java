package view.attendee.pages;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.LoggerUtil;
import util.ui.UIComponentFactory;
import view.attendee.DataCallback.ViewUpcomingConferenceDataCallback;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.observers.ConferenceEventObserver;

import javax.swing.*;
import java.awt.*;

public class ViewUpcomingConferencePage extends JPanel implements ViewUpcomingConferenceDataCallback {

    // dependencies
    private final UserDTO attendee;
    private final String conferenceId;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;

    // fetched data
    private ConferenceDTO upcomingConference;
    private String organizerName;

    public ViewUpcomingConferencePage(UserDTO attendee, String conferenceId, UIEventMediator eventMediator, Navigator navigator) {
        this.attendee = attendee;
        this.conferenceId = conferenceId;
        this.eventMediator = eventMediator;
        this.navigator = navigator;

        // publish event to fetch conference data
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onConferenceSelected(conferenceId, this)
        );

        createPageContent();
    }

    private void createPageContent() {
        setLayout(new BorderLayout());

        // header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(upcomingConference.getName(), e -> navigator.navigateBack(), 490);
        add(headerPanel, BorderLayout.NORTH);

        // conference details panel
        JPanel conferenceDetailsPanel = UIComponentFactory.createConferenceDetailsPanel(upcomingConference, organizerName);
        conferenceDetailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 250, 50, 0));
        add(conferenceDetailsPanel, BorderLayout.CENTER);

        // register button
        JButton registerButton = UIComponentFactory.createStyledButton("Register");
        JPanel registerButtonPanel = UIComponentFactory.createButtonPanel(registerButton);
        registerButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 30));
        add(registerButtonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void onConferenceFetched(ConferenceDTO conferenceDTO) {
        LoggerUtil.getInstance().logInfo(String.format("Conference data received: %s", conferenceDTO.getName()));
        this.upcomingConference = conferenceDTO;

        // publish event to mediator to get conference organizer name
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onGetOrganizerName(conferenceDTO.getOrganizerId(), this)
        );
    }

    @Override
    public void onOrganizerNameFetched(String organizerName) {
        LoggerUtil.getInstance().logInfo(String.format("Organizer name received: %s", organizerName));

        this.organizerName = organizerName;
    }

    @Override
    public void onError(String errorMessage) {
        showError(errorMessage);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

}

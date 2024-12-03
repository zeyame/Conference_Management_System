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
import java.util.List;

public class ViewRegisteredConferences extends JPanel implements ViewRegisteredConferencesCallback {

    private final UserDTO attendee;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;
    private final JButton backButton;
    private List<ConferenceDTO> registeredConferences;

    public ViewRegisteredConferences(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator) {
        this.attendee = attendee;
        this.eventMediator = eventMediator;
        this.navigator = navigator;
        this.backButton = UIComponentFactory.createBackButton(e -> navigator.navigateBack());

        // publish event to fetch registered conferences for attendee
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onGetRegisteredConferences(attendee.getId(), this)
        );

        createPageContent();
    }

    public void createPageContent() {
        setLayout(new BorderLayout());

        JPanel headerPanel = UIComponentFactory.createHeaderPanel("Your Registered Conferences", backButton);
        add(headerPanel, BorderLayout.NORTH);

        JScrollPane conferencesScrollPane = UIComponentFactory.createConferenceScrollPane(registeredConferences, this::handleViewConferenceButton, "View Conference");
        conferencesScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(conferencesScrollPane, BorderLayout.CENTER);

    }

    @Override
    public void onRegisteredConferencesFetched(List<ConferenceDTO> registeredConferences) {
        this.registeredConferences = registeredConferences;
    }

    @Override
    public void onError(String errorMessage) {
        showError(errorMessage);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handleViewConferenceButton(ActionEvent e) {

    }

}

package view.attendee.pages.view.conference;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.navigation.Navigator;
import view.event.UIEventMediator;
import view.observers.ConferenceEventObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class ViewConferencePage extends JPanel {
    // dependencies
    protected final UserDTO attendee;
    protected final UIEventMediator eventMediator;
    protected final Navigator navigator;
    protected final String conferenceId;

    // fetched data
    protected ConferenceDTO conferenceDTO;
    protected String organizerName;

    public ViewConferencePage(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator, String conferenceId) {
        this.attendee = attendee;
        this.eventMediator = eventMediator;
        this.navigator = navigator;
        this.conferenceId = conferenceId;

        setLayout(new BorderLayout());

        fetchConference();
        fetchOrganizerName();
    }

    protected void createPageContent() {
        // header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(conferenceDTO.getName(), this::handleBackButton, 490);
        add(headerPanel, BorderLayout.NORTH);

        // conference details panel
        JPanel conferenceDetailsPanel = UIComponentFactory.createConferenceDetailsPanel(conferenceDTO, organizerName, 150);
        conferenceDetailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 250, 50, 0));
        add(conferenceDetailsPanel, BorderLayout.CENTER);

        // footer panel
        add(getFooterPanel(), BorderLayout.SOUTH);
    }

    protected abstract JPanel getFooterPanel();

    // data fetchers
    private void fetchConference() {
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onConferenceSelected(conferenceId, this::onConferenceFetched)
        );
    }

    private void fetchOrganizerName() {
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onGetOrganizerName(conferenceDTO.getOrganizerId(), this::onOrganizerNameFetched)
        );
    }

    // event responders
    private void onConferenceFetched(ConferenceDTO conferenceDTO, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        this.conferenceDTO = conferenceDTO;
    }

    private void onOrganizerNameFetched(String organizerName, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        this.organizerName = organizerName;
    }

    // Joption Pane helpers
    protected void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void handleBackButton(ActionEvent e) {
        ViewRegisteredConferencesPage viewRegisteredConferencesPage = new ViewRegisteredConferencesPage(attendee, eventMediator, navigator);
        navigator.navigateTo(viewRegisteredConferencesPage, false);
    }

}

package view.attendee.pages;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.observers.ConferenceEventObserver;

import javax.swing.*;
import java.awt.*;

public class ViewRegisteredConferencePage extends JPanel {
    private final UserDTO attendee;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;
    private final String conferenceId;
    private ConferenceDTO registeredConference;
    private String organizerName;

    // buttons
    private final JButton leaveConferenceButton;
    private final JButton viewSessionsButton;
    private final JButton viewSpeakersButton;
    private final JButton provideFeedbackButton;

    public ViewRegisteredConferencePage(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator, String conferenceId) {
        this.attendee = attendee;
        this.eventMediator = eventMediator;
        this.navigator = navigator;
        this.conferenceId = conferenceId;

        // initialize buttons
        this.leaveConferenceButton = UIComponentFactory.createStyledButton("Leave Conference");
        this.viewSessionsButton = UIComponentFactory.createStyledButton("View Sessions");
        this.viewSpeakersButton = UIComponentFactory.createStyledButton("View Speakers");
        this.provideFeedbackButton = UIComponentFactory.createStyledButton("Provide Feedback");

        fetchRegisteredConference();

        createPageContent();
    }

    private void createPageContent() {
        setLayout(new BorderLayout());

        // header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(registeredConference.getName(), e -> navigator.navigateBack(), 400);
        headerPanel.add(Box.createRigidArea(new Dimension(480, 0)));
        headerPanel.add(leaveConferenceButton);
        add(headerPanel, BorderLayout.NORTH);

        // details panel
        JPanel conferenceDetails = UIComponentFactory.createConferenceDetailsPanel(registeredConference, organizerName);
        conferenceDetails.setBorder(BorderFactory.createEmptyBorder(0, 30, 40, 0));
        add(conferenceDetails, BorderLayout.CENTER);

        // footer panel
        JPanel footerPanel = createFooterPanel();
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 100));
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        footerPanel.add(viewSessionsButton);
        footerPanel.add(viewSpeakersButton);
        footerPanel.add(provideFeedbackButton);

        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        return footerPanel;
    }

    // event responders
    private void onGetRegisteredConference(ConferenceDTO conferenceDTO, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        this.registeredConference = conferenceDTO;

        // get organizer name of conference
        fetchOrganizerName();
    }

    private void onGetOrganizerName(String organizerName, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        this.organizerName = organizerName;
    }


    // data fetchers
    private void fetchRegisteredConference() {
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onConferenceSelected(conferenceId, this::onGetRegisteredConference)
        );
    }

    private void fetchOrganizerName() {
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onGetOrganizerName(registeredConference.getOrganizerId(), this::onGetOrganizerName)
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

}

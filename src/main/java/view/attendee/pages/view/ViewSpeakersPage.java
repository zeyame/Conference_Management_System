package view.attendee.pages.view;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.navigation.Navigator;
import view.event.UIEventMediator;
import view.observers.ConferenceEventObserver;
import view.attendee.pages.form.ProvideFeedbackPage;
import view.attendee.pages.form.ProvideSpeakerFeedbackPage;
import view.attendee.pages.view.conference.ViewConferencePage;
import view.attendee.pages.view.conference.ViewRegisteredConferencePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ViewSpeakersPage extends JPanel {
    private final UserDTO attendee;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;
    private final String conferenceId;

    // fetched data
    private List<UserDTO> speakers;
    private Map<String, String> speakerBios = new HashMap<>();    // K: Speaker Id, V: Speaker Bio
    private ConferenceDTO conferenceDTO;

    public ViewSpeakersPage(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator, String conferenceId) {
        this.attendee = attendee;
        this.eventMediator = eventMediator;
        this.navigator = navigator;
        this.conferenceId = conferenceId;

        setLayout(new BorderLayout());

        fetchSpeakers();
        fetchSpeakerBios();
        fetchConference();

        createPageContent();
    }

    private void createPageContent() {
        // header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(String.format("Speakers expected to speak at '%s'", conferenceDTO.getName()), this::handleBackButton, 300);
        add(headerPanel, BorderLayout.NORTH);

        // speakers scroll pane
        if (speakers.isEmpty()) {
            JPanel emptyStatePanel = UIComponentFactory.createEmptyStatePanel("No speakers have been assigned to speak at this conference yet.", 200);
            add(emptyStatePanel, BorderLayout.CENTER);
        } else {
            JScrollPane scrollPane = createSpeakersScrollPane();
            add(scrollPane, BorderLayout.CENTER);
        }
    }

    private JScrollPane createSpeakersScrollPane() {
        JPanel speakersPanel = new JPanel();
        speakersPanel.setLayout(new BoxLayout(speakersPanel, BoxLayout.Y_AXIS));
        speakersPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 0));

        for (UserDTO speaker: speakers) {
            JPanel speakerPanel = UIComponentFactory.createSpeakerPanel(speaker, speakerBios.get(speaker.getId()), "Provide Feedback", this::handleProvideFeedbackButton);
            speakersPanel.add(speakerPanel);
        }
        JScrollPane scrollPane = new JScrollPane(speakersPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(7);
        return scrollPane;
    }

    // data fetchers
    private void fetchSpeakers() {
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onGetSpeakers(conferenceId, this::onSpeakersFetched)
        );
    }

    private void fetchSpeakerBios() {
        eventMediator.publishEvent(
            ConferenceEventObserver.class,
            observer -> observer.onGetSpeakerBios(getSpeakerIds(), this::onSpeakerBiosFetched)
        );
    }

    private void fetchConference() {
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onConferenceSelected(conferenceId, this::onConferenceFetched)
        );
    }


    // callback responders
    private void onSpeakersFetched(List<UserDTO> speakers, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        this.speakers = speakers;
    }

    private void onSpeakerBiosFetched(Map<String, String> speakerIdsToBios, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        this.speakerBios = speakerIdsToBios;
    }

    private void onConferenceFetched(ConferenceDTO conferenceDTO, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        this.conferenceDTO = conferenceDTO;
    }

    // button handlers
    private void handleBackButton(ActionEvent e) {
        ViewConferencePage viewConferencePage = new ViewRegisteredConferencePage(attendee, eventMediator, navigator, conferenceId);
        navigator.navigateTo(viewConferencePage);
    }

    private void handleProvideFeedbackButton(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        UserDTO speaker = (UserDTO) sourceButton.getClientProperty("speaker");

        ProvideFeedbackPage provideFeedbackPage = new ProvideSpeakerFeedbackPage(attendee, eventMediator, navigator, speaker.getId(), speaker.getName(), conferenceId);
        navigator.navigateTo(provideFeedbackPage);
    }

    // helpers
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private Set<String> getSpeakerIds() {
        return this.speakers.stream()
                .map(UserDTO::getId)
                .collect(Collectors.toSet());
    }
}

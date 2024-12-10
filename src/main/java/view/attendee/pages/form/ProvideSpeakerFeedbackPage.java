package view.attendee.pages.form;

import domain.model.feedback.FeedbackType;
import dto.FeedbackDTO;
import dto.UserDTO;
import view.navigation.Navigator;
import view.event.UIEventMediator;
import view.observers.ConferenceEventObserver;
import view.attendee.pages.view.conference.ViewConferencePage;
import view.attendee.pages.view.conference.ViewRegisteredConferencePage;
import java.awt.event.ActionEvent;

public class ProvideSpeakerFeedbackPage extends ProvideFeedbackPage {

    private final String speakerId;
    private final String speakerName;
    private final String conferenceId;

    public ProvideSpeakerFeedbackPage(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator, String speakerId, String speakerName, String conferenceId) {
        super(attendee, eventMediator, navigator);
        this.speakerId = speakerId;
        this.speakerName = speakerName;
        this.conferenceId = conferenceId;

        createPageContent();
    }

    @Override
    protected String getFeedbackTitle() {
        return String.format("Provide feedback for speaker '%s'", speakerName);
    }


    @Override
    protected void handleBackAction(ActionEvent e) {
        ViewConferencePage viewConferencePage = new ViewRegisteredConferencePage(attendee, eventMediator, navigator, conferenceId);
        navigator.navigateTo(viewConferencePage);
    }

    @Override
    protected void handleSubmitAction(ActionEvent e) {
        validateFeedbackForm();

        String comment = commentTextArea.getText();
        Integer rating = (Integer) ratingDropdown.getSelectedItem();

        FeedbackDTO feedbackDTO = new FeedbackDTO(attendee.getId(), attendee.getName(), rating, comment, FeedbackType.SPEAKER);
        feedbackDTO.setSpeakerId(speakerId);

        // publish event to submit session feedback
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onSubmitFeedback(feedbackDTO, this::onFeedbackSubmitted)
        );
    }
}

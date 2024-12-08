package view.attendee.pages.form;

import domain.model.feedback.FeedbackType;
import dto.ConferenceDTO;
import dto.FeedbackDTO;
import dto.UserDTO;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.observers.ConferenceEventObserver;
import view.attendee.pages.view.conference.ViewConferencePage;
import view.attendee.pages.view.conference.ViewRegisteredConferencePage;

import java.awt.event.ActionEvent;

public class ProvideConferenceFeedbackPage extends ProvideFeedbackPage {
    private final ConferenceDTO conferenceDTO;
    public ProvideConferenceFeedbackPage(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator, ConferenceDTO conferenceDTO) {
        super(attendee, eventMediator, navigator);
        this.conferenceDTO = conferenceDTO;

        createPageContent();
    }

    @Override
    protected String getFeedbackTitle() {
        return String.format("Provide feedback for the Conference '%s'", conferenceDTO.getName());
    }

    @Override
    protected void handleBackAction(ActionEvent e) {
        ViewConferencePage viewConferencePage = new ViewRegisteredConferencePage(attendee, eventMediator, navigator, conferenceDTO.getId());
        navigator.navigateTo(viewConferencePage, false);
    }

    @Override
    protected void handleSubmitAction(ActionEvent e) {
        validateFeedbackForm();

        String comment = commentTextArea.getText();
        Integer rating = (Integer) ratingDropdown.getSelectedItem();


        FeedbackDTO feedbackDTO = new FeedbackDTO(attendee.getId(), attendee.getName(), rating, comment, FeedbackType.CONFERENCE);
        feedbackDTO.setConferenceId(conferenceDTO.getId());

        // publish event to submit session feedback
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onSubmitFeedback(feedbackDTO, this::onFeedbackSubmitted)
        );
    }
}

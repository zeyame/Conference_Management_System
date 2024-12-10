package view.attendee.pages.form;

import domain.model.feedback.FeedbackType;
import dto.FeedbackDTO;
import dto.SessionDTO;
import dto.UserDTO;
import view.navigation.Navigator;
import view.event.UIEventMediator;
import view.observers.SessionEventObserver;
import view.attendee.pages.view.session.ViewPastRegisteredSessionPage;
import view.attendee.pages.view.session.ViewSessionPage;

import java.awt.event.ActionEvent;

public class ProvideSessionFeedbackPage extends ProvideFeedbackPage {
    private final SessionDTO sessionDTO;
    public ProvideSessionFeedbackPage(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator, SessionDTO sessionDTO) {
        super(attendee, eventMediator, navigator);
        this.sessionDTO = sessionDTO;

        createPageContent();
    }


    @Override
    protected String getFeedbackTitle() {
        return String.format("Provide feedback for the session '%s'", sessionDTO.getName());
    }

    @Override
    protected void handleBackAction(ActionEvent e) {
        ViewSessionPage viewSessionPage = new ViewPastRegisteredSessionPage(attendee, sessionDTO.getId(), eventMediator, navigator);
        navigator.navigateTo(viewSessionPage, false);
    }

    @Override
    protected void handleSubmitAction(ActionEvent e) {
        validateFeedbackForm();

        String comment = commentTextArea.getText();
        Integer rating = (Integer) ratingDropdown.getSelectedItem();

        FeedbackDTO feedbackDTO = new FeedbackDTO(attendee.getId(), attendee.getName(), rating, comment, FeedbackType.SESSION);
        feedbackDTO.setSessionId(sessionDTO.getId());

        // publish event to submit session feedback
        eventMediator.publishEvent(
                SessionEventObserver.class,
                observer -> observer.onSubmitFeedback(feedbackDTO, this::onFeedbackSubmitted)
        );
    }
}

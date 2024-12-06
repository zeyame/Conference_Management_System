package view.attendee.pages.form;

import domain.model.FeedbackType;
import dto.FeedbackDTO;
import dto.SessionDTO;
import dto.UserDTO;
import util.ui.FormBuilder;
import util.ui.UIComponentFactory;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.observers.SessionEventObserver;
import view.attendee.pages.view.session.ViewUpcomingRegisteredSessionPage;
import view.attendee.pages.view.session.ViewSessionPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FeedbackPage extends JPanel {
    private final UserDTO attendee;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;
    private final SessionDTO sessionDTO;
    private final FeedbackType feedbackType;


    // rating dropdown
    private JComboBox<Integer> ratingDropdown = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

    // text area
    private JTextArea commentTextArea = new JTextArea(5, 20);

    public FeedbackPage(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator, SessionDTO sessionDTO, FeedbackType feedbackType) {
        this.attendee = attendee;
        this.eventMediator = eventMediator;
        this.navigator = navigator;
        this.sessionDTO = sessionDTO;
        this.feedbackType = feedbackType;

        createPageContent();
    }


    private void createPageContent() {
        setLayout(new BorderLayout());

        // header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(String.format("Provide Feedback for %s", sessionDTO.getName()), this::handleBackAction,  400);
        add(headerPanel, BorderLayout.NORTH);

        // form
        JPanel feedbackForm = createFeedbackForm();
        add(feedbackForm, BorderLayout.CENTER);
    }

    private JPanel createFeedbackForm() {
        FormBuilder formBuilder = new FormBuilder(10);

        JScrollPane commentScrollPane = new JScrollPane(commentTextArea);
        commentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        commentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // submit button
        JButton submitButton = UIComponentFactory.createStyledButton("Submit");
        submitButton.addActionListener(this::handleSubmitAction);

        // build form layout
        formBuilder
                .addLabel("Rating (1-5):", 0, 0)
                .addComponent(ratingDropdown, 0, 1)
                .addLabel("Comment:", 1, 0)
                .addComponent(commentScrollPane, 1, 1)
                .addFullWidthComponent(submitButton, 2);

        return formBuilder.build();
    }

    private void handleBackAction(ActionEvent e) {
        ViewSessionPage viewSessionPage = new ViewUpcomingRegisteredSessionPage(attendee, sessionDTO.getId(), eventMediator, navigator);
        navigator.navigateTo(viewSessionPage);
    }

    private void handleSubmitAction(ActionEvent e) {
        String comment = commentTextArea.getText();
        Integer rating = (Integer) ratingDropdown.getSelectedItem();

        // validate input
        if (comment.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please provide a comment before submitting.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
        }

        FeedbackDTO feedbackDTO = new FeedbackDTO(attendee.getId(), attendee.getName(), sessionDTO.getId(), sessionDTO.getConferenceId(), rating, comment, feedbackType);

        // publish event to submit feedback
        if (feedbackType == FeedbackType.SESSION) {
            eventMediator.publishEvent(
                    SessionEventObserver.class,
                    observer -> observer.onSubmitFeedback(feedbackDTO, this::onFeedbackSubmitted)
            );
        } else {
            // publish event to user/speaker observer for a user related event like submitting speaker feedback
        }
    }

    private void onFeedbackSubmitted(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }

        Integer rating = (Integer) ratingDropdown.getSelectedItem();
        String comment = commentTextArea.getText();

        showSuccess(String.format("Feedback submitted successfully!\nRating: %d\nComment: %s", rating, comment));

        // navigate back after successful submission
        handleBackAction(null);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Submission Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }



}

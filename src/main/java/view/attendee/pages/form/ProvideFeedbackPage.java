package view.attendee.pages.form;

import dto.UserDTO;
import util.ui.FormBuilder;
import util.ui.UIComponentFactory;
import view.navigation.Navigator;
import view.event.UIEventMediator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class ProvideFeedbackPage extends JPanel {
    protected final UserDTO attendee;
    protected final UIEventMediator eventMediator;
    protected final Navigator navigator;

    // rating dropdown
    protected JComboBox<Integer> ratingDropdown = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

    // text area
    protected JTextArea commentTextArea = new JTextArea(5, 20);

    public ProvideFeedbackPage(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator) {
        this.attendee = attendee;
        this.eventMediator = eventMediator;
        this.navigator = navigator;
    }

    protected abstract String getFeedbackTitle();

    protected abstract void handleBackAction(ActionEvent e);

    protected abstract void handleSubmitAction(ActionEvent e);

    protected void createPageContent() {
        setLayout(new BorderLayout());

        // header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(String.format(getFeedbackTitle()), this::handleBackAction,  400-getFeedbackTitle().length());
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

    protected void onFeedbackSubmitted(String errorMessage) {
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

    protected void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Submission Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void validateFeedbackForm() {
        String comment = commentTextArea.getText();
        Integer rating = (Integer) ratingDropdown.getSelectedItem();

        if (rating == null) {
            showError("Invalid rating");
        }
        // validate input
        if (comment.trim().isEmpty()) {
            showError("Please provide a comment before submitting.");
        }
    }
}

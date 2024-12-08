package view.organizer.pages.view;

import dto.FeedbackDTO;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewFeedbackPage extends ViewListPage<FeedbackDTO> {

    public ViewFeedbackPage(OrganizerObserver organizerObserver, String eventName, List<FeedbackDTO> feedback) {
        super(organizerObserver, eventName, feedback);
    }

    @Override
    protected String getPageTitle() {
        return String.format("Feedback for '%s'", eventName);
    }

    @Override
    protected String getEmptyItemsMessage() {
        return "\t\t\t\t\t\t\t\tNo feedback has been given so far.";
    }

    @Override
    protected JPanel createItemPanel(FeedbackDTO item) {
        JPanel feedbackPanel = new JPanel();
        feedbackPanel.setLayout(new BoxLayout(feedbackPanel, BoxLayout.Y_AXIS));

        // name of attendee
        JLabel nameLabel = new JLabel("Name: " + item.getAttendeeName());
        nameLabel.setFont(new Font("Sans serif", Font.BOLD, 18));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // comment of feedback
        JLabel commentLabel = new JLabel("Comment: " + item.getComment());
        commentLabel.setFont(new Font("Sans serif", Font.PLAIN, 14));

        // rating of feedback
        JLabel ratingLabel = new JLabel("Rating: " + item.getRating());
        ratingLabel.setFont(new Font("Sans serif", Font.PLAIN, 14));

        feedbackPanel.add(nameLabel);
        feedbackPanel.add(commentLabel);
        feedbackPanel.add(ratingLabel);

        return feedbackPanel;
    }
}

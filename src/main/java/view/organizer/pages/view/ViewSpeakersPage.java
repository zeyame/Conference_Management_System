package view.organizer.pages.view;

import dto.FeedbackDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ViewSpeakersPage extends ViewListPage<UserDTO>  {

    private String clickedSpeakerName;
    private Map<String, String> speakerBios = new HashMap<>();    // K: Speaker Id, V: Speaker Bio

    public ViewSpeakersPage(OrganizerObserver organizerObserver, String eventName, List<UserDTO> speakers) {
        super(organizerObserver, eventName, speakers);
        fetchSpeakerBios();
    }

    @Override
    protected String getPageTitle() {
        return String.format("Speakers assigned to sessions in '%s'", eventName);
    }

    @Override
    protected String getEmptyItemsMessage() {
        return "\t\t\t\t\t\tNo speakers have been assigned to sessions in this conference yet.";
    }

    @Override
    protected JPanel createItemPanel(UserDTO speaker) {
        return UIComponentFactory.createSpeakerPanel(speaker, speakerBios.get(speaker.getId()), "View Feedback", this::handleViewSpeakerFeedbackButton);
    }

    private void handleViewSpeakerFeedbackButton(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        UserDTO speaker = (UserDTO) sourceButton.getClientProperty("speaker");
        clickedSpeakerName = speaker.getName();

        organizerObserver.onViewSpeakerFeedbackRequest(speaker.getId(), speaker.getName());
    }

    private void fetchSpeakerBios() {
        organizerObserver.onGetSpeakerBiosRequest(getSpeakerIds(), this::onSpeakerBiosFetched);
    }

    private void onSpeakerBiosFetched(Map<String, String> speakerBios, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        this.speakerBios = speakerBios;
    }

    private Set<String> getSpeakerIds() {
        return this.items.stream()
                .map(UserDTO::getId)
                .collect(Collectors.toSet());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

}

package view.organizer.pages.view;

import dto.UserDTO;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewSpeakersPage extends ViewListPage<UserDTO>  {

    public ViewSpeakersPage(OrganizerObserver organizerObserver, String eventName, List<UserDTO> speakers) {
        super(organizerObserver, eventName, speakers);
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
        JPanel speakersPanel = new JPanel();
        speakersPanel.setLayout(new BoxLayout(speakersPanel, BoxLayout.Y_AXIS));

        // name of speaker
        JLabel nameLabel = new JLabel("Name: " + speaker.getName());
        nameLabel.setFont(new Font("Sans serif", Font.BOLD, 18));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // email of speaker
        JLabel emailLabel = new JLabel("Email: " + speaker.getEmail());
        emailLabel.setFont(new Font("Sans serif", Font.PLAIN, 14));

        // bio of speaker
//        JLabel bioLabel = new JLabel("Bio: " + speaker.getEmail());
//        bioLabel.setFont(new Font("Sans serif", Font.PLAIN, 14));

        speakersPanel.add(nameLabel);
        speakersPanel.add(emailLabel);
//        speakersPanel.add(bioLabel);

        return speakersPanel;
    }
}

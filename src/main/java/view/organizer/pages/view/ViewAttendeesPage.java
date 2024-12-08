package view.organizer.pages.view;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewAttendeesPage extends ViewListPage<UserDTO> {

    public ViewAttendeesPage(OrganizerObserver organizerObserver, String eventName, List<UserDTO> attendees) {
        super(organizerObserver, eventName, attendees);
    }

    @Override
    protected String getPageTitle() {
        return String.format("Attendees registered for '%s'", this.eventName);
    }

    protected String getEmptyItemsMessage() {return "No attendees have registered for sessions in this conference yet.";}

    @Override
    protected JPanel createItemPanel(UserDTO attendee) {
        JPanel attendeePanel = new JPanel();
        attendeePanel.setLayout(new BoxLayout(attendeePanel, BoxLayout.Y_AXIS));

        // name of attendee
        JLabel nameLabel = new JLabel("Name: " + attendee.getName());
        nameLabel.setFont(new Font("Sans serif", Font.BOLD, 18));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // email of conference
        JLabel emailLabel = new JLabel("Email: " + attendee.getEmail());
        emailLabel.setFont(new Font("Sans serif", Font.PLAIN, 14));

        attendeePanel.add(nameLabel);
        attendeePanel.add(emailLabel);

        return attendeePanel;
    }
}

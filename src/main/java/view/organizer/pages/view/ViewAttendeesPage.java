package view.organizer.pages.view;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewAttendeesPage extends ViewListPage<UserDTO> {

    public ViewAttendeesPage(OrganizerObserver organizerObserver, String conferenceName, List<UserDTO> attendees) {
        super(organizerObserver, attendees, conferenceName);
    }

    @Override
    protected String getPageTitle() {
        return String.format("Attendees registered for '%s'", this.conferenceName);
    }

    @Override
    protected JScrollPane createItemsScrollPane() {
        JPanel attendeesPanel = new JPanel();
        attendeesPanel.setLayout(new BoxLayout(attendeesPanel, BoxLayout.Y_AXIS));

        for (UserDTO attendee: items) {
            attendeesPanel.add(createItemPanel(attendee));
            attendeesPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        }

        JScrollPane attendeesScrollPane = new JScrollPane(attendeesPanel);
        attendeesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        attendeesScrollPane.getVerticalScrollBar().setUnitIncrement(7);
        attendeesScrollPane.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 0));

        return attendeesScrollPane;
    }

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

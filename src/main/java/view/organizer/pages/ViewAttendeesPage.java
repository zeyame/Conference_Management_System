package view.organizer.pages;

import dto.UserDTO;
import util.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewAttendeesPage {
    private final List<UserDTO> attendees;
    private final String conferenceName;

    // main panel
    private final JPanel mainContentPanel;

    // button
    private final JButton backButton;

    public ViewAttendeesPage(OrganizerObserver organizerObserver, List<UserDTO> attendees, String conferenceName) {
        // dependencies
        this.attendees = attendees;
        this.conferenceName = conferenceName;

        // initializing components
        this.mainContentPanel = new JPanel(new BorderLayout());
        this.backButton = UIComponentFactory.createBackButton(e -> organizerObserver.onNavigateBackRequest());
    }

    public JPanel createPageContent() {
        // refresh page
        mainContentPanel.removeAll();

        // creating main components
        JPanel headerPanel = UIComponentFactory
                .createHeaderPanel("Attendees registered for conference '" + conferenceName + "'", backButton);
        JScrollPane attendeesScrollPane = createAttendeesScrollPane();

        // adding components to main content panel
        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(Box.createRigidArea(new Dimension(40, 0)));
        mainContentPanel.add(attendeesScrollPane, BorderLayout.CENTER);

        return mainContentPanel;
    }

    private JScrollPane createAttendeesScrollPane() {
        JPanel attendeesPanel = new JPanel();
        attendeesPanel.setLayout(new BoxLayout(attendeesPanel, BoxLayout.Y_AXIS));

        for (UserDTO attendee: attendees) {
            attendeesPanel.add(createAttendeePanel(attendee.getName(), attendee.getEmail()));
            attendeesPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        }

        JScrollPane attendeesScrollPane = new JScrollPane(attendeesPanel);
        attendeesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        attendeesScrollPane.getVerticalScrollBar().setUnitIncrement(7);
        attendeesScrollPane.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 0));

        return attendeesScrollPane;
    }

    private JPanel createAttendeePanel(String attendeeName, String attendeeEmail) {
        JPanel attendeePanel = new JPanel();
        attendeePanel.setLayout(new BoxLayout(attendeePanel, BoxLayout.Y_AXIS));

        // name of attendee
        JLabel nameLabel = new JLabel("Name: " + attendeeName);
        nameLabel.setFont(new Font("Sans serif", Font.BOLD, 18));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // email of conference
        JLabel emailLabel = new JLabel("Email: " + attendeeEmail);
        emailLabel.setFont(new Font("Sans serif", Font.PLAIN, 14));

        attendeePanel.add(nameLabel);
        attendeePanel.add(emailLabel);

        return attendeePanel;
    }
}

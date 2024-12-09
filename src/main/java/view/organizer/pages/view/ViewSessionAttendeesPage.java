package view.organizer.pages.view;

import dto.SessionDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ViewSessionAttendeesPage extends ViewListPage<UserDTO> {

    private final SessionDTO sessionDTO;
    public ViewSessionAttendeesPage(OrganizerObserver organizerObserver, List<UserDTO> attendees, SessionDTO sessionDTO) {
        super(organizerObserver, sessionDTO.getName(), attendees);
        this.sessionDTO = sessionDTO;
    }

    @Override
    protected String getPageTitle() {
        return String.format("Attendees registered for '%s'", this.eventName);
    }

    protected String getEmptyItemsMessage() {return "No attendees have registered for sessions in this conference yet.";}

    @Override
    protected JPanel createItemPanel(UserDTO attendee) {
        JPanel attendeePanel = UIComponentFactory.createAttendeePanel(attendee.getName(), attendee.getEmail());

        // Create the checkbox to mark attendance
        JCheckBox attendanceCheckBox = new JCheckBox("Mark as Attended");
        attendanceCheckBox.setFont(new Font("Sans serif", Font.PLAIN, 14));
        attendanceCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        attendanceCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));

        // Set the attendeeId as a client property
        attendanceCheckBox.putClientProperty("attendeeId", attendee.getId());

        // Add action listener to handle the checkbox click
        attendanceCheckBox.addActionListener(this::handleAttendeeCheckbox);

        // check the checkbox if attendee was marked as present
        if (sessionDTO.getPresentAttendees().contains(attendee.getId())) {
            attendanceCheckBox.setSelected(true);
        }

        // Add the checkbox to the panel with some spacing
        attendeePanel.add(Box.createVerticalStrut(5));
        attendeePanel.add(attendanceCheckBox);

        return attendeePanel;
    }

    private void handleAttendeeCheckbox(ActionEvent e) {
        JCheckBox checkBox = (JCheckBox) e.getSource();
        String attendeeId = (String) checkBox.getClientProperty("attendeeId");

        boolean isSelected = checkBox.isSelected();
        if (isSelected) {
            organizerObserver.onMarkAttendeeAsPresentRequest(sessionDTO.getId(), attendeeId);
        } else {
            organizerObserver.onMarkAttendeeAsAbsentRequest(sessionDTO.getId(), attendeeId);
        }
    }

}

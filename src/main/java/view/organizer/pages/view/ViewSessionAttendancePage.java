package view.organizer.pages.view;

import dto.UserDTO;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class ViewSessionAttendancePage extends ViewListPage<UserDTO> {
    private final float attendanceRecord;
    private final Set<String> presentAttendees;

    public ViewSessionAttendancePage(OrganizerObserver organizerObserver, List<UserDTO> registeredAttendees, Set<String> presentAttendees, String eventName, float attendanceRecord) {
        super(organizerObserver, registeredAttendees, eventName);
        this.presentAttendees = presentAttendees;
        this.attendanceRecord = attendanceRecord;
    }

    @Override
    public JPanel createPageContent() {
        JPanel mainContentPanel = super.createPageContent();
        mainContentPanel.add(createAttendanceRecordLabel(), BorderLayout.SOUTH);
        return mainContentPanel;
    }

    @Override
    protected String getPageTitle() {
        return String.format("Attendance Record for '%s'", eventName);
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
        String status = presentAttendees.contains(attendee.getId()) ? "Attended" : "Absent";
        JLabel emailLabel = new JLabel("Status: " + status);
        emailLabel.setFont(new Font("Sans serif", Font.PLAIN, 14));

        attendeePanel.add(nameLabel);
        attendeePanel.add(emailLabel);

        return attendeePanel;
    }

    private JLabel createAttendanceRecordLabel() {
        JLabel attendanceRecordLabel = new JLabel("Attendance Record: " + this.attendanceRecord + "%");
        attendanceRecordLabel.setFont(new Font("Sans serif", Font.BOLD, 20));
        attendanceRecordLabel.setBorder(BorderFactory.createEmptyBorder(0, 500, 30, 0));
        return attendanceRecordLabel;
    }
}

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
        super(organizerObserver, eventName, registeredAttendees);
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
    protected String getEmptyItemsMessage() {return "";}

    @Override
    protected JPanel createItemPanel(UserDTO attendee) {
        JPanel attendancePanel = new JPanel();
        attendancePanel.setLayout(new BoxLayout(attendancePanel, BoxLayout.Y_AXIS));

        // name of attendee
        JLabel nameLabel = new JLabel("Name: " + attendee.getName());
        nameLabel.setFont(new Font("Sans serif", Font.BOLD, 18));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // email of conference
        String status = presentAttendees.contains(attendee.getId()) ? "Attended" : "Absent";
        JLabel statusLabel = new JLabel("Status: " + status);
        statusLabel.setFont(new Font("Sans serif", Font.PLAIN, 14));

        attendancePanel.add(nameLabel);
        attendancePanel.add(statusLabel);

        return attendancePanel;
    }

    private JLabel createAttendanceRecordLabel() {
        JLabel attendanceRecordLabel = new JLabel("Attendance Record: " + this.attendanceRecord + "%");
        attendanceRecordLabel.setFont(new Font("Sans serif", Font.BOLD, 20));
        attendanceRecordLabel.setBorder(BorderFactory.createEmptyBorder(0, 500, 30, 0));
        return attendanceRecordLabel;
    }
}

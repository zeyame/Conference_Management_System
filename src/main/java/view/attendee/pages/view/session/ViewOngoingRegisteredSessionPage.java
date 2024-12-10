package view.attendee.pages.view.session;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.navigation.Navigator;
import view.event.UIEventMediator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewOngoingRegisteredSessionPage extends ViewSessionPage {
    public ViewOngoingRegisteredSessionPage(UserDTO attendee, String sessionId, UIEventMediator eventMediator, Navigator navigator) {
        super(attendee, sessionId, eventMediator, navigator);
    }

    @Override
    protected JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));

        JButton registerAttendanceButton = UIComponentFactory.createStyledButton("Register Attendance");

        registerAttendanceButton.addActionListener(this::handleRegisterAttendanceButton);

        footerPanel.add(registerAttendanceButton);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 80));

        return footerPanel;
    }

    @Override
    protected void handleBackButton(ActionEvent e) {
        ViewPersonalSchedulePage viewPersonalSchedulePage = new ViewPersonalSchedulePage(userDTO, eventMediator, navigator, sessionDTO.getConferenceId());
        navigator.navigateTo(viewPersonalSchedulePage);
    }

    private void handleRegisterAttendanceButton(ActionEvent e) {

    }
}

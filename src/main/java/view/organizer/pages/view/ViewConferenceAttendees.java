package view.organizer.pages.view;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class ViewConferenceAttendees extends ViewListPage<UserDTO> {

    public ViewConferenceAttendees(OrganizerObserver organizerObserver, String eventName, List<UserDTO> attendees) {
        super(organizerObserver, eventName, attendees);
    }

    @Override
    protected String getPageTitle() {
        return String.format("Attendees registered for '%s'", this.eventName);
    }

    protected String getEmptyItemsMessage() {return "No attendees have registered for sessions in this conference yet.";}

    @Override
    protected JPanel createItemPanel(UserDTO attendee) {
        return UIComponentFactory.createAttendeePanel(attendee.getName(), attendee.getEmail());
    }

}
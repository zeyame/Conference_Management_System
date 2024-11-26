package view.organizer.pages.add;

import dto.SessionDTO;
import dto.UserDTO;
import exception.FormValidationException;
import util.validation.FormValidator;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class EditSessionPage extends AddSessionPage {
    private final SessionDTO existingSession;

    public EditSessionPage(OrganizerObserver organizerObserver, String conferenceId, List<UserDTO> speakers, SessionDTO existingSession) {
        super(organizerObserver, conferenceId, null, speakers);
        this.existingSession = existingSession;

        // prefill the form fields with the existing session details
        prefillFormFields();

        // change button text to "Update"
        submitButton.setText("Update");
    }

    @Override
    protected String getFormTitle() {
        return "Edit Session";
    }
    @Override
    protected void handleSubmitSessionDetails(ActionEvent e) {
        String sessionName = nameField.getText();
        String sessionDescription = descriptionField.getText();
        UserDTO speaker = (UserDTO) speakerDropdown.getSelectedItem();
        String room = roomField.getText();
        LocalDate date = extractLocalDate((Date) dateSpinner.getValue());
        LocalTime startTime = extractLocalTime((Date) startTimeSpinner.getValue());
        LocalTime endTime = extractLocalTime((Date) endTimeSpinner.getValue());

        try {
            FormValidator.validateSessionForm(sessionName, sessionDescription, speaker, room, date, startTime, endTime);
        } catch (FormValidationException ex) {
            JOptionPane.showMessageDialog(mainContentPanel, ex.getMessage(), "Form Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SessionDTO updatedSessionDTO = SessionDTO.builder(conferenceId, speaker.getId(), speaker.getName(),
                        sessionName,
                        room, date, startTime, endTime)
                .setId(existingSession.getId())
                .setDescription(sessionDescription)
                .setRegisteredAttendees(existingSession.getRegisteredAttendees())
                .setPresentAttendees(existingSession.getPresentAttendees())
                .setFeedback(existingSession.getFeedback())
                .build();

        organizerObserver.onUpdateSessionFormRequest(updatedSessionDTO);
    }


    private void prefillFormFields() {
        nameField.setText(existingSession.getName());
        descriptionField.setText(existingSession.getDescription());
        roomField.setText(existingSession.getRoom());
        dateSpinner.setValue(java.sql.Date.valueOf(existingSession.getDate()));
        startTimeSpinner.setValue(java.sql.Time.valueOf(existingSession.getStartTime()));
        endTimeSpinner.setValue(java.sql.Time.valueOf(existingSession.getEndTime()));

        // Set the selected speaker
        for (int i = 0; i < speakerDropdown.getItemCount(); i++) {
            if (speakerDropdown.getItemAt(i).getId().equals(existingSession.getSpeakerId())) {
                speakerDropdown.setSelectedIndex(i);
                break;
            }
        }
    }

}

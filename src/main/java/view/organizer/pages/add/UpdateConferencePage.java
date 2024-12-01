package view.organizer.pages.add;

import dto.ConferenceDTO;
import dto.UserDTO;
import exception.FormValidationException;
import util.validation.FormValidator;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class UpdateConferencePage extends AddConferencePage {
    private final ConferenceDTO existingConference;

    public UpdateConferencePage(OrganizerObserver organizerObserver, UserDTO userDTO, ConferenceDTO existingConference) {
        super(organizerObserver, userDTO);
        this.existingConference = existingConference;

        prefillFormFields();

        submitButton.setText("Update");
    }


    @Override
    protected String getFormTitle() {
        return "Edit Conference";
    }

    @Override
    protected void handleSubmitConferenceDetails(ActionEvent e) {
        String conferenceName = nameField.getText();
        String conferenceDescription = descriptionField.getText();
        LocalDate startDate = extractLocalDate((Date) startDateSpinner.getValue());
        LocalDate endDate = extractLocalDate((Date) endDateSpinner.getValue());

        try {
            FormValidator.validateConferenceForm(conferenceName, conferenceDescription, startDate, endDate);
        } catch (FormValidationException ex) {
            JOptionPane.showMessageDialog(mainContentPanel, ex.getMessage(), "Form Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ConferenceDTO conferenceDTO = ConferenceDTO.builder(
                userDTO.getId(),
                conferenceName,
                conferenceDescription,
                startDate,
                endDate
        )
            .setId(existingConference.getId())
            .setSessions(existingConference.getSessions())
            .setAttendees(existingConference.getAttendees())
            .setSpeakers(existingConference.getSpeakers())
            .setFeedback(existingConference.getFeedback())
            .build();

        // publish event to organizer ui that user wants to update the conference
        organizerObserver.onUpdateConferenceRequest(conferenceDTO);
    }
    private void prefillFormFields() {
        nameField.setText(existingConference.getName());
        descriptionField.setText(existingConference.getDescription());

        // Convert LocalDate to Date for JSpinner
        Date startDate = Date.from(existingConference.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(existingConference.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant());

        startDateSpinner.setValue(startDate);
        endDateSpinner.setValue(endDate);
    }


}

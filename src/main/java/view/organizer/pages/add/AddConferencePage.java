package view.organizer.pages.add;

import dto.ConferenceDTO;
import dto.UserDTO;
import exception.FormValidationException;
import util.ui.FormBuilder;
import util.ui.UIComponentFactory;
import util.validation.FormValidator;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AddConferencePage extends AddPage {
    // dependencies
    private final UserDTO userDTO;

    // conference form fields
    private final JSpinner startDateSpinner;
    private final JSpinner endDateSpinner;

    public AddConferencePage(OrganizerObserver organizerObserver, UserDTO userDTO) {
        super(organizerObserver);
        this.userDTO = userDTO;

        this.startDateSpinner = UIComponentFactory.createDateSpinner();
        this.endDateSpinner = UIComponentFactory.createDateSpinner();

        // set up event listeners for the page
        setUpListeners();
    }

    @Override
    protected String getFormTitle() {
        return "Add a New Conference";
    }

    @Override
    protected JPanel createForm(JLabel formHeaderLabel) {
        FormBuilder formBuilder = new FormBuilder(10);

        formBuilder.addFullWidthComponent(formHeaderLabel, 0)
                .addLabel("Name", 1, 0)
                .addComponent(nameField, 1, 1)
                .addLabel("Description", 2, 0)
                .addComponent(descriptionField, 2, 1)
                .addLabel("Start Date", 3, 0)
                .addComponent(startDateSpinner, 3, 1)
                .addLabel("End Date", 4, 0)
                .addComponent(endDateSpinner, 4, 1)
                .addFullWidthComponent(submitButton, 5);

        JPanel conferenceFormPanel = formBuilder.build();

        // adjust form size
        conferenceFormPanel.setPreferredSize(new Dimension(300, 900));
        conferenceFormPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 150, 0));

        return formBuilder.build();
    }


    private void setUpListeners() {
        submitButton.addActionListener(this::handleSubmitConferenceDetails);
    }

    private void handleSubmitConferenceDetails(ActionEvent e) {
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
        ).build();

        // publish event to organizer ui that user wants to submit the conference form
        organizerObserver.onSubmitConferenceFormRequest(conferenceDTO);
    }
    private LocalDate extractLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}

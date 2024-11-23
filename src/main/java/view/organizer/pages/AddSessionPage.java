package view.organizer.pages;

import dto.SessionDTO;
import dto.UserDTO;
import exception.FormValidationException;
import util.FormBuilder;
import util.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class AddSessionPage {
    private final OrganizerObserver organizerObserver;
    private final String conferenceId;
    private final List<UserDTO> speakers;

    // Main panel
    private final JPanel mainContentPanel;

    // Form fields
    private final JTextField nameField;
    private final JTextField descriptionField;
    private final JTextField roomField;
    private final JSpinner dateSpinner;
    private final JSpinner startTimeSpinner;
    private final JSpinner endTimeSpinner;
    private final JButton submitButton;
    private JComboBox<UserDTO> speakerDropdown;

    // Back button
    private final JButton backButton;

    public AddSessionPage(OrganizerObserver organizerObserver, String conferenceId, List<UserDTO> speakers) {
        this.organizerObserver = organizerObserver;
        this.conferenceId = conferenceId;
        this.speakers = speakers;

        // Initialize components
        this.mainContentPanel = new JPanel(new BorderLayout());
        this.nameField = new JTextField(17);
        this.descriptionField = new JTextField(17);
        this.roomField = new JTextField(17);
        this.dateSpinner = UIComponentFactory.createDateSpinner();
        this.startTimeSpinner = UIComponentFactory.createTimeSpinner();
        this.endTimeSpinner = UIComponentFactory.createTimeSpinner();
        this.submitButton = new JButton("Submit");
        this.backButton = UIComponentFactory.createBackButton(e -> organizerObserver.onNavigateBackRequest());
        initializeJComboBox();

        // Adjust back button size
        Dimension smallerSize = new Dimension(25, 25);
        backButton.setPreferredSize(smallerSize);
        backButton.setMinimumSize(smallerSize);
        backButton.setMaximumSize(smallerSize);
        backButton.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        // Set up event listeners
        setUpListeners();
    }

    public JPanel createPageContent() {
        mainContentPanel.removeAll();

        // create header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel("", backButton);

        // create center panel for the form
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // add the session form
        JPanel sessionForm = createSessionForm();
        centerPanel.add(sessionForm);

        // add space at the bottom
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // add components to main panel
        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(centerPanel, BorderLayout.CENTER);

        return mainContentPanel;
    }

    private JPanel createSessionForm() {
        FormBuilder formBuilder = new FormBuilder(10);

        formBuilder.addFullWidthComponent(createHeaderLabel(), 0)
                .addLabel("Name", 1, 0)
                .addComponent(nameField, 1, 1)
                .addLabel("Description", 2, 0)
                .addComponent(descriptionField, 2, 1)
                .addLabel("Speaker", 3, 0)
                .addComponent(speakerDropdown, 3, 1)
                .addLabel("Room", 4, 0)
                .addComponent(roomField, 4, 1)
                .addLabel("Date", 5, 0)
                .addComponent(dateSpinner, 5, 1)
                .addLabel("Start Time", 6, 0)
                .addComponent(startTimeSpinner, 6, 1)
                .addLabel("End Time", 7, 0)
                .addComponent(endTimeSpinner, 7, 1)
                .addFullWidthComponent(submitButton, 8);

        JPanel sessionFormPanel =  formBuilder.build();

        // adjust form size
        sessionFormPanel.setPreferredSize(new Dimension(300, 900));
        sessionFormPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 150, 0));

        return sessionFormPanel;
    }

    private JLabel createHeaderLabel() {
        JLabel headerLabel = new JLabel("Add a New Session");
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 24));
        return headerLabel;
    }

    private void initializeJComboBox() {
        // initialize the JComboBox with UserDTO objects
        this.speakerDropdown = new JComboBox<>(speakers.toArray(new UserDTO[0]));

        // set custom renderer to display the name of each speaker
        this.speakerDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof UserDTO userDTO) {
                    setText(userDTO.getName());
                }
                return this;
            }
        });
    }

    private void setUpListeners() {
        submitButton.addActionListener(this::handleSubmitSessionDetails);
    }

    private void handleSubmitSessionDetails(ActionEvent e) {
        String sessionName = nameField.getText();
        String sessionDescription = descriptionField.getText();
        UserDTO speaker = (UserDTO) speakerDropdown.getSelectedItem();
        String room = roomField.getText();
        LocalDate date = extractLocalDate((Date) dateSpinner.getValue());
        LocalTime startTime = extractLocalTime((Date) startTimeSpinner.getValue());
        LocalTime endTime = extractLocalTime((Date) endTimeSpinner.getValue());

        try {
            validateSessionForm(sessionName, sessionDescription, speaker, room, date, startTime, endTime);
        } catch (FormValidationException ex) {
            JOptionPane.showMessageDialog(mainContentPanel, ex.getMessage(), "Form Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SessionDTO sessionDTO = SessionDTO.builder(conferenceId, speaker.getId(), speaker.getName(),
                sessionName, sessionDescription,
                room, date, startTime, endTime)
                .build();

        organizerObserver.onSubmitSessionFormRequest(sessionDTO);
    }

    private void validateSessionForm(String name, String description, UserDTO speaker, String room, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (name.isEmpty() || description.isEmpty() || speaker == null || room.isEmpty() || date == null || startTime == null || endTime == null) {
            throw new FormValidationException("All fields must be filled out.");
        }

        if (date.isBefore(LocalDate.now())) {
            throw new FormValidationException("Session date cannot be in the past.");
        }

        if (startTime.equals(endTime) || startTime.isAfter(endTime)) {
            throw new FormValidationException("Start time must be before end time.");
        }
    }

    private LocalDate extractLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private LocalTime extractLocalTime(Date time) {
        return time.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }
}

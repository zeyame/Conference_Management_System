package ui;

import controller.OrganizerController;
import dto.ConferenceDTO;
import dto.UserDTO;
import util.UIComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class OrganizerUI extends JFrame implements UserUI {
    private final OrganizerController organizerController;
    private final UserDTO userDTO;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    public OrganizerUI(OrganizerController organizerController, UserDTO userDTO) {
        this.organizerController = organizerController;
        this.userDTO = userDTO;

        // frame configuration
        setTitle("Organizer Landing Page");
        setSize(new Dimension(1400, 800));
        setResizable(false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // centering the UI
        setLocationRelativeTo(null);

        // welcome message
        JPanel welcomePanel = UIComponentFactory.createWelcomePanel(userDTO.getName());

        add(welcomePanel, BorderLayout.NORTH);

        // hiding the welcome message after three seconds
        new Timer(3000, e -> welcomePanel.setVisible(false)).start();

        // main content panel of the frame
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel, BorderLayout.CENTER);

        contentPanel.add(createHomePage(), "Home Page");

        cardLayout.show(contentPanel, "Home Page");
    }

    @Override
    public void display() {
        setVisible(true);
        toFront();
    }

    private JPanel createHomePage() {
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));

        // title for home page
        JLabel headerLabel = new JLabel("Your Managed Conferences");
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        homePanel.add(headerLabel);
        homePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // scrollable container for the conferences panels
        JPanel conferencesPanel = new JPanel();
        conferencesPanel.setLayout(new BoxLayout(conferencesPanel, BoxLayout.Y_AXIS));
        conferencesPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 0));

        // displaying all managed conferences for organizer
        List<ConferenceDTO> conferences = organizerController.getManagedConferences(userDTO.getEmail());
        for (ConferenceDTO conference: conferences) {
            JPanel conferencePanel = createConferencePanel(conference);
            conferencesPanel.add(conferencePanel);
            conferencesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JScrollPane scrollPane = new JScrollPane(conferencesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(7);

        homePanel.add(scrollPane);
        return homePanel;
    }

    private JPanel createConferencePanel(ConferenceDTO conference) {
        JPanel conferencePanel = new JPanel();
        conferencePanel.setLayout(new BoxLayout(conferencePanel, BoxLayout.Y_AXIS));

        // name of conference
        JLabel nameLabel = new JLabel(conference.getName());
        nameLabel.setFont(new Font("Sans serif", Font.BOLD, 16));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // start date of conference
        JLabel dateLabel = new JLabel("Date: " + conference.getStartDate());
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // manage conference button
        JButton manageButton = new JButton("Manage Conference");
        manageButton.setFocusPainted(false);
        manageButton.putClientProperty("conferenceId", conference.getId());
        manageButton.addActionListener(this::handleManageConferenceButton);

        conferencePanel.add(nameLabel);
        conferencePanel.add(dateLabel);
        conferencePanel.add(manageButton);

        return conferencePanel;
    }

    private void handleManageConferenceButton(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        String conferenceId = (String) sourceButton.getClientProperty("conferenceId");
    }
}

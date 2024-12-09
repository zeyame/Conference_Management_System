package view.attendee.pages.view;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.pages.view.conference.ViewConferencePage;
import view.attendee.pages.view.conference.ViewPastRegisteredConferencePage;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewCertificatePage extends JPanel {

    private final UserDTO attendee;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;
    private final ConferenceDTO conferenceDTO;

    public ViewCertificatePage(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator, ConferenceDTO conferenceDTO) {
        this.attendee = attendee;
        this.eventMediator = eventMediator;
        this.navigator = navigator;
        this.conferenceDTO = conferenceDTO;

        setLayout(new BorderLayout());
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCertificatePanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(248, 245, 233); // Light beige
                Color color2 = new Color(237, 232, 220); // Slightly darker beige
                g2d.setPaint(new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JButton backButton = createBackButton();
        headerPanel.add(backButton, BorderLayout.WEST);

        return headerPanel;
    }

    private JButton createBackButton() {
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Garamond", Font.PLAIN, 16));
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(210, 180, 140)); // Light tan color
        backButton.setForeground(new Color(90, 64, 42)); // Dark brown text
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backButton.addActionListener(this::handleBackButton);
        return backButton;
    }

    private JPanel createCertificatePanel() {
        JPanel certificatePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(248, 245, 233); // Light beige
                Color color2 = new Color(237, 232, 220); // Slightly darker beige
                g2d.setPaint(new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        certificatePanel.setLayout(new BoxLayout(certificatePanel, BoxLayout.Y_AXIS));
        certificatePanel.setBorder(createCertificateBorder());

        certificatePanel.add(createLogoLabel());
        certificatePanel.add(Box.createVerticalStrut(20));
        certificatePanel.add(createNameLabel());
        certificatePanel.add(createConferenceLabel());
        certificatePanel.add(Box.createVerticalStrut(20));
        certificatePanel.add(createFooterLabel());

        return certificatePanel;
    }

    private Border createCertificateBorder() {
        return new CompoundBorder(
                new EmptyBorder(30, 30, 30, 30),
                BorderFactory.createTitledBorder(
                        new LineBorder(new Color(90, 64, 42), 5, true),
                        "Certificate of Completion",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Garamond", Font.BOLD, 28),
                        new Color(90, 64, 42)
                )
        );
    }

    private JLabel createLogoLabel() {
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon("src/main/resources/images/uh-logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        return logoLabel;
    }

    private JLabel createNameLabel() {
        JLabel nameLabel = new JLabel("This certifies that " + attendee.getName());
        nameLabel.setFont(new Font("Garamond", Font.PLAIN, 22));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        return nameLabel;
    }

    private JLabel createConferenceLabel() {
        JLabel conferenceLabel = new JLabel(
                String.format("has successfully completed the conference '%s'", conferenceDTO.getName())
        );
        conferenceLabel.setFont(new Font("Garamond", Font.PLAIN, 20));
        conferenceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return conferenceLabel;
    }

    private JLabel createFooterLabel() {
        JLabel footerLabel = new JLabel("Presented on behalf of the University");
        footerLabel.setFont(new Font("Garamond", Font.ITALIC, 16));
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        return footerLabel;
    }

    private void handleBackButton(ActionEvent e) {
        ViewConferencePage viewConferencePage = new ViewPastRegisteredConferencePage(attendee, eventMediator, navigator, conferenceDTO.getId());
        navigator.navigateTo(viewConferencePage);
    }
}

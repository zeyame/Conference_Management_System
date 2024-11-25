package view.organizer.pages.add;

import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;

public abstract class AddPage {
    // dependencies
    protected final OrganizerObserver organizerObserver;

    // main content panel
    protected final JPanel mainContentPanel;

    // buttons
    protected final JButton backButton;
    protected final JButton submitButton;

    // fields
    protected final JTextField nameField;
    protected final JTextField descriptionField;

    public AddPage(OrganizerObserver organizerObserver) {
        this.organizerObserver = organizerObserver;

        // initialize components
        this.mainContentPanel = new JPanel(new BorderLayout());
        this.backButton = UIComponentFactory.createBackButton(e -> this.organizerObserver.onNavigateBackRequest());
        this.submitButton = UIComponentFactory.createStyledButton("Submit");
        this.nameField = new JTextField(17);
        this.descriptionField = new JTextField(17);
    }


    public JPanel createPageContent() {
        mainContentPanel.removeAll();

        // create header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel("", backButton);

        // create center panel for the form
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // add the session form
        JPanel form = createForm(createHeaderLabel(getFormTitle()));
        centerPanel.add(form);

        // add space at the bottom
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // add components to main panel
        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(centerPanel, BorderLayout.CENTER);

        return mainContentPanel;
    }


    protected abstract String getFormTitle();

    protected abstract JPanel createForm(JLabel formHeaderLabel);

    private JLabel createHeaderLabel(String title) {
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 24));
        return headerLabel;
    }


}

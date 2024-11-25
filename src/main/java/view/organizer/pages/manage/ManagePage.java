package view.organizer.pages.manage;

import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;

public abstract class ManagePage {
    // dependencies
    protected final OrganizerObserver organizerObserver;

    // main content panel
    protected final JPanel mainContentPanel;

    // buttons
    protected final JButton backButton;
    protected final JButton editButton;
    protected final JButton deleteButton;

    public ManagePage(OrganizerObserver organizerObserver, String editButtonText, String deleteButtonText) {
        this.organizerObserver = organizerObserver;
        this.mainContentPanel = new JPanel(new BorderLayout());

        // initialize common buttons
        this.backButton = UIComponentFactory.createBackButton(e -> this.organizerObserver.onNavigateBackRequest());
        this.editButton = UIComponentFactory.createStyledButton(editButtonText);
        this.deleteButton = UIComponentFactory.createStyledButton(deleteButtonText);

    }

    public JPanel createPageContent() {
        // refresh page
        mainContentPanel.removeAll();

        // add main components
        mainContentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainContentPanel.add(createDetailsPanel(), BorderLayout.CENTER);
        mainContentPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        return mainContentPanel;
    }

    protected JPanel createHeaderPanel() {
        JPanel baseHeaderPanel = UIComponentFactory.createHeaderPanel(getHeaderTitle(), backButton);
        baseHeaderPanel.add(Box.createRigidArea(new Dimension(350, 0))); // Adjust space dynamically
        baseHeaderPanel.add(editButton);
        baseHeaderPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        baseHeaderPanel.add(deleteButton);

        return baseHeaderPanel;
    }

    protected abstract String getHeaderTitle();

    protected abstract JPanel createDetailsPanel();

    protected abstract JPanel createFooterPanel();

    protected abstract void setUpListeners();
}

package view.organizer.pages.view;

import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public abstract class ViewListPage<T> {
    protected final OrganizerObserver organizerObserver;
    protected final String conferenceName;

    // main panel
    protected final JPanel mainContentPanel;

    // back button
    protected final JButton backButton;

    // list of items
    protected final List<T> items;

    public ViewListPage(OrganizerObserver organizerObserver, List<T> items, String conferenceName) {
        this.organizerObserver = organizerObserver;
        this.items = items;
        this.conferenceName = conferenceName;

        // initializing components
        this.mainContentPanel = new JPanel(new BorderLayout());
        this.backButton = UIComponentFactory.createBackButton(e -> organizerObserver.onNavigateBackRequest());
    }

    public JPanel createPageContent() {
        // creating main components
        JPanel headerPanel = UIComponentFactory
                .createHeaderPanel(getPageTitle(), backButton);
        JScrollPane scrollPane = createItemsScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        return mainContentPanel;
    }

    protected abstract String getPageTitle();

    protected abstract JScrollPane createItemsScrollPane();

    protected abstract JPanel createItemPanel(T item);
}

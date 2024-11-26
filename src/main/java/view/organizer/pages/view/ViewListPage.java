package view.organizer.pages.view;

import dto.SessionDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public abstract class ViewListPage<T> {
    protected final OrganizerObserver organizerObserver;
    protected final String eventName;           // event can be a conference or a session

    // main panel
    protected final JPanel mainContentPanel;

    // back button
    protected final JButton backButton;

    // list of items
    protected final List<T> items;

    public ViewListPage(OrganizerObserver organizerObserver, List<T> items, String eventName) {
        this.organizerObserver = organizerObserver;
        this.items = items;
        this.eventName = eventName;

        // initializing components
        this.mainContentPanel = new JPanel(new BorderLayout());
        this.backButton = UIComponentFactory.createBackButton(e -> organizerObserver.onNavigateBackRequest());
    }

    public JPanel createPageContent() {
        // creating main components
        JPanel headerPanel = UIComponentFactory
                .createHeaderPanel(getPageTitle(), backButton);
        JScrollPane scrollPane = createItemsScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 0));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(7);

        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        return mainContentPanel;
    }

    protected abstract String getPageTitle();

    protected abstract JPanel createItemPanel(T item);

    private JScrollPane createItemsScrollPane() {
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        for (T item : items) {
            itemsPanel.add(createItemPanel(item));
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add spacing between sessions
        }

        return new JScrollPane(itemsPanel);
    }
}

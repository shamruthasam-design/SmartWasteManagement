package ui;

/** Implemented by every page so MainFrame can reload its data when it is opened. */
public interface Refreshable {
    void refresh();
}

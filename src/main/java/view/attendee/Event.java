package view.attendee;

public interface Event<T> {
    void notify(T observer);
}

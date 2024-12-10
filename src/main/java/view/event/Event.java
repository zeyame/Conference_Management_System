package view.event;

public interface Event<T> {
    void notify(T observer);
}

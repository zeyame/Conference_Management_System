package view.attendee;

import java.util.HashMap;
import java.util.Map;

public class UIEventMediator {
    private final Map<Class<?>, Object> observers = new HashMap<>();

    // register an observer by a specific type
    public <T> void registerObserver(Class<T> observerType, T observer) {
        observers.put(observerType, observer);
    }

    // publish an event to the correct observer
    public <T> void publishEvent(Class<T> observerType, Event<T> event) {
        T observer = observerType.cast(observers.get(observerType));
        if (observer != null) {
            event.notify(observer);
        }
    }
}

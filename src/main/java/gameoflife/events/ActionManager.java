package gameoflife.events;

import gameoflife.events.action.Action;
import gameoflife.events.listener.Listener;

import java.util.ArrayList;
import java.util.List;

public enum ActionManager {
    INSTANCE;

    private List<Listener> listeners = new ArrayList<>();

    public <T extends Action> void sendAction(T action) {
        this.listeners.forEach(listener -> listener.processAction(action));
    }

    public <T extends Action> void addListener(Listener listener) {
        listeners.add(listener);
    }
}

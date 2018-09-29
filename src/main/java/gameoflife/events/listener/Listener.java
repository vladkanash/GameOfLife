package gameoflife.events.listener;

import gameoflife.events.action.Action;

public interface Listener {

    void processAction(Action action);

}

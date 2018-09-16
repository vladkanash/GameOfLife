package gameoflife.events.listener;

import gameoflife.events.action.Action;

public abstract class Listener {

    public abstract void processAction(Action action);

}

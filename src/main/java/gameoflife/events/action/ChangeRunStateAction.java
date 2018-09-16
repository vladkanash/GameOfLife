package gameoflife.events.action;

public class ChangeRunStateAction implements Action {

    private boolean state;

    public ChangeRunStateAction(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return state;
    }
}

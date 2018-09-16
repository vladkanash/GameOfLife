package gameoflife.events.listener;

import gameoflife.events.action.Action;
import gameoflife.events.action.ChangeRunStateAction;
import org.eclipse.swt.widgets.ToolItem;

public class StopButtonListener extends Listener {

    private ToolItem button;

    public StopButtonListener(ToolItem button) {
        this.button = button;
    }

    @Override
    public void processAction(Action action) {
        if (action instanceof ChangeRunStateAction) {
            button.setSelection(!((ChangeRunStateAction) action).getState());
        }
    }
}

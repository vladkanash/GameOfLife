package gameoflife.events.listener;

import gameoflife.events.action.Action;
import gameoflife.events.action.ChangeRunStateAction;
import org.eclipse.swt.widgets.ToolItem;

public class StartButtonListener extends Listener {

    private ToolItem button;

    public StartButtonListener(ToolItem button) {
        this.button = button;
    }

    @Override
    public void processAction(Action action) {
        if (action instanceof ChangeRunStateAction) {
            button.setSelection(((ChangeRunStateAction) action).getState());
        }
    }
}

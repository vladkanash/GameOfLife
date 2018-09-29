package gameoflife.events.listener;

import gameoflife.events.action.Action;
import gameoflife.events.action.UpdateGenLabelAction;
import org.eclipse.swt.widgets.Label;

public class GenLabelListener implements Listener {

    private Label label;

    public GenLabelListener(Label label) {
        this.label = label;
    }

    @Override
    public void processAction(Action action) {
        if (action instanceof UpdateGenLabelAction) {
            label.setText(((UpdateGenLabelAction) action).getText());
        }
    }
}

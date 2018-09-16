package gameoflife.events.action;

public class SetLabelTextAction implements Action {

    public SetLabelTextAction(String text) {
        this.text = text;
    }

    private String text;

    public String getText() {
        return text;
    }
}

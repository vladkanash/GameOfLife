package gameoflife;

/**
 * Created by Vlad Kanash on 9.7.16.
 */

import gameoflife.ui.GameOfLifeUI;
import org.eclipse.swt.widgets.Display;

public class Main {

    /**
     * main()
     * @param args not used
     */
    public static void main(String[] args) {
        Display display = new Display();

        //game logic thread
        CellGrid engine = new CellGrid();
        Thread t = new Thread(engine, "Game logic thread");
        t.setDaemon(true);
        t.start();

        //GUI
        new GameOfLifeUI(display, engine);
    }
}

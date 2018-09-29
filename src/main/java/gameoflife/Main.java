package gameoflife;

/**
 * Created by Vlad Kanash on 9.7.16.
 */

import gameoflife.ui.GameBoard;
import gameoflife.ui.GameOfLifeUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main {

    /**
     * main()
     * @param args not used
     */
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);

        //game logic thread
        GameBoard gameBoard = new GameBoard(shell);

        //GUI
        new GameOfLifeUI(shell, gameBoard);
    }
}

package gameoflife;

import gameoflife.ui.board.GameBoard;

public class SavingManager {

    private String currentFilepath;
    private final GameBoard gameBoard;

    public SavingManager(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void loadGame(String filePath) {
        gameBoard.loadGame(filePath);
        currentFilepath = filePath;
    }

    public void saveGame() {
        if (currentFilepath != null) {
            gameBoard.saveGame(currentFilepath);
        }
    }

    public void saveGameAs(String filePath) {
        gameBoard.saveGame(filePath);
        currentFilepath = filePath;
    }

    public String getCurrentFilepath() {
        return currentFilepath;
    }
}

package gameoflife.ui;

import gameoflife.Cell;
import gameoflife.CellGrid;
import gameoflife.Scenario;
import gameoflife.events.ActionManager;
import gameoflife.events.action.SetLabelTextAction;
import gameoflife.events.action.UpdateGenLabelAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import java.util.Enumeration;

/**
 * Represents a game field on the screen
 */

public class GameBoard extends Canvas implements Runnable {

    private ActionManager actionManager = ActionManager.INSTANCE;

    /**
     * False = field without a grid.
     */
    private boolean gridState = true;


    /**
     * Current cell size (in pixels).
     */
    private int cellSize = 15;

    /**
     * Parent display
     */
    private final Display display;


    /**
     * Contents game cells and logic.
     */
    private final CellGrid cellGrid;


    /**
     * Background color.
     */
    private final Color BackgroundColor;

    /**
     * Grid color.
     */
    private final Color ForegroundColor;


    /**
     * Shows the area of the game field which is on the screen at the moment.
     */
    private Rectangle zoomOffset;

    /**
     * Grid image. Does not has to be redrawn every time, so we save it
     */
    private Image gridImage;


    /**
     * Saved game scenario
     */
    private Scenario scenario;


    /**
     * True if scenario is recorded at the moment
     */
    private boolean recording = false;


    /**
     * True if scenario is running at the moment
     */
    private boolean scnRunning = false;


    /**
     * True if scenario is paused at the moment
     */
    private boolean scnPaused = false;



    /**
     * @param shell Parent Shell
     */
    public GameBoard(Shell shell) {
        super(shell, SWT.BORDER | SWT.NO_BACKGROUND);
        this.display = shell.getDisplay();
        this.cellGrid = new CellGrid();

        this.scenario = new Scenario(cellGrid);
        this.zoomOffset = new Rectangle(0, 0, 0, 0);
        this.BackgroundColor = new Color(display, 20, 20, 20);
        this.ForegroundColor = new Color(display, 0, 200, 0);

        //Layout data
        GridData data = new GridData();
        data.horizontalSpan = 5;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        setLayoutData(data);

        this.addPaintListener(e -> {
            //Создаем новый обьект только когда необходимо (если изменился размер поля)
            Image image = (Image) getData("double-buffer-image");
            if (image == null
                    || image.getBounds().width != getSize().x
                    || image.getBounds().height != getSize().y)
            {
                image = new Image( display, getSize().x, getSize().y);
                updateGridImage();

                setData("double-buffer-image", image);
            }

            GC imageGC = new GC(image);

            imageGC.drawImage(gridImage, 0, 0);   //Рисуем сетку (загружаем сохраненную)
            drawCells(imageGC); //Рисуем клетки

            e.gc.drawImage(image,0,0);   //Выводим изображение на экран

            imageGC.dispose();
            e.gc.dispose();

        });

        Listener mouseListener = new Listener() {
            private boolean mousePressed = false;
            private boolean cellState = false;

            public void handleEvent(Event event)
            {
                switch (event.type)
                {
                    case SWT.MouseDown:
                        mousePressed = true;
                        cellState = !cellGrid
                                .getCell(event.x / cellSize + zoomOffset.x, event.y / cellSize + zoomOffset.y);
                        drawCell(event.x, event.y, cellState);
                        break;

                    case SWT.MouseMove:
                        if (mousePressed)
                            drawCell(event.x, event.y, cellState);
                        break;

                    case SWT.MouseUp:
                        mousePressed = false;
                        break;
                }
            }
        };

        this.addListener(SWT.MouseDown, mouseListener);
        this.addListener(SWT.MouseMove, mouseListener);
        this.addListener(SWT.MouseUp, mouseListener);

        this.addListener(SWT.Resize, event -> updateZoomOffset());

        this.setBackground(BackgroundColor);

        display.asyncExec(this); //initial run() call
        updateZoomOffset();

        Thread t = new Thread(cellGrid, "Game logic thread");
        t.setDaemon(true);
        t.start();
    }


    /**
     *Used by Display.asyncExec() method
     */
    public void run() {
        redraw();
        updateGenLabel();

        if (scnRunning && !scnPaused) {
            cellGrid.setRunState(false);
            cellGrid.next();


            if (!scenario.checkForEntries() && getGeneration() == scenario.getLastGeneration()) {
                scnRunning = false;

                //End message
                MessageBox msg = new MessageBox(display.getActiveShell(), SWT.OK);
                msg.setMessage("Scenario completed!");
                cellGrid.setRunState(false);
                msg.open();

                if (!getShell().isDisposed()) {
                    display.asyncExec(this);
                }
            } else {
                redraw();
                cellGrid.incrementGenerations();
                display.timerExec(cellGrid.getDelay(), this);
            }
        }

        else {
            if (!getShell().isDisposed()) display.asyncExec(this);
        }
    }


    /**
     * Change the state of a single cell on the game field
     * @param x X pos of the mouse
     * @param y Y pos of the mouse
     * @param state true - place new cell
     *              false - delete cell
     */
    private void drawCell(int x, int y, boolean state) {
       if (cellGrid.getRunState() || scnRunning) return;

        cellGrid.setCell(x / cellSize + zoomOffset.x, y / cellSize + zoomOffset.y, state );

        //add the cell info to the saved scenario.
        if (recording)
        {
            Point pos = new Point(x / cellSize + zoomOffset.x, y / cellSize + zoomOffset.y);
            scenario.addEntry(getGeneration(), pos, state);
        }
    }



    /**
     *Set the new grid image (after resizing, zooming, etc.)
     */
    private void updateGridImage() {
        gridImage = new Image( display, getSize().x, getSize().y);

        GC gridGC = new GC(gridImage);

        gridGC.setBackground(BackgroundColor);
        gridGC.setAntialias(SWT.ON);

        Rectangle imageSize = gridImage.getBounds();
        gridGC.fillRectangle(0, 0, imageSize.width + 1, imageSize.height + 1);

        drawGrid(gridGC); //Draw the grid
        gridGC.dispose();
    }



    /**
     * Draw the grid on the canvas
     * @param e canvas GC
     */
    private void drawGrid(GC e) {
        if (!gridState) return;

        for (int i=0; i < getSize().x; i+= cellSize)
            e.drawLine(i, 0, i, getSize().y);

        for (int i=0; i < getSize().y; i+= cellSize)
            e.drawLine(0, i, getSize().x, i);

    }


    /**
     * Draw the living cells on the canvas
     * @param e canvas GC
     */
    private void drawCells(GC e) {
        //fillRectangle использует цвет фона для заливки
        e.setBackground(ForegroundColor);

        Enumeration Enum = cellGrid.getEnum();
        Cell c;
        while ( Enum.hasMoreElements() )
        {
            c = (Cell) Enum.nextElement();
            e.fillRectangle((c.col-zoomOffset.x) * cellSize, (c.row - zoomOffset.y)* cellSize, cellSize, cellSize);
        }

       e.setBackground(BackgroundColor);
    }

    /**
     * Get the generations count since the game has started
     * @return generations count
     */
    private synchronized int getGeneration() {
        return cellGrid.getGenerations();
    }


    /**
     * Calculate the new zoomOffset value (depending on canvas size)
     */
    public void updateZoomOffset() {
        this.zoomOffset.x = (int) (0.5 * (CellGrid.gridColumns - this.getSize().x / cellSize));
        this.zoomOffset.y = (int) (0.5 * (CellGrid.gridRows - this.getSize().y / cellSize));
        this.zoomOffset.width = this.getSize().x / cellSize;
        this.zoomOffset.height = this.getSize().y / cellSize;

        cellGrid.setZoomOffset(zoomOffset);
    }

    /**
     * Changes cell size and updates the image
     * @param newCellSize new cell size (in pixels)
     */
    public synchronized void zoom(int newCellSize) {
        cellSize = newCellSize;
        updateZoomOffset();
        updateGridImage();
    }


    /**
     * Resets the game
     */
    public void resetGame() {
        this.cellGrid.clear();
        updateGenLabel();
    }


    /**
     * Grid state
     * @param newState true - show the grid
     *                 false - hide it
     */
    public synchronized void setGridState(boolean newState) {
        this.gridState = newState;
        updateGridImage();
    }

    /**
     * Save current pattern
     * @param fileName .gol file name
     */
    public void saveGame(String fileName) {
        cellGrid.save(fileName);
    }

    /**
     * Load pattern from file
     * @param fileName .gol file name
     */
    public void loadGame(String fileName) {
        cellGrid.load(fileName);
        updateGenLabel();
        //redraw();
    }


    /**
     * Save scenario to the file
     */
    public void saveScenario(String filename)
    {
        scenario.saveScenario(filename);
    }


    /**
     * Load scenario from file and run it.
     * @param fileName .scn file name
     */
    public void runScenario(String fileName) {
        cellGrid.clear();
        scenario.clear();

        scenario.loadScenario(fileName);

        //redraw();
        updateGenLabel();

        cellGrid.setRunState(false);
        setScnPause(true);
        this.scnRunning = true;
    }

    /**
     * Replay the scenario from the memory
     */
    public void replayScenario() {
        cellGrid.clear();
        scenario.setInitGeneration();

        cellGrid.setRunState(false);
        setScnPause(false);
        this.scnRunning = true;
    }


    public void setRecordState(boolean state) {
        this.recording = state;


        //Save the initial state for the scenario
        if (state)
        {
            scenario.clear();
            scenario.addCurrentState(getGeneration());
        }

        //Save the last generation
        else
        {
            scenario.setLastGeneration(getGeneration());
        }
    }

    /**
     * Return to the normal game mode
     */
    public void stopScenarioPlaying() {
        scnRunning = false;
    }

    /**
     * Update the generations label
     */
    public void updateGenLabel() {
        actionManager.sendAction(new UpdateGenLabelAction(String.format("Generation: %06d", getGeneration())));
    }


    /**
     * Go to the next generation (for the "step" button)
     */
    public void makeStep() {
        if (scnRunning) {
            cellGrid.setRunState(false);
            cellGrid.next();


            if (!scenario.checkForEntries() && getGeneration() == scenario.getLastGeneration()) {
                scnRunning = false;

                //End message
                MessageBox msg = new MessageBox(display.getActiveShell(), SWT.OK);
                msg.setMessage("Scenario completed!");
                cellGrid.setRunState(false);
                msg.open();

            }
            else {
                redraw();
                cellGrid.incrementGenerations();
            }

        }

        redraw();
        cellGrid.next();
        cellGrid.incrementGenerations();
        updateGenLabel();
    }

    /**
     * Set a pause while a scenario is played.
     * @param state true - set pause
     */
    public void setScnPause(boolean state)
    {
        scnPaused = state;
    }


    /**
     * Is the scenario running at the moment
     * @return true - scenario is running
     *         false - scenario isn't running (normal game mode)
     */
    public synchronized boolean isScnRunning()
    {
        return scnRunning;
    }

    public void setRunState(boolean state) {
        this.cellGrid.setRunState(state);
    }

    public void setDelay(int delay) {
        this.cellGrid.setDelay(delay);
    }

    public void next() {
        this.cellGrid.next();
    }

    public void incrementGenerations() {
        this.cellGrid.incrementGenerations();
    }

}



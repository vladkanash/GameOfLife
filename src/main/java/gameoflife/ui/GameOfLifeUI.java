package gameoflife.ui;

/**
 * Created by Vlad Kanash on 18.02.2015.
 */

import gameoflife.CellGrid;
import gameoflife.ui.board.GameBoard;
import gameoflife.config.MainConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import  org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

import java.lang.String;
import java.util.ListResourceBundle;

/**
 * UI main class
 */
public class GameOfLifeUI
{

    private Shell shell;

    private ToolItem startButton;
    private ToolItem stopButton;
    private Label genLabel;

    private final GameBoard game;
    private String currentGameFile;
    private final CellGrid grid;

    private final ListResourceBundle stringConfig;


    public GameOfLifeUI(Display display, CellGrid grid)
    {
        this.shell = new Shell(display);
        this.grid = grid;

        this.stringConfig = new MainConfig();

        shell.setText(stringConfig.getString("gameTitle"));
        shell.pack();
        shell.setSize((Integer) stringConfig.getObject("initialWidth"),
                (Integer) stringConfig.getObject("initialHeight"));

        GridLayout layout = new GridLayout(5, false);
        shell.setLayout(layout);

        initWidgets();

        game = new GameBoard(shell, genLabel, grid);

        shell.open();

        game.updateZoomOffset();

        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();

        display.dispose();
    }

    private void initWidgets()
    {
        final ToolBar mainToolBar = new ToolBar(shell, SWT.WRAP | SWT.RIGHT );

        initStartButton(mainToolBar);
        initStopButton(mainToolBar);
        initStepButton(mainToolBar);

        initSpeedLabel();
        initSpeedScale();

        initGenerationLabel();

        initMainMenu();
        initCloseMessageBox();
    }

    private void initCloseMessageBox() {
        shell.addListener(SWT.Close, event -> {
            grid.setRunState(false);

            final MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
            messageBox.setMessage("Save the game before exit?");
            messageBox.setText("Exiting Application");
            final int response = messageBox.open();
            if (response == SWT.YES) {
                event.doit = false;

                final FileDialog fd = new FileDialog(shell, SWT.SAVE);
                fd.setFilterPath("C:/");
                fd.setOverwrite(true);
                final String[] filterExt = {"*.gol"};
                final String[] extName = {"Game of Life pattern (*.gol)"};
                fd.setFilterNames(extName);
                fd.setFilterExtensions(filterExt);
                game.saveGame(fd.open());

            } else if (response == SWT.CANCEL) {
                event.doit = false;
            }
        });
    }

    private Menu initZoomMenu() {
        final Menu zoomMenu = new Menu(shell, SWT.DROP_DOWN );

        UITools.initMenuItem(zoomMenu, "Huge", 0, SWT.PUSH, () -> game.zoom(30));
        UITools.initMenuItem(zoomMenu, "Normal", 0, SWT.PUSH, () -> game.zoom(10));
        UITools.initMenuItem(zoomMenu, "Small", 0, SWT.PUSH, () -> game.zoom(4));
        UITools.initMenuItem(zoomMenu, "Pixel cells", 0, SWT.PUSH, () -> game.zoom(1));

        return zoomMenu;
    }

    private Menu initShapesMenu() {
        final Menu shapeMenu  = new Menu(shell, SWT.DROP_DOWN );

        final MenuItem glider = new MenuItem(shapeMenu, SWT.PUSH);
        glider.setText("Glider");

        final MenuItem gun = new MenuItem(shapeMenu, SWT.PUSH);
        gun.setText("Gun");
        //TODO: More shapes
        return shapeMenu;
    }

    private void initMainMenu() {
        final Menu mainMenu = new Menu(shell, SWT.BAR | SWT.LEFT_TO_RIGHT);

        final MenuItem Game = new MenuItem(mainMenu, SWT.CASCADE);
        Game.setText("&Game");

        final MenuItem Scenario = new MenuItem(mainMenu, SWT.CASCADE);
        Scenario.setText("&Scenario");

        final MenuItem Shapes = new MenuItem(mainMenu, SWT.CASCADE);
        Shapes.setText("&Patterns");

        final MenuItem Zoom = new MenuItem(mainMenu, SWT.CASCADE);
        Zoom.setText("&Zoom");

        UITools.initMenuItem(mainMenu, "&Help", 0, SWT.PUSH, () -> {
            final MessageBox msg = new MessageBox(shell, SWT.NONE);
            msg.setMessage("No help yet");
            msg.open(); //TODO help window
        });

        final Menu gameMenu = initGameMenu();
        Game.setMenu(gameMenu);

        final Menu shapeMenu = initShapesMenu();
        Shapes.setMenu(shapeMenu);

        final Menu zoomMenu = initZoomMenu();
        Zoom.setMenu(zoomMenu);

        shell.setMenuBar(mainMenu);
    }

    private Menu initGameMenu() {

        final MenuAction resetGame = (e) -> {
            game.resetGame();
            grid.setRunState(false);
            startButton.setSelection(false);
            stopButton.setSelection(true);
        };

        final MenuAction triggerGrid = e -> {
            MenuItem item = (MenuItem) e.widget;
            boolean sel = item.getSelection();
            boolean enabled = item.getEnabled();
            game.setGridState(item.getSelection());
        };

        final MenuAction loadGame = (e) -> {
            FileDialog fd = new FileDialog(shell, SWT.OPEN);
            fd.setFilterPath("C:/");
            fd.setOverwrite(true);
            String[] filterExt = { "*.gol" };
            String[] extName = {"Game of Life pattern (*.gol)"};
            fd.setFilterNames(extName);
            fd.setFilterExtensions(filterExt);
            String selected = fd.open();
            game.loadGame(selected);
            currentGameFile = selected;
        };

        final MenuAction saveGame = (e) -> {
            if (currentGameFile != null) {
                game.saveGame(currentGameFile);
                return;
            }
            stopButton.setSelection(true);

            FileDialog fd = new FileDialog(shell, SWT.SAVE);
            fd.setFilterPath("C:/");
            fd.setOverwrite(true);
            String[] filterExt = { "*.gol" };
            String[] extName = {"Game of Life pattern (*.gol)"};
            fd.setFilterNames(extName);
            fd.setFilterExtensions(filterExt);
            String selected = fd.open();

            game.saveGame(selected);
            currentGameFile = selected;
        };

        final MenuAction saveGameAs = (e) -> {
            grid.setRunState(false);

            FileDialog fd = new FileDialog(shell, SWT.SAVE);
            fd.setFilterPath("C:/");
            fd.setOverwrite(true);

            String[] filterExt = { "*.gol" };
            String[] extName = {"Game of Life pattern (*.gol)"};

            fd.setFileName(currentGameFile);
            fd.setFilterNames(extName);
            fd.setFilterExtensions(filterExt);
            String selected = fd.open();

            game.saveGame(selected);
            currentGameFile = selected;
        };

        final MenuAction jumpForward  = (e) -> {
            for (int i = 0; i< 100; i++) {
                grid.next();
                grid.incrementGenerations();
            }
        };

        final Menu gameMenu = new Menu(shell, SWT.DROP_DOWN);

        UITools.initMenuItem(gameMenu, "&Reset", 0, SWT.PUSH, resetGame);
        UITools.initMenuItem(gameMenu, "&Open\tCtrl+O", SWT.CTRL | 'O', SWT.PUSH, loadGame);
        UITools.initMenuItem(gameMenu, "&Save\tShift+Ctrl+S", SWT.CTRL+ SWT.SHIFT + 'S', SWT.PUSH, saveGame);
        UITools.initMenuItem(gameMenu, "Save &as\tCtrl+S", SWT.SHIFT + 's', SWT.PUSH, saveGameAs);
        UITools.initMenuItem(gameMenu, "Jump 100 steps forward", 0, SWT.PUSH, jumpForward);

        final MenuItem triggerGridItem = UITools.initMenuItem(gameMenu, "Show grid", 0, SWT.CHECK, triggerGrid);
        triggerGridItem.setSelection(true);

        return gameMenu;
    }

    private void initGenerationLabel() {
        genLabel = new Label(shell, SWT.NONE);
        genLabel.setText(String.format(stringConfig.getString("generationLabel") + "%06d", 0));
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        gridData.horizontalIndent = 40;
        genLabel.setLayoutData(gridData);
    }

    private void initSpeedScale() {
        final Scale speedScale = new Scale(shell, SWT.HORIZONTAL);
        speedScale.setMaximum(4);
        speedScale.setMinimum(1);
        speedScale.setIncrement(1);
        speedScale.setPageIncrement(1);
        speedScale.setSelection(2);
        speedScale.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                switch (speedScale.getSelection()) {
                    case 1:{grid.setDelay(800); break;}
                    case 2:{grid.setDelay(400); break;}
                    case 3:{grid.setDelay(150); break;}
                    case 4:{grid.setDelay(20);  break;}
                    default: break;
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    private void initSpeedLabel() {
        final Label speedLabel = new Label(shell, SWT.WRAP);
        speedLabel.setText(stringConfig.getString("speedLabel"));
        GridData speedData = new GridData();
        speedData.horizontalIndent = 40;
        speedLabel.setLayoutData(speedData);
    }

    private void initStepButton(ToolBar mainToolBar) {
        UITools.initImageButton(mainToolBar, "stepImage", SWT.PUSH, () -> {
            stopButton.setSelection(true);
            startButton.setSelection(false);
            grid.setRunState(false);
            game.makeStep();
        });
    }

    private void initStopButton(final ToolBar mainToolBar) {
        this.startButton = UITools.initImageButton(mainToolBar, "pauseImage", SWT.RADIO,
                () -> grid.setRunState(false));
    }

    private void initStartButton(final ToolBar mainToolBar) {
        this.stopButton = UITools.initImageButton(mainToolBar, "playImage", SWT.RADIO,
                () -> grid.setRunState(true));
    }
}
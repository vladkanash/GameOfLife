package gameoflife.ui;

import gameoflife.SavingManager;
import gameoflife.events.ActionManager;
import gameoflife.events.action.ChangeRunStateAction;
import gameoflife.events.listener.GenLabelListener;
import gameoflife.events.listener.StartButtonListener;
import gameoflife.events.listener.StopButtonListener;
import gameoflife.ui.board.GameBoard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

import java.lang.String;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * UI main class
 */
public class GameOfLifeUI {

    private final ActionManager actionManager = ActionManager.INSTANCE;
    private final SavingManager savingManager;
    private final Shell shell;

    private final GameBoard game;

    private final static String BUNDLE_PATH = "gameoflife.config.MainConfig";
    private final ResourceBundle gameConfig = ResourceBundle.getBundle(BUNDLE_PATH);

    public GameOfLifeUI(Shell shell, GameBoard gameBoard) {
        this.shell = shell;
        this.game = gameBoard;
        this.savingManager = new SavingManager(game);

        game.updateZoomOffset();

        initShell();
        initWidgets();
        startDisplayCycle(shell.getDisplay());
    }

    private void startDisplayCycle(Display display) {
        shell.open();
        while (!shell.isDisposed())
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        display.dispose();
    }

    private void initShell() {
        shell.setText(gameConfig.getString("gameTitle"));
        shell.pack();
        
        var initialWidth = (Integer) gameConfig.getObject("initialWidth");
        var initialHeight = (Integer) gameConfig.getObject("initialHeight");
        shell.setSize(initialWidth, initialHeight);

        GridLayout layout = new GridLayout(5, false);
        shell.setLayout(layout);
    }

    private void initWidgets() {
        final ToolBar mainToolBar = new ToolBar(shell, SWT.WRAP | SWT.RIGHT);

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
            game.setRunState(false);

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
        final Menu zoomMenu = new Menu(shell, SWT.DROP_DOWN);

        UITools.initMenuItem(zoomMenu, "Huge", 0, SWT.PUSH, () -> game.zoom(30));
        UITools.initMenuItem(zoomMenu, "Normal", 0, SWT.PUSH, () -> game.zoom(10));
        UITools.initMenuItem(zoomMenu, "Small", 0, SWT.PUSH, () -> game.zoom(4));
        UITools.initMenuItem(zoomMenu, "Pixel cells", 0, SWT.PUSH, () -> game.zoom(1));

        return zoomMenu;
    }

    private Menu initShapesMenu() {
        final Menu shapeMenu = new Menu(shell, SWT.DROP_DOWN);

        final MenuItem glider = new MenuItem(shapeMenu, SWT.PUSH);
        glider.setText("Glider");

        final MenuItem gun = new MenuItem(shapeMenu, SWT.PUSH);
        gun.setText("Gun");

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

        final Consumer<SelectionEvent> resetGame = (e) -> {
            game.resetGame();
            game.setRunState(false);
            actionManager.sendAction(new ChangeRunStateAction(false));
        };

        final Consumer<SelectionEvent> triggerGrid = e -> {
            MenuItem item = (MenuItem) e.widget;
            game.setGridState(item.getSelection());
        };

        final Consumer<SelectionEvent> loadGame = (e) -> {
            game.setRunState(false);
            actionManager.sendAction(new ChangeRunStateAction(false));
            String selected = UITools.createFileDialog(shell, SWT.OPEN).open();
            savingManager.loadGame(selected);
        };

        final Consumer<SelectionEvent> saveGame = (e) -> {
            if (savingManager.getCurrentFilepath() != null) {
                savingManager.saveGame();
                return;
            }
            game.setRunState(false);
            actionManager.sendAction(new ChangeRunStateAction(false));
            String selected = UITools.createFileDialog(shell, SWT.SAVE).open();
            savingManager.saveGameAs(selected);
        };

        final Consumer<SelectionEvent> saveGameAs = (e) -> {
            game.setRunState(false);
            actionManager.sendAction(new ChangeRunStateAction(false));
            var saveDialog = UITools.createFileDialog(shell, SWT.SAVE);
            saveDialog.setFileName(savingManager.getCurrentFilepath());
            String selected = saveDialog.open();
            savingManager.saveGameAs(selected);
        };

        final Consumer<SelectionEvent> jumpForward = (e) -> {
            for (int i = 0; i < 100; i++) {
                game.next();
                game.incrementGenerations();
            }
        };

        final Menu gameMenu = new Menu(shell, SWT.DROP_DOWN);

        UITools.initMenuItem(gameMenu, "&Reset", 0, SWT.PUSH, resetGame);
        UITools.initMenuItem(gameMenu, "&Open\tCtrl+O", SWT.CTRL | 'O', SWT.PUSH, loadGame);
        UITools.initMenuItem(gameMenu, "&Save\tShift+Ctrl+S", SWT.CTRL + SWT.SHIFT + 'S', SWT.PUSH, saveGame);
        UITools.initMenuItem(gameMenu, "Save &as\tCtrl+S", SWT.SHIFT + 's', SWT.PUSH, saveGameAs);
        UITools.initMenuItem(gameMenu, "Jump 100 steps forward", 0, SWT.PUSH, jumpForward);

        final MenuItem triggerGridItem = UITools.initMenuItem(gameMenu, "Show grid", 0, SWT.CHECK, triggerGrid);
        triggerGridItem.setSelection(true);

        return gameMenu;
    }

    private void initGenerationLabel() {
        Label genLabel = new Label(shell, SWT.NONE);
        genLabel.setText(String.format(gameConfig.getString("generationLabel") + "%06d", 0));
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        gridData.horizontalIndent = 40;
        genLabel.setLayoutData(gridData);
        actionManager.addListener(new GenLabelListener(genLabel));
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
                    case 1: {
                        game.setDelay(800);
                        break;
                    }
                    case 2: {
                        game.setDelay(400);
                        break;
                    }
                    case 3: {
                        game.setDelay(150);
                        break;
                    }
                    case 4: {
                        game.setDelay(20);
                        break;
                    }
                    default:
                        break;
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
        speedLabel.setText(gameConfig.getString("speedLabel"));
        GridData speedData = new GridData();
        speedData.horizontalIndent = 40;
        speedLabel.setLayoutData(speedData);
    }

    private void initStepButton(ToolBar mainToolBar) {
        UITools.initImageButton(mainToolBar, "stepImage", SWT.PUSH, () -> {
            actionManager.sendAction(new ChangeRunStateAction(false));
            game.setRunState(false);
            game.makeStep();
        });
    }

    private void initStopButton(final ToolBar mainToolBar) {
        var button = UITools.initImageButton(mainToolBar, "pauseImage", SWT.RADIO,
                () -> game.setRunState(false));
        actionManager.addListener(new StopButtonListener(button));
    }

    private void initStartButton(final ToolBar mainToolBar) {
        var button = UITools.initImageButton(mainToolBar, "playImage", SWT.RADIO,
                () -> game.setRunState(true));
        actionManager.addListener(new StartButtonListener(button));
    }
}
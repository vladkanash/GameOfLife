package GameOfLife;


/**
 * Created by Vlad Kanash on 18.02.2015.
 */


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import  org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
//import scala.Tuple2;

import java.lang.String;
import java.util.List;
import java.text.DecimalFormat;
import java.util.ListIterator;


/**
 * App Main class
 */
public class Main
{

    final static String PlayImagePath = "Play.png";   //
    final static String StopImagePath = "Pause.png";  // Button images
    final static String StepImagePath = "Step.png";   //

    /**
     * App window
     */
    private Shell shell;

    /**
     * Label for the number of generations since the beginning of the game.
     */
    private Label genLabel;

    /**
     * Game field. Extends swt.canvas
     */
    private GameBoard game;


    /**
     * File name for the current pattern (if it was loaded from a file)
     */
    private String currentGameFile;

    /**
     * File name fo the current scenario (if it was loaded from a file)
     */
    private String currentScenario;

    /**
     * Contains information about the scenario file
     */
//    private ScalaAnalyzer fileInfo;

    /**
     * Cell grid contains game logic (server part).
     */
    private CellGrid grid;



    /**
     *
     * @param display parent display
     */
    public Main(Display display, CellGrid grid)
    {

        shell = new Shell(display);
        this.grid = grid;


        shell.setText("Game of Life");
        shell.pack();
        shell.setSize(800, 600);                          //Initial window size

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


    /**
     * Interface init (SWT widgets)
     */
    public void initWidgets()
    {

        //Toolbar buttons
        ToolBar mainToolBar = new ToolBar(shell, SWT.WRAP | SWT.RIGHT );

        final ToolItem start = new ToolItem(mainToolBar, SWT.RADIO);
        ImageData iib = new ImageData(PlayImagePath);
        start.setImage(new Image(shell.getDisplay(), iib));
        start.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (game.isScnRunning()) game.setScnPause(false);
                else grid.setRunState(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {

            }
        });

        final ToolItem stop = new ToolItem(mainToolBar, SWT.RADIO);
        ImageData iis = new ImageData(StopImagePath);
        stop.setImage(new Image(shell.getDisplay(), iis));
        stop.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (game.isScnRunning()) game.setScnPause(true);
                else grid.setRunState(false);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {

            }
        });

        ToolItem step = new ToolItem(mainToolBar, SWT.PUSH);
        ImageData stepIm = new ImageData(StepImagePath);
        step.setImage(new Image(shell.getDisplay(), stepIm));
        step.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                stop.setSelection(true);
                start.setSelection(false);

                if (game.isScnRunning()) game.setScnPause(true);
                else grid.setRunState(false);

                game.makeStep();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }
        });



        //"Speed: " label
        Label speedLabel = new Label(shell, SWT.WRAP);
        speedLabel.setText("Speed: ");
        GridData speedData = new GridData();
        speedData.horizontalIndent = 40;
        speedLabel.setLayoutData(speedData);



        //Speed scale
        final Scale speedScale = new Scale(shell, SWT.HORIZONTAL);
        speedScale.setMaximum(4);
        speedScale.setMinimum(1);
        speedScale.setIncrement(1);
        speedScale.setPageIncrement(1);
        speedScale.setSelection(2);
        speedScale.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                switch (speedScale.getSelection())
                {
                    case 1:{grid.setDelay(800); break;}
                    case 2:{grid.setDelay(400); break;}
                    case 3:{grid.setDelay(150); break;}
                    case 4:{grid.setDelay(20);  break;}
                    default: break;
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }
        });



        //Generations label
        genLabel = new Label(shell, SWT.NONE);
        genLabel.setText(String.format("Generation: %06d", 0));
        //Generations.setFont(new Font(shell.getDisplay(), "Arial", 9, SWT.BOLD));
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        gridData.horizontalIndent = 40;
        genLabel.setLayoutData(gridData);



        //Main menu
        Menu mainMenu =  new Menu(shell, SWT.BAR | SWT.LEFT_TO_RIGHT);   //Main Menu

        MenuItem Game = new MenuItem(mainMenu, SWT.CASCADE);    //"Game" submenu
        Game.setText("&Game");

        MenuItem Scenario = new MenuItem(mainMenu, SWT.CASCADE);    //"Scenario" submenu
        Scenario.setText("&Scenario");

        MenuItem Shapes = new MenuItem(mainMenu, SWT.CASCADE);    //Saved patterns
        Shapes.setText("&Patterns");

        MenuItem Zoom = new MenuItem(mainMenu, SWT.CASCADE);    //"Zoom" submenu
        Zoom.setText("&Zoom");

        MenuItem Help = new MenuItem(mainMenu, SWT.PUSH);    //"Help" submenu
        Help.setText("&Help");
        Help.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                MessageBox msg = new MessageBox(shell, SWT.NONE);
                msg.setMessage("No help yet");
                msg.open();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });




        Menu gameMenu = new Menu(shell, SWT.DROP_DOWN);

        //Add client button
        MenuItem demo = new MenuItem(gameMenu, SWT.PUSH);      //Reset game
        demo.setText("&Add client (DEMO)");
        demo.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {

                Thread runnable = new Thread("New thread")    //Новый поток
                {
                    public void run()
                    {
                        Display disp = new Display();
                        new Main(disp, grid);

                    }
                };

                runnable.start();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });


        MenuItem reset = new MenuItem(gameMenu, SWT.PUSH);      //Reset game
        reset.setText("&Reset");
        reset.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                game.resetGame();
                grid.setRunState(false);
                start.setSelection(false);
                stop.setSelection(true);
                //game.redraw();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });


        final MenuItem NoGrid = new MenuItem(gameMenu, SWT.CHECK);   //Draw grid on/off
        NoGrid.setText("Show grid");
        NoGrid.setSelection(true);
        NoGrid.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                game.setGridState(NoGrid.getSelection());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }
        });


        MenuItem load = new MenuItem(gameMenu, SWT.PUSH);     //Load game from file
        load.setText("&Open\tCtrl+O");
        load.setAccelerator(SWT.CTRL | 'O');
        load.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetSelected(SelectionEvent e)
            {
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
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        final MenuItem save = new MenuItem(gameMenu, SWT.PUSH);   //Save the game
        save.setText("&Save\tShift+Ctrl+S");
        save.setAccelerator(SWT.CTRL+ SWT.SHIFT + 'S');
        save.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (currentGameFile != null)
                {
                    game.saveGame(currentGameFile);
                    return;
                }

                stop.setSelection(true);

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
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }
        });


        final MenuItem saveAs = new MenuItem(gameMenu, SWT.PUSH);   //Save the game
        saveAs.setText("Save &as\tCtrl+S");
        saveAs.setAccelerator(SWT.SHIFT + 's'); //TODO Accelerators!!
        saveAs.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                stop.setSelection(true);

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
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }
        });



        final MenuItem jump = new MenuItem(gameMenu, SWT.PUSH);   //Save the game
        jump.setText("Jump 100 steps forward");
        jump.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {

                for (int i = 0; i< 100; i++)
                {
                    grid.next();
                    grid.incrementGenerations();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }
        });


        Menu ScenarioMenu = new Menu(shell, SWT.DROP_DOWN);


        final MenuItem replay = new MenuItem(ScenarioMenu, SWT.PUSH);   //Save the game
        replay.setEnabled(false);
        replay .setText("Replay");
        replay .addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                game.replayScenario();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }
        });

        final MenuItem showInfo = new MenuItem(ScenarioMenu, SWT.PUSH);
        showInfo.setEnabled(false);
        showInfo.setText("File information");
        showInfo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                showFileInfoBox();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });


        final MenuItem Rec = new MenuItem(ScenarioMenu, SWT.CHECK);   //Save the game
        Rec.setSelection(false);
        Rec.setText("Record");
        Rec.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                game.setRecordState(Rec.getSelection());
                stop.setSelection(true);
                start.setSelection(false);
                grid.setRunState(false);

                if (Rec.getSelection())
                {

                    Zoom.setEnabled(false);
                    game.stopScenarioPlaying();
                }

                else
                {
                    FileDialog fd = new FileDialog(shell, SWT.SAVE);
                    fd.setFilterPath("C:/");
                    fd.setOverwrite(true);

                    String[] filterExt = { "*.scn" };
                    String[] extName = {"Game of Life scenario (*.scn)"};

                    fd.setFileName(currentGameFile);
                    fd.setFilterNames(extName);
                    fd.setFilterExtensions(filterExt);
                    String selected = fd.open();

                    if (selected == null) return;

                    game.saveScenario(selected);

                    currentScenario = selected;

                    game.setRecordState(Rec.getSelection());

                    replay.setEnabled(true);
                    showInfo.setEnabled(true);
                    Zoom.setEnabled(true);

//                    fileInfo = new ScalaAnalyzer(selected);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }
        });


        final MenuItem Run = new MenuItem(ScenarioMenu, SWT.PUSH);
        Run.setSelection(false);
        Run.setText("Load");
        Run.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                fd.setFilterPath("C:/");
                fd.setOverwrite(true);
                String[] filterExt = {"*.scn"};
                String[] extName = {"Game of Life scenario (*.scn)"};
                fd.setFilterNames(extName);
                fd.setFilterExtensions(filterExt);


                String selected = fd.open();

                if (selected == null) return;

                // Saving name of the file
                currentScenario = selected;

                replay.setEnabled(true);
                showInfo.setEnabled(true);


                //Collecting info about the file (may take some time)
//                fileInfo = new ScalaAnalyzer(selected);
                showFileInfoBox();

                game.runScenario(selected);
                //sp.setVisible(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });


        final MenuItem startPlay = new MenuItem(ScenarioMenu, SWT.PUSH);   //Save the game
        startPlay .setText("Start play");
        startPlay .addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                game.stopScenarioPlaying();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });



        Scenario.setMenu(ScenarioMenu);


        shell.addListener(SWT.Close, event -> {
                stop.setSelection(true);

                MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
                messageBox.setMessage("Save the game before exit?");
                messageBox.setText("Exiting Application");
                int response = messageBox.open();
                if (response == SWT.YES)
                {

                    event.doit = false;

                    FileDialog fd = new FileDialog(shell, SWT.SAVE);
                    fd.setFilterPath("C:/");
                    fd.setOverwrite(true);
                    String[] filterExt = {"*.gol"};
                    String[] extName = {"Game of Life pattern (*.gol)"};
                    fd.setFilterNames(extName);
                    fd.setFilterExtensions(filterExt);
                    String selected = fd.open();
                    game.saveGame(selected);

                } else if (response == SWT.CANCEL) {
                    event.doit = false;
                }
        });

        Game.setMenu(gameMenu);


        //Saved patterns
        Menu shapeMenu  = new Menu(shell, SWT.DROP_DOWN );

        MenuItem glider = new MenuItem(shapeMenu, SWT.PUSH);
        glider.setText("Glider");

        MenuItem gun    = new MenuItem(shapeMenu, SWT.PUSH);
        gun.setText("Gun");
         //TODO: More shapes

        Shapes.setMenu(shapeMenu);


        Menu zoomMenu = new Menu(shell, SWT.DROP_DOWN );

        MenuItem huge = new MenuItem(zoomMenu, SWT.PUSH);
        huge. setText ("Huge");
        huge.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                game.zoom(30);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                game.zoom(30);
            }
        });

        MenuItem normal = new MenuItem(zoomMenu, SWT.PUSH);
        normal. setText ("Normal");
        normal.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                game.zoom(10);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                game.zoom(10);
            }
        });

        MenuItem tiny = new MenuItem(zoomMenu, SWT.PUSH);
        tiny. setText ("Tiny");
        tiny.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                game.zoom(4);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                game.zoom(4);
            }
        });

        MenuItem pixel = new MenuItem(zoomMenu, SWT.PUSH);
        pixel. setText ("Pixel cells");
        pixel.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                game.zoom(1);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                game.zoom(1);
            }
        });


        Zoom.setMenu(zoomMenu);

        shell.setMenuBar(mainMenu);

    }

    private void showFileInfoBox()
    {
        MessageBox msg = new MessageBox(shell, SWT.OK);

        String[] filename = currentScenario.split("\\\\");

        msg.setText(filename[filename.length-1] + " info");

        DecimalFormat df = new DecimalFormat("#.###");


//        msg.setMessage(String.format("Total games: " + fileInfo.gamesCount() +
//                        "\nLongest game: #" + fileInfo.longestGameNum() + " (" + fileInfo.longestGameLength() + " generations)" +
//                        "\nShortest game: #" + fileInfo.shortestGameNum() + " (" + fileInfo.shortestGameLength() + " generations)" +
//                        "\nAverage game length: %s generations" +
//                        "\nAverage number of cells placed during the game: %s cells",
//                df.format(fileInfo.averageGameLength()),
//                df.format(fileInfo.averageCellsPlaced())));
//
//        msg.open();

//        showNotationTree();
    }

//    /**
//     * Show the information about the saved notation in a tree
//     */
//    private void showNotationTree()
//    {
//        int index = 1;
//
//        List<String> stepList = fileInfo.getNotation(index);
//        List<List<Tuple2<Integer, Integer>>> cells = fileInfo.getCellsNotation(index);
//
//        final Shell shell = new Shell(this.shell.getDisplay());
//
//        shell.setLayout(new FillLayout());
//        shell.setText("Saved Notation");
//
//        Tree tree = new Tree(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
//        tree.setHeaderVisible(true);
//
//        ListIterator<List<Tuple2<Integer, Integer>>> cellsIter;
//        ListIterator<String> stepsIter;
//
//
//        for (stepsIter = stepList.listIterator(), cellsIter = cells.listIterator(); cellsIter.hasNext(); )
//        {
//            TreeItem item = new TreeItem(tree, SWT.NONE);
//            item.setText(stepsIter.next());
//
//            for (Tuple2<Integer, Integer> cell : cellsIter.next())
//            {
//                TreeItem subItem = new TreeItem(item, SWT.NONE);
//                subItem.setText("at X: " + cell._1() + ", Y: " + cell._2());
//            }
//        }
//
//        //Last generation string
//        TreeItem item = new TreeItem(tree, SWT.NONE);
//        item.setText(stepList.get(stepList.size() - 1));
//
//        shell.pack();
//        shell.open();
//    }



    /**
     * main()
     * @param args not used
     */
    public static void main(String[] args)
    {
        Display display = new Display();


        //game logic thread
        CellGrid server= new CellGrid();
        Thread t =  new Thread(server, "Game logic thread");
        t.setDaemon(true);
        t.start();


        //GUI
        new Main(display, server);

    }

}







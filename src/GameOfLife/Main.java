package GameOfLife;


/**
 * Created by Vlad Kanash on 18.02.2015.
 */


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import  org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import java.lang.String;



/**
 * App Main class
 */
public class Main
{

    final static String PlayImagePath = "C:\\Users\\user\\IdeaProjects\\Play.png";   //
    final static String StopImagePath = "C:\\Users\\user\\IdeaProjects\\Pause.png";  // Button images
    final static String StepImagePath = "C:\\Users\\user\\IdeaProjects\\Step.png";   //

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
     * File name for the current pattern (if it was loaded from the file)
     */
    private String currentGameFile;


    /**
     *
     * @param display parent display
     */
    public Main(Display display)
    {

        shell = new Shell(display);

        shell.setText("Game of Life");
        shell.pack();
        shell.setSize(800, 600);                          //Initial window size

        GridLayout layout = new GridLayout(4, false);
        shell.setLayout(layout);


        initWidgets();

        game = new GameBoard(shell, genLabel);


        shell.open();

        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
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
                game.setRunState(true);
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
                game.setRunState(false);
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
                game.setRunState(false);
                game.nextGeneration();

                stop.setSelection(true);
                start.setSelection(false);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                game.setRunState(false);
                game.nextGeneration();

                stop.setSelection(true);
                start.setSelection(false);
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
                    case 1:{game.setDelay(800); break;}
                    case 2:{game.setDelay(400); break;}
                    case 3:{game.setDelay(150); break;}
                    case 4:{game.setDelay(20);  break;}
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
                MessageBox msg = new MessageBox(shell, SWT.NONE);
                msg.setMessage("No help yet");
                msg.open();
            }
        });




        Menu gameMenu = new Menu(shell, SWT.DROP_DOWN);

        MenuItem reset = new MenuItem(gameMenu, SWT.PUSH);      //Reset game
        reset.setText("&Reset");
        reset.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                game.resetGame();
                game.setRunState(false);
                start.setSelection(false);
                stop.setSelection(true);
                game.redraw();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                game.resetGame();
                game.setRunState(false);
                start.setSelection(false);
                stop.setSelection(true);
                game.redraw();
            }
        });

        final MenuItem NoGrid = new MenuItem(gameMenu, SWT.CHECK);   //Draw grid on/off
        NoGrid.setText("Show grid");
        NoGrid.setSelection(true);
        NoGrid.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                game.setGridState(NoGrid.getSelection());
                game.redraw();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                game.setGridState(NoGrid.getSelection());
                game.redraw();
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





        shell.addListener(SWT.Close, new Listener()                           //save request before exiting
        {
            public void handleEvent(Event event)
            {
                if (!game.isSaved())
                {
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

                    } else if (response == SWT.CANCEL)
                    {
                        event.doit = false;
                    }
                }
            }
        });

        Game.setMenu(gameMenu);


        Menu shapeMenu  = new Menu(shell, SWT.DROP_DOWN );         //Saved patterns

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


    /**
     * main()
     * @param args not used
     */
    public static void main(String[] args)
    {
        Display display = new Display();
        new Main(display);
        display.dispose();
    }

}







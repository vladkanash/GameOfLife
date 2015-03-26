package GameOfLife;


/**
 * Created by user on 18.02.2015.
 */


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import  org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;



/**
 * Основной класс приложения
 */
public class Main
{

    /**
     * Окно приложения
     */
    private Shell shell;

    /**
     * Счетчик поколений, прошедших от начала игры
     */
    private Label genLabel;     //Количество поколений (передается в game)

    /**
     * Поле для игры
     */
    private GameBoard game;     //Поле для игры

    /**
     *
     * @param display parent display
     */
    public Main(Display display)
    {

        shell = new Shell(display);

        shell.setText("Game of Life");
        shell.pack();
        shell.setSize(600, 400);                          //Начальный размер окна

        GridLayout layout = new GridLayout(4, false);
        shell.setLayout(layout);


        initWidgets();

        game = new GameBoard(shell, genLabel);


        shell.open();

        while (!shell.isDisposed())                       //Цикл обработки событий
            if (!display.readAndDispatch())
                display.sleep();
    }


    /**
     * Создание интерфейса приложения.
     */
    public void initWidgets()
    {
        //Toolbar buttons
        ToolBar mainToolBar = new ToolBar(shell, SWT.WRAP | SWT.RIGHT );

        final ToolItem start = new ToolItem(mainToolBar, SWT.RADIO);
        ImageData iib = new ImageData("C:\\Users\\user\\IdeaProjects\\Play.png");
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
        ImageData iis = new ImageData("C:\\Users\\user\\IdeaProjects\\Pause.png");
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
        ImageData stepIm = new ImageData("C:\\Users\\user\\IdeaProjects\\Step.png");
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



        //"Speed: "
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
                switch (speedScale.getSelection())
                {
                    case 1:{game.setDelay(800); break;}
                    case 2:{game.setDelay(400); break;}
                    case 3:{game.setDelay(150); break;}
                    case 4:{game.setDelay(50);  break;}
                    default: break;
                }
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




        Menu mainMenu =  new Menu(shell, SWT.BAR | SWT.LEFT_TO_RIGHT);   //Главное меню

        MenuItem Game = new MenuItem(mainMenu, SWT.CASCADE);    //Меню игры
        Game.setText("&Game");


        //MenuItem Shapes = new MenuItem(mainMenu, SWT.CASCADE);    //Сохраненные паттерны
        //Shapes.setText("&Patterns");

        MenuItem Zoom = new MenuItem(mainMenu, SWT.CASCADE);    //Управление масштабированием
        Zoom.setText("&Zoom");

        MenuItem Help = new MenuItem(mainMenu, SWT.PUSH);    //Помощь
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

        MenuItem reset = new MenuItem(gameMenu, SWT.PUSH);      //Сброс поля
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

        final MenuItem NoGrid = new MenuItem(gameMenu, SWT.CHECK);   //Вкл/выкл отображение сетки
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

        Game.setMenu(gameMenu);


        Menu shapeMenu  = new Menu(shell, SWT.DROP_DOWN );         //Сохраненные паттерны

       // MenuItem glider = new MenuItem(shapeMenu, SWT.PUSH);
       // glider.setText("Glider");
       // MenuItem gun    = new MenuItem(shapeMenu, SWT.PUSH);
        //gun.setText("Gun");
        // TODO: More shapes

        //Shapes.setMenu(shapeMenu);


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

        shell.setMenuBar(mainMenu);                       //Установка главного меню

    }


    /**
     * main()
     * @param args не используется
     */
    public static void main(String[] args)
    {
        Display display = new Display();
        new Main(display);
        display.dispose();
    }

}







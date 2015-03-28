package GameOfLife;


/**
 * Created by Vlad Kanash on 24.02.2015.
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.internal.gdip.Rect;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Label;
import java.util.Enumeration;

/**
 * Представляет поле, на котором рисуются клетки игры
 */
public class GameBoard extends Canvas
{

    /**
     * Начальный размер поля в клетках.
     */
    private final int START_ROWS = 2200;

    /**
     * Начальный размер поля в клетках.
     */
    private final int START_COLUMNS = 1400;


    /**
     * true - разрешена отрисовка сетки на поле
     */
    private boolean gridState = true;

    /**
     * true - игровой цикл запущен
     * false - игра на паузе
     */
    private boolean inGame = true;

    /**
     * Текущий размер клетки (в пикселях)
     */
    private int cellSize = 15;


    /**
     * Задержка между сменой поколений
     */
    private int delay = 400;


    /**
     * Ссылка на объект Label, на котором отображается текущее количество поколений
     */
    private Label genLabel;

    /**
     * Parent display
     */
    private Display display;


    /**
     * Массив клеток поля
     */
    private CellGrid cellGrid;


    /**
     * Цвет фона поля
     */
    private Color BackgroundColor;

    /**
     * Цвет сетки на поле
     */
    private Color ForegroundColor;


    /**
     * Смещение масштабирования. Необходимо для того, чтобы вне зависимости от выбранного масштаба
     * на экране находилась центральная часть игрового поля.
     */
    private Rect zoomOffset;

    /**
     * Рисунок сетки. Сохраняется, т.к. нет необходимости перерисовывать в каждом кадре
     */
    private Image gridImage;


    /**
     * True if the current pattern was not changed after the last save.
     */
    private boolean gameSaved = true;


    //public enum speedValues


    /**
     * @param shell Parent Shell
     * @param label Надпись, в которую выводится текущее поколение игры
     */
    public GameBoard(Shell shell, Label label)
    {
        super(shell, SWT.BORDER | SWT.NO_BACKGROUND);


        zoomOffset = new Rect();

        BackgroundColor = new Color(shell.getDisplay(), 20, 20, 20);
        ForegroundColor = new Color(shell.getDisplay(), 0, 200, 0);


        this.genLabel = label;
        display = shell.getDisplay();


        cellGrid = new CellGrid(START_ROWS, START_COLUMNS);


        //Layout information
        GridData data = new GridData();
        data.horizontalSpan = 4;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        setLayoutData(data);


        this.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
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

                imageGC.drawImage(gridImage, 0, 0);   //Рисуем сетку


                drawCells(imageGC); //Рисуем клетки


                e.gc.drawImage(image,0,0);   //Выводим изображение на экран

                imageGC.dispose();
                e.gc.dispose();

            }
        });

        Listener mouseListener = new Listener()
        {

            private boolean mousePressed = false;

            public void handleEvent(Event event)
            {
                switch (event.type)
                {
                    case SWT.MouseDown:
                        mousePressed = true;
                        drawCell(event.x, event.y);
                        break;

                    case SWT.MouseMove:
                        if (mousePressed)
                            drawCell(event.x, event.y);
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

        this.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                updateZoomOffset();
            }
        });


        this.setBackground(BackgroundColor);

        initGame(shell.getDisplay());   //Запускает игровой цикл
    }



    /**
     * Создает новый поток, в котором запускает игровой цикл.
     *
     * @param display Parent display
     */
    private void initGame(final Display display) {

        updateZoomOffset();


        this.setRunState(false);

        for (int i = 800; i<1400; i++)
        {
            cellGrid.setCell(i, 700,true);       //Паттерн The Line
        }


        //Создаем новый поток
        Runnable runnable = new Runnable()    //Новый поток
        {
            public void run()
            {
                if (inGame)         //Если игра не на паузе
                {
                    cellGrid.next(zoomOffset);  //Переход к следующему поколению
                    redraw();         //Выводим на экран (см. PaintListener)
                    updateGenLabel(); //Обновляем счетчик поколений
                    gameSaved = false; //Pattern has changed after last save
                }

                display.timerExec(delay, this); //delay between next() calls

            }
        };

        display.timerExec(delay, runnable);
    }


    /**
     * Распологает 1 клетку на сетке по координатам Х и У
     * X, Y - координаты клетки в пикселях внутри объекта GameOfLife.GameBoard
     * @param x Х-координата
     * @param y У-координата
     */
    private void drawCell(int x, int y)
    {

       if (inGame) return;
            cellGrid.setCell(x / cellSize + zoomOffset.X, y / cellSize + zoomOffset.Y, true/*!cellUnderMouse*/ );
            redraw();
    }




    /**
     *Обновляет рисунок сетки. (при изменении размера окна, масштабировании и т.п.)
     */
    private void updateGridImage()
    {
        gridImage = new Image( display, getSize().x, getSize().y);

        GC gridGC = new GC(gridImage);

        gridGC.setBackground(BackgroundColor);
        gridGC.setAntialias(SWT.ON);

        Rectangle imageSize = gridImage.getBounds();
        gridGC.fillRectangle(0, 0, imageSize.width + 1, imageSize.height + 1);

        drawGrid(gridGC); //Рисуем сетку
        gridGC.dispose();
    }



    /**
     * Рисует сетку на экране.
     * @param e объект GC для рисования сетки
     */
    private void drawGrid(GC e)
    {
        //Если рисование сетки отменено то выходим
        if (!gridState) return;

        for (int i=0; i < getSize().x; i+= cellSize)
            e.drawLine(i, 0, i, getSize().y);

        for (int i=0; i < getSize().y; i+= cellSize)
            e.drawLine(0, i, getSize().x, i);

    }


    /**
     * Рисует "живые" клетки на экране
     * @param e объект GC для рисования клеток
     */
    private void drawCells(GC e)
    {
        //fillRectangle использует цвет фона для заливки
        e.setBackground(ForegroundColor);

        Enumeration Enum = cellGrid.getEnum();
        Cell c;
        while ( Enum.hasMoreElements() )
        {
            c = (Cell) Enum.nextElement();
            e.fillRectangle((c.col-zoomOffset.X) * cellSize, (c.row - zoomOffset.Y)* cellSize, cellSize, cellSize);
        }

       e.setBackground(BackgroundColor);
    }

    /**
     * Возвращает количество поколений, прошедших от начала игры
     * @return количество поколений
     */
    private int getGeneration()
    {
        return cellGrid.getGenerations();
    }


    /**
     * Вычисляет значение смещения для масштабирования камеры на поле
     */
    private void updateZoomOffset()
    {
        this.zoomOffset.X = (int) (0.5 * (START_ROWS - getSize().x / cellSize));
        this.zoomOffset.Y = (int) (0.5 * (START_COLUMNS - getSize().y / cellSize));
        this.zoomOffset.Width = getSize().x / cellSize;
        this.zoomOffset.Height = getSize().y / cellSize;
    }

    /**
     * Меняет размер клетки, перерисовывает сетку
     *
     * @param newCellSize новый размер клетки (в пикселях)
     */
    public void zoom(int newCellSize)
    {
        cellSize = newCellSize;
        updateZoomOffset();
        updateGridImage();
        redraw();
    }

    /**
     * Устанавливает состояние игры
     * @param state true - игра работает
     *              false - игра на паузе
     */
    public void setRunState(boolean state)
    {
        this.inGame = state;
    }


    /**
     * Очищает поле, обнуляет счетчик поколений
     */
    public void resetGame()
    {
        this.cellGrid.clear();
        updateGenLabel();
    }


    /**
     * Устанавливает задержку между сменой поколений
     * @param delay время задержки (милисекунды)
     */
    public void setDelay(int delay)
    {
        this.delay = delay;
    }


    /**
     * Переход к следующему поколению и обновление счетчика поколений.
     */
    public void nextGeneration()
    {
        cellGrid.next(zoomOffset);
        updateGenLabel();
        redraw();
        gameSaved = false;
    }


    /**
     * Включить/выключить отрисовку сетки на экране
     * @param newState true - отрисовывать сетку
     *                 false - не отрисовывать сетку
     */
    public void setGridState(boolean newState)
    {
        this.gridState = newState;
        updateGridImage();
    }

    /**
     * Save current pattern
     * @param fileName .gol file name
     */
    public void saveGame(String fileName)
    {
        cellGrid.save(fileName);
        gameSaved = true;
    }

    /**
     * Load pattern from file
     * @param fileName .gol file name
     */
    public void loadGame(String fileName)
    {
        cellGrid.load(fileName);
        updateGenLabel();
        redraw();

    }

    public boolean isSaved()
    {
        return gameSaved;
    }


    /**
     * Update the generations label
     */
    public void updateGenLabel()
    {
        genLabel.setText(String.format("Generation: %06d", getGeneration()));
    }
}



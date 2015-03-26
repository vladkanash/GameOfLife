package GameOfLife;


/**
 * Created by user on 04.03.2015.
 */


import java.util.Enumeration;
import java.util.Hashtable;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.gdip.Rect;

/**
 * Содержит в себе игровые клетки, а также управляет игровой логикой
 */
public class CellGrid
{
    /**
     * Количество поколений, прошедших от начала игры.
     */
    private int generations;


    /**
     * Хэш-таблица, содержащая текущие живые клетки.
     */
    private Hashtable currentShape;


    /**
     * Вспомогательная хэш-таблица, необходима для работы next()
     */
    private Hashtable nextShape;


    /**
     * Двумерный массив клеток. Заполняется при начале игры, остается неизменным до конца
     * работы приложения.
     */
    private Cell[][] grid;


    /**
     * Массив координат соседей клетки.
     * Необходимо для работы next()
     */
    private  Point[] neighbours;


    /**
     * @param cellCols ширина поля (в клетках)
     * @param cellRows высота поля (в клетках)
     */
    public CellGrid(int cellCols, int cellRows)
    {
        this.generations = 0;
        currentShape = new Hashtable();
        nextShape = new Hashtable();

        grid = new Cell[cellCols][cellRows];  //Создаем обьекты клеток и заполняем массив
        for ( int c=0; c<cellCols; c++)
            for ( int r=0; r<cellRows; r++ )
                grid[c][r] = new Cell( c, r );


        neighbours = new Point[8];

        for (int i=0; i<8; i++)
            neighbours[i] = new Point(0,0);


    }

    /**
     * Очищает счетчик поколений и хэш таблицы.
     */
    public synchronized void clear() {
        generations = 0;
        currentShape.clear();
        nextShape.clear();
    }

    /**
     * Переход к следующему поколению. Реализует логику игры
     * @param offset Границы той части поля, которая отрисовывается на экране в данный момент.
     */
    public synchronized void next(Rect offset) {

        Cell cell;
        int col, row;
        Enumeration Enum;

        //Увеличиваем счетчик поколений
        generations++;
        nextShape.clear();


        //Обнуляем соседей у всех живых клеток
        Enum = currentShape.keys();
        while ( Enum.hasMoreElements() )
        {
            cell = (Cell) Enum.nextElement();
            cell.neighbour = 0;
        }


        //Заполняем nextShape
        Enum = currentShape.keys();
        while ( Enum.hasMoreElements() )
        {
            cell = (Cell) Enum.nextElement();
            col = cell.col;
            row = cell.row;


            //Координаты соседей клеток
            neighbours[0].x = col-1;
            neighbours[0].y = row-1;

            neighbours[1].x = col;
            neighbours[1].y = row-1;

            neighbours[2].x = col+1;
            neighbours[2].y = row-1;

            neighbours[3].x = col-1;
            neighbours[3].y = row;

            neighbours[4].x = col+1;
            neighbours[4].y = row;

            neighbours[5].x = col-1;
            neighbours[5].y = row+1;

            neighbours[6].x = col;
            neighbours[6].y = row+1;

            neighbours[7].x = col+1;
            neighbours[7].y = row+1;


            //Для поля в виде тора
            if (col == offset.X )
            {
                neighbours[0].x += offset.Width;
                neighbours[3].x += offset.Width;
                neighbours[5].x += offset.Width;
            }

            if (col == (offset.X + offset.Width - 1))
            {
                neighbours[2].x -= offset.Width;
                neighbours[4].x -= offset.Width;
                neighbours[7].x -= offset.Width;
            }

            if (row == offset.Y)
            {
                neighbours[0].y += offset.Height;
                neighbours[1].y += offset.Height;
                neighbours[2].y += offset.Height;
            }

            if (row == (offset.Y + offset.Height - 1))
            {
                neighbours[5].y -= offset.Height;
                neighbours[6].y -= offset.Height;
                neighbours[7].y -= offset.Height;
            }



            for (int i=0; i<8; i++)
                addNeighbour(neighbours[i]);
        }


        //Первое правило игры
        Enum = currentShape.keys();
        while ( Enum.hasMoreElements() )
        {
            cell = (Cell) Enum.nextElement();

            if ( cell.neighbour != 3 && cell.neighbour != 2)

            {
                currentShape.remove( cell );
            }
        }

        //Второе правило игры
        Enum = nextShape.keys();
        while ( Enum.hasMoreElements() )
        {
            cell = (Cell) Enum.nextElement();

            if ( cell.neighbour == 3 )
            {
                setCell( cell.col, cell.row, true );
            }
        }



        //Для поля в виде тора
        Enum = currentShape.keys();
        while( Enum.hasMoreElements() )
        {
            cell = (Cell)Enum.nextElement();

            if      (cell.col < offset.X || cell.col > (offset.X + offset.Width) ||
                    (cell.row < offset.Y || cell.row > (offset.Y + offset.Height)))
            {
                cell.neighbour = 0;
                currentShape.remove(cell);
            }
        }
    }

    /**
     * Добавляет соседа к указанной клетке.
     * Также добавляет клетку в таблицу nextShape если ее там нет.
     * Необходимо для работы next()
     *
     * @param pos координаты клетки, к которой необходимо добавить соседа
     */
    private synchronized void addNeighbour(Point pos)
    {
        try {
            Cell cell = (Cell) nextShape.get(grid[pos.x][pos.y]);
            if (cell == null)        //Клетки нет в таблице
            {
                Cell c = grid[pos.x][pos.y];
                c.neighbour = 1;       //1 сосед - та клетка, которая вызвала метод
                nextShape.put(c, c);

            } else                     //Клетка уже есть в таблице
            {
                cell.neighbour++;      //Новый сосед - та клетка, которая вызвала метод
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            //Ignore
        }
    }

    /**
     * Возвращает перечисление текущих живых клеток
     * @return текущие живые клетки
     */
    public Enumeration getEnum() {
        return currentShape.keys();
    }

    /**
     * Получить значение клетки по указанным координатам
     * @param col x-coordinate of cell
     * @param row y-coordinate of cell
     * @return true - клетка с этими координатами жива
     */
    public synchronized boolean getCell( int col, int row ) {
        try {
            return currentShape.containsKey(grid[col][row]);
        } catch (ArrayIndexOutOfBoundsException e) {
            // ignore
        }
        return false;
    }

    /**
     * Устанавливает клетку на поле по координатам X и Y
     * @param col X
     * @param row Y
     * @param c Состояние клетки. true - живая клетка
     *                            false - мертвая клетка
     */
    public synchronized void setCell( int col, int row, boolean c )
    {
        try
        {
            Cell cell = grid[col][row];
            if ( c )
            {
                currentShape.put(cell, cell);
            } else
            {
                currentShape.remove(cell);
            }
        } catch (ArrayIndexOutOfBoundsException e)
        {
            ////
        }
    }

    /**
     * Получить количество поколений, прошедших от начала игры
     * @return Количество поколений
     */
    public int getGenerations()
    {
        try {
            return generations;
        }catch (NullPointerException e)
        {
            return 0;
        }
    }


}

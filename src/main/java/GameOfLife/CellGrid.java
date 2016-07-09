package GameOfLife;


/**
 * Created by Vlad Kanash on 04.03.2015.
 */
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.gtk.GdkRectangle;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;



/**
 * Contains game logic and game cells
 */
public class CellGrid implements Runnable
{
    /**
     * Initial rows count of the field.
     */
    public final static int gridRows = 1400;

    /**
     * Initial columns count of the field.
     */
    public final static int gridColumns = 2200;


    /**
     * Generations since the game has started
     */
    private int generations;


    /**
     * Current living cells
     */
    private Hashtable<Cell, Cell> currentShape;


    /**
     * Additional hashtable, used in next() method
     */
    private Hashtable<Cell, Cell> nextShape;


    /**
     * Array of game cells
     */
    private final Cell[][] grid;


    /**
     * Array of a cell's neighbours. Need for next() method.
     */
    private final Point[] neighbours;

    /**
     * False = game is paused.
     * True  = game is running.
     */
    private volatile boolean inGame = true;

    /**
     * Delay between generations.
     */
    private volatile int delay = 400;


    /**
     * Shows the area of the game field which is on the screen at the moment.
     */
    private volatile GdkRectangle zoomOffset;


    public CellGrid()
    {
        zoomOffset = new GdkRectangle();
        this.generations = 0;
        currentShape = new Hashtable<>();
        nextShape = new Hashtable<>();

        grid = new Cell[gridColumns][gridRows];  //Initializing cell grid
        for ( int c=0; c < gridColumns; c++)
            for ( int r=0; r < gridRows; r++ )
                grid[c][r] = new Cell( c, r );


        neighbours = new Point[8];

        for (int i=0; i<8; i++)
            neighbours[i] = new Point(0,0);


        //Game initialization
        this.inGame = false;

        for (int i = 800; i<1400; i++)
        {
            setCell(i, 700,true);       //"Line" pattern
        }
    }

    /**
     * Enter point for the thread
     */
    public void run()
    {
        while(!Thread.interrupted())
        {
            try
            {
                if (inGame)                 //If game is not paused
                {
                    next();                 //To the next generation
                    incrementGenerations(); //increment the generations counter
                    Thread.sleep(delay);    //delay before the next generation
                }


            } catch (InterruptedException e)
            {
                //ignore
                e.printStackTrace();
            }
        }
    }

    /**
     * Clears hashtables and a generation label.
     */
    public synchronized void clear() {
        generations = 0;
        currentShape.clear();
        nextShape.clear();
    }


    /**
     * Increments the generation counter
     */
    public synchronized void incrementGenerations()
    {
        generations++;
    }


    /**
     * Move to the next generation of cells. Game logic here.
     */
    public synchronized void next()
    {
        Cell cell;
        int col, row;
        Enumeration Enum;

        //generations++;

        nextShape.clear();


        //Clear the neighbours
        Enum = currentShape.keys();
        while ( Enum.hasMoreElements() )
        {
            cell = (Cell) Enum.nextElement();
            cell.neighbour = 0;
        }



        Enum = currentShape.keys();
        while ( Enum.hasMoreElements() )
        {
            cell = (Cell) Enum.nextElement();
            col = cell.col;
            row = cell.row;


            //Neighbours coords
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


            //make a tor field
            if (col == zoomOffset.x )
            {
                neighbours[0].x += zoomOffset.width;
                neighbours[3].x += zoomOffset.width;
                neighbours[5].x += zoomOffset.width;
            }

            if (col == (zoomOffset.x + zoomOffset.width - 1))
            {
                neighbours[2].x -= zoomOffset.width;
                neighbours[4].x -= zoomOffset.width;
                neighbours[7].x -= zoomOffset.width;
            }

            if (row == zoomOffset.y)
            {
                neighbours[0].y += zoomOffset.height;
                neighbours[1].y += zoomOffset.height;
                neighbours[2].y += zoomOffset.height;
            }

            if (row == (zoomOffset.y + zoomOffset.height - 1))
            {
                neighbours[5].y -= zoomOffset.height;
                neighbours[6].y -= zoomOffset.height;
                neighbours[7].y -= zoomOffset.height;
            }



            for (int i=0; i<8; i++)
                addNeighbour(neighbours[i]);
        }


        //First Game of Life rule
        Enum = currentShape.keys();
        while ( Enum.hasMoreElements() )
        {
            cell = (Cell) Enum.nextElement();

            if ( cell.neighbour != 3 && cell.neighbour != 2)

            {
                currentShape.remove( cell );
            }
        }

        //Second Game of Life rule
        Enum = nextShape.keys();
        while ( Enum.hasMoreElements() )
        {
            cell = (Cell) Enum.nextElement();

            if ( cell.neighbour == 3 )
            {
                setCell( cell.col, cell.row, true );
            }
        }


        //Delete cells which are not to be drawn
        Enum = currentShape.keys();
        while( Enum.hasMoreElements() )
        {
            cell = (Cell)Enum.nextElement();

            if      (cell.col < zoomOffset.x || cell.col > (zoomOffset.x + zoomOffset.width) ||
                    (cell.row < zoomOffset.y || cell.row > (zoomOffset.y + zoomOffset.height)))
            {
                cell.neighbour = 0;
               currentShape.remove(cell);
            }
        }
    }

    /**
     * Add a neighbour to the cell. Used in next() method.
     *
     * @param pos cell position on the field.
     */
    private synchronized void addNeighbour(Point pos)
    {
        try {
            Cell cell = nextShape.get(grid[pos.x][pos.y]);
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
     * Get the current living cells
     * @return current living cells
     */
    public Enumeration getEnum() {
        return currentShape.keys();
    }

    /**
     * Get the cell state for this position
     * @param col cell column
     * @param row cell row
     * @return true - cell is alive
     *         false - cell is dead
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
     * Set the cell state
     * @param col cell column
     * @param row cell row
     * @param c  true - cell is alive
     *           false - cell is dead
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
     * Save the current pattern
     * @param fileName file name
     */
    public synchronized void save(String fileName)
    {
        if (fileName == null) return;

        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream outStream = new ObjectOutputStream(fos);


            outStream.writeObject(generations);
            outStream.writeObject(currentShape);

            outStream.close();

        } catch (IOException | NullPointerException e)
        {
            e.printStackTrace();
        }
    }



    /**
     * Load pattern from file
     * @param fileName file name
     */
    public synchronized void  load(String fileName)
    {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream inStream = new ObjectInputStream(fis);

            currentShape.clear();
            nextShape.clear();

            generations = (Integer)inStream.readObject();
            nextShape = (Hashtable)inStream.readObject();

            Cell cell;
            Enumeration Enum  = nextShape.keys();
            while (Enum.hasMoreElements())
            {
                cell = (Cell)Enum.nextElement();
                this.setCell(cell.col, cell.row, true);
            }


            inStream.close();

        }
        catch (IOException | NullPointerException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Get the generations count since the game started
     * @return current generation count
     */
    public synchronized int getGenerations()
    {
        try {
            return generations;
        }catch (NullPointerException e)
        {
            return 0;
        }
    }

    /**
     * Sets the new generations value (for scenario functions)
     * @param generation new generations value
     */
    public synchronized void setGenerationCount(int generation)
    {
        generations = generation;
    }


    /**
     * Set the new zoom offset value
     * @param offset new offset value
     */
    public synchronized void setZoomOffset(GdkRectangle offset)
    {
        this.zoomOffset = offset;
    }


    /**
     * Set delay between generations
     * @param delay delay time (in milliseconds)
     */
    public synchronized void setDelay(int delay)
    {
        this.delay = delay;
    }

    /**
     * Get the current delay between generations (in milliseconds)
     * @return current delay time
     */
    public int getDelay()
    {
     return delay;
    }


    /**
     * Set the game state
     * @param state true - game is running
     *              false - game is paused
     */
    public synchronized void setRunState(boolean state)
    {
        this.inGame = state;
    }


    /**
     * Get the current game state
     * @return true - game is running
     *         false - game is paused
     */
    public boolean getRunState()
    {
        return this.inGame;
    }

}

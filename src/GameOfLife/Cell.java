package GameOfLife;

/**
 * Created by Vlad Kanash on 04.03.2015.
 */

import java.io.Serializable;

/**
 * Represents a single cell on the game field.
 */
public class Cell implements Serializable
{
    /**
     * Column of the cell
     */
    public final short col;

    /**
     * Row of the cell
     */
    public final short row;


    /**
     * Number of live neighbours
     */
    public byte neighbour;


    /**
     * @param col X-координата клетки
     * @param row Y-координата клетки
     */
    public Cell( int col, int row )
    {
        this.col = (short)col;
        this.row = (short)row;
        neighbour = 0;
    }

    /**
     * Default constructor need for save/load operations
     */
    public Cell()
   {
       col = 0;
       row = 0;
       neighbour = 0;
   }


    public boolean equals(Object o)        //For using in hashtable
    {
        if (!(o instanceof Cell) )
            return false;
        return col==((Cell)o).col && row==((Cell)o).row;
    }

    public int hashCode()                   //For using in hashtable
    {
        return 5000*row+col;
    }

    public String toString()                 //For using in hashtable
    {
        return "C"+col+"R"+row+"N"+neighbour;
    }
}
package GameOfLife;

/**
 * Представляет клетку на игровом поле.
 */
public class Cell
{
    /**
     * X-координата клетки
     */
    public final short col;

    /**
     * Y-координата клетки
     */
    public final short row;


    /**
     * Количество живых соседей клетки
     */
    public byte neighbour;


    private static int HASHFACTOR = 5000;


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


    public boolean equals(Object o)        //Необходимо для хэш-таблицы
    {
        if (!(o instanceof Cell) )
            return false;
        return col==((Cell)o).col && row==((Cell)o).row;
    }

    public int hashCode()                   //Необходимо для хэш-таблицы
    {
        return HASHFACTOR*row+col;
    }

    public String toString()                 //Необходимо для хэш-таблицы
    {
        return "GameOfLife.Cell at ("+col+", "+row+") with "+neighbour+" neighbour"+(neighbour==1?"":"s");
    }
}
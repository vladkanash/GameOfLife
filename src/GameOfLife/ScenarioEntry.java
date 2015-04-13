package GameOfLife;

import org.eclipse.swt.graphics.Point;

import java.io.Serializable;

/**
 * Created by Vlad Kanash on 11.04.2015.
 */

/**
 * Contents the saved information about user input
 */
public class ScenarioEntry implements Serializable
{

    /**
     * When the cell was placed/deleted
     */
    private int generation;

    /**
     * Coordinates of a cell
     */
    private Point pos;


    /**
     * True - place new cell
     * False - delete cell
     */
    private boolean action;


    /**
     *
     * @param generation When the cell was placed/deleted
     * @param pos Coordinates of a cell
     */
    public ScenarioEntry (int generation, Point pos, boolean action)
    {
        this.generation = generation;
        this.pos = pos;
        this.action = action;
    }

    /**
     * Default constructor need for file operations
     */
    public ScenarioEntry()
    {
        pos = new Point(0,0);
        generation = 0;
        action = true;
    }

    /**
     * Get generation info.
     * @return generation
     */
    public int getGeneration()
    {
        return generation;
    }


    /**
     * Get entry action info.
     * @return action
     */
    public boolean getAction()
    {
        return action;
    }

    /**
     * Get coordinates of a cell
     * @return coordinates
     */
    public Point getPoint()
    {
        return pos;
    }


    public boolean equals(Object o)
    {
        if (!(o instanceof ScenarioEntry) )
            return false;

        return  pos.x       == ((ScenarioEntry)o).pos.x &&
                pos.y      == ((ScenarioEntry)o).pos.y &&
                generation == ((ScenarioEntry)o).generation &&
                action     == ((ScenarioEntry)o).action;
    }

    public int hashCode()
    {
        return 5000*pos.y+pos.x;
    }

    public String toString()
    {
        return action+"G"+generation+"X"+pos.x+"Y"+pos.y;
    }

}

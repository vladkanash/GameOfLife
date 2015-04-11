package GameOfLife;

import org.eclipse.swt.graphics.Point;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.ListIterator;

/**
 * Created by user on 01.04.2015.
 */
public class Scenario
{


    private Hashtable<Integer, Point> entryList;

    private CellGrid grid;
    private GameBoard board;

    private String fileName;



    public Scenario(String fileName, CellGrid grid, GameBoard board)
    {
        entryList = new Hashtable<>();


        this.grid = grid;
        this.board = board;

        this.fileName = fileName;

    }


    public void addEntry(int generation, Point coords)
    {
        entryList.put (generation, coords);
    }


    public void saveScenario()
    {
        if (fileName == null) return;

        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream outStream = new ObjectOutputStream(fos);


            outStream.writeObject(entryList);


            outStream.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    public int runScenario()
    {
        int i = 0;

        Enumeration keys = entryList.keys();


        while (keys.hasMoreElements())     //REMOVE THIS
        {

            if (entryList.containsKey(i))
            {
                grid.setCell(entryList.get(i).x, entryList.get(i).y, true);  //TODO removing cells
            }
        }

        return 0;
    }

}

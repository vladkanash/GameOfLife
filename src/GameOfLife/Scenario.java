package GameOfLife;

import org.eclipse.swt.graphics.Point;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

/**
 * Created by user on 01.04.2015.
 */
public class Scenario
{


    private Hashtable<Point, Integer> entryList;

    private String fileName;



    public Scenario(String fileName)
    {
        entryList = new Hashtable<>();
        this.fileName = fileName;

    }


    public void addEntry(int generation, Point coords)
    {
        entryList.put (coords, generation);
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

}

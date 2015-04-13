package GameOfLife;

import org.eclipse.swt.graphics.Point;

import java.io.*;
import java.util.*;

/**
 * Created by user on 01.04.2015.
 */


/**
 * Contains information about user input.
 */
public class Scenario {


    /**
     * User game actions (deleting cells, placing new cells).
     */
    private LinkedHashSet<ScenarioEntry> entryList;


    /**
     * Current cell grid
     */
    private CellGrid grid;


    /**
     * Last generation of the recording.
     */
    private int lastGen;


    /**
     *
     * @param grid Current game cell grid.
     */
    public Scenario(CellGrid grid)
    {
        this.entryList = new LinkedHashSet<>();
        this.grid = grid;
        this.lastGen = 0;
    }


    /**
     * Add a new entry to the record.
     * @param generation current generation
     * @param coords coordinates of a cell
     * @param action true - cell placed
     *               false - cell deleted
     */
    public void addEntry(int generation, Point coords, boolean action) {
        entryList.add(new ScenarioEntry(generation, coords, action));
    }

    /**
     * Clear the record.
     */
    public void clear() {
        entryList.clear();
        lastGen = 0;
    }

    /**
     * Sets the final generation for the record.
     * @param generation final generation
     */
    public void setLastGeneration(int generation) {
        lastGen = generation;
    }

    /**
     * Get the final generation for the record.
     * @return final generation
     */
    public int getLastGeneration() {
        return lastGen;
    }

    /**
     * Saves the current grid state (all living cells) in the record.
     * @param generation current generation.
     */
    public void addCurrentState(int generation) {
        Enumeration e = grid.getEnum();

        Cell cell;
        while (e.hasMoreElements()) {
            cell = (Cell) e.nextElement();
            addEntry(generation, new Point(cell.col, cell.row), true);
        }

    }

    /**
     * Checks for the current generation of the grid and places/deletes cells on the grid.
     * @return true - cells were successfully placed/deleted
     *         false - entry list is empty (no more actions from this scenario can be applied).
     */
    public boolean checkForEntries() {


        ScenarioEntry entry;
        Iterator iter = entryList.iterator();
        if (!iter.hasNext()) return false;
        entry = (ScenarioEntry) iter.next();

        while (entry.getGeneration() < grid.getGenerations()) {
            if (!iter.hasNext()) return false;
            entry = (ScenarioEntry) iter.next();
        }


        if (entry.getGeneration() == grid.getGenerations()) {
            do {
                grid.setCell(entry.getPoint().x, entry.getPoint().y, entry.getAction());
                if (!iter.hasNext()) return false;
                entry = (ScenarioEntry) iter.next();

            } while (entry.getGeneration() == grid.getGenerations());

        }

        return true;
    }

    /**
     * Save scenario in the file.
     * @param filename .scn filename
     */
    public void saveScenario(String filename) {
        if (filename == null) return;

        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream outStream = new ObjectOutputStream(fos);


            outStream.writeObject(entryList);
            outStream.writeObject(lastGen);

            outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load scenario from file.
     * @param filename .scn filename
     */
    public synchronized void loadScenario(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream inStream = new ObjectInputStream(fis);


            entryList.clear();


            entryList = (LinkedHashSet) inStream.readObject();
            lastGen = (int) inStream.readObject();


            inStream.close();

            setInitGeneration();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the cell grid generation same as the first generation of the scenario.
     * (Preparing grid for the scenario playing)
     */
    void setInitGeneration() {
        Iterator iter = entryList.iterator();
        if (!iter.hasNext()) return;

        ScenarioEntry entry = (ScenarioEntry) iter.next();
        grid.setGenerationCount(entry.getGeneration());
    }
}



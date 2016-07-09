package gameoflife;


import org.eclipse.swt.graphics.Point;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Created by Vlad Kanash on 01.04.2015.
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
     * Get the initial generation for the record.
     * @return initial generation
     */
    public int getFirstGeneration()
    {
        Iterator<ScenarioEntry> it = entryList.iterator();
        if (!it.hasNext()) return 0;

        return (it.next()).getGeneration();
    }

    /**
     * Saves the current grid state (all living cells) in the record.
     * @param generation current generation.
     */
    public void addCurrentState(int generation) {
        Enumeration e = grid.getEnum();

        Cell cell;
        while (e.hasMoreElements())
        {
            cell = (Cell) e.nextElement();
            addEntry(generation, new Point(cell.col, cell.row), true);
        }

    }

    /**
     * Checks for the current generation of the grid and places/deletes cells on the grid.
     * @return true - cells were successfully placed/deleted
     *         false - entry list is empty (no more actions from this scenario can be applied).
     */
    public boolean checkForEntries()
    {
        ScenarioEntry entry;
        Iterator<ScenarioEntry> iter = entryList.iterator();
        if (!iter.hasNext()) return false;
        entry = iter.next();

        while (entry.getGeneration() < grid.getGenerations()) {
            if (!iter.hasNext()) return false;
            entry = iter.next();
        }



        if (entry.getGeneration() == grid.getGenerations()) {
            do {
                grid.setCell(entry.getPoint().x, entry.getPoint().y, entry.getAction());
                if (!iter.hasNext()) return false;
                entry = iter.next();

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

        Writer writer = null;

        try {
            writer = new FileWriter(filename);

            for (ScenarioEntry entry : entryList)
            {
                writer.write(entry.toString());

            }
            writer.write("LG" + lastGen);
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (writer != null)
            {
                try
                {
                    writer.close();
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Load scenario from file.
     * @param filename .scn filename
     */
    public synchronized void loadScenario(String filename) {
        if (filename == null) return;

        //Analyzer fileInfo = new Analyzer(filename);


        FileReader fr;
        BufferedReader reader;
        String buffer = new String();



        entryList.clear();
        try
        {

            fr = new FileReader(filename);
            reader = new BufferedReader(fr);

            //for (int k = 0; k < scalaInfo.longestGameNum(); k++)    //reads the longest game
                buffer = reader.readLine();

            LinkedList<Integer> numbers = new LinkedList<Integer>();

            Pattern nums = Pattern.compile("\\d+");
            Pattern actions = Pattern.compile("[TF]");

            Matcher m = nums.matcher(buffer);
            Matcher a = actions.matcher(buffer);

            while (m.find())
            {
                numbers.add(Integer.valueOf(m.group()));
            }

            int i = 0;

            while (a.find())
            {
                Point coords = new Point(numbers.get(i+1), numbers.get(i+2));
                boolean act;

                //action value (T or F)
                act = a.group().equals("T");

                addEntry(numbers.get(i),coords, act);
                i += 3;
            }

            reader.close();
            fr.close();

            lastGen = numbers.getLast();

            setInitGeneration();

        } catch (Exception e)
        {
            e.printStackTrace();

        }
    }

    /**
     * Sets the cell grid generation same as the first generation of the scenario.
     * (Preparing grid for the scenario playing)
     */
    public void setInitGeneration()
    {
        //MakeBigFile();

        Iterator<ScenarioEntry> iter = entryList.iterator();
        if (!iter.hasNext()) return;

        ScenarioEntry entry = iter.next();
        grid.setGenerationCount(entry.getGeneration());
    }


    private void GenerateRandomScenario()
    {
        Random generator = new Random();

        entryList.clear();

        int steps = generator.nextInt(20);
        int gen = generator.nextInt(100);
        int lineCount = generator.nextInt(100) + 200;
        boolean action = true;

        for (int i = 0; i < steps; i++)
        {
            int X = generator.nextInt(600) + 800;
            int Y = generator.nextInt(400) + 500;
            for (int j = 0; j< lineCount; j++)
            {
               //Double x = generator.nextGaussian() * 2000;
               //Double y = generator.nextGaussian() * 1000;

                Point coords = new Point(X, Y);

                addEntry(gen, coords, action);

                X += generator.nextInt(3) - 1;
                Y += generator.nextInt(3) - 1;
            }


            int genDelay = generator.nextInt(10);
            if (generator.nextInt(11) > 8)
            action = false; else action = true;

            gen += genDelay;
        }

        lastGen = gen;

    }

    private void MakeBigFile()
    {
        Writer writer = null;

        try
        {
            writer = new FileWriter("BigFile2.scn");


            for (int i = 0; i< 10000; i++)
            {

                GenerateRandomScenario();

                for (ScenarioEntry entry : entryList) {
                    writer.write(entry.toString());

                }
                writer.write("LG" + lastGen);
                writer.write(System.getProperty("line.separator"));
            }


        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (writer != null)
            {
                try
                {
                    writer.close();
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
}



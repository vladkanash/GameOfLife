package GameOfLife;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vlad Kanash on 13.05.2015.
 */
public class Analyzer
{

    private int longestGameNum;

    private int shortestGameNum;

    private int longestGameLength;

    private int shortestGameLength;

    private int gamesCount;

    private double averageGameLength;


    public Analyzer(String filename)
    {
        FileReader fr;
        BufferedReader reader;
        String buffer;
        int gameLen;
        int i = 0;
        double averLen = 0;

        LinkedHashMap<Integer, Integer> lens = new LinkedHashMap<>();
        //entryList.clear();


        try
        {

            fr = new FileReader(filename);
            reader = new BufferedReader(fr);


            System.out.println("Reading the file...");

            buffer = reader.readLine();

            while (buffer != null)
            {
                i++;

                gameLen = getProperties(buffer);
                lens.put(i + 1, gameLen);
                averLen += gameLen;
                buffer = reader.readLine();
            }

            this.gamesCount = i;
            this.averageGameLength = averLen / i;

            //Java sort
            JavaSortAndPrint(lens);


            reader.close();

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void JavaSortAndPrint(HashMap<Integer, Integer> map)
    {
        //lens.entrySet().stream().sorted((e1, e2) -> Integer.compare(e1.getValue(), e2.getValue()))
        //  .forEach(e -> System.out.println(e.getKey() + " == " + e.getValue()));

        Object[] keyArr = map.keySet().toArray();
        Object[] valArr = map.values().toArray();


        Quicksort sorter = new Quicksort();
        LengthComparator comp = new LengthComparator();

        long time = System.currentTimeMillis();

        sorter.sort(valArr, keyArr, comp);

        time = System.currentTimeMillis() - time;

        int i ;
        for (i = 0; i<valArr.length; i++)
        {
            System.out.println("#" + keyArr[i] + " == " + valArr[i]);
        }



        System.out.println("Java sort time : " + time + " millis");

        this.longestGameNum =  (Integer)keyArr[i-1];
        this.shortestGameNum = (Integer)keyArr[0];

        this.longestGameLength = (Integer)valArr[i-1];
        this.shortestGameLength = (Integer)valArr[0];

    }

    private int getProperties(String buffer)
    {
        Pattern nums = Pattern.compile("\\d+");
        Matcher m = nums.matcher(buffer);

        LinkedList<Integer> numbers = new LinkedList<>();

        while (m.find())
        {
            numbers.add(Integer.valueOf(m.group()));
        }

        //Last number = last generation, first number = first generation.
        return numbers.getLast() - numbers.getFirst();
    }


    public int getLongestGameNum() {
        return longestGameNum;
    }

    public int getShortestGameNum() {
        return shortestGameNum;
    }

    public int getLongestGameLength() {
        return longestGameLength;
    }

    public int getShortestGameLength() {
        return shortestGameLength;
    }

    public int getGamesCount() {
        return gamesCount;
    }



    public class LengthComparator implements Comparator<Integer>
    {
        @Override
        public int compare(Integer a, Integer b)
        {
            return Integer.compare(a,b);
        }
    }

    public class Quicksort
    {
        public final Random RND = new Random();

        private void swap(Object[] array, Object[] array2, int i, int j) {
            Object tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;

            tmp = array2[i];
            array2[i] = array2[j];
            array2[j] = tmp;

        }

        private int partition(Object[] array, Object[] array2, int begin, int end, Comparator cmp) {
            int index = begin + RND.nextInt(end - begin + 1);
            Object pivot = array[index];

            swap(array, array2, index, end);

            for (int i = index = begin; i < end; ++i) {
                if (cmp.compare(array[i], pivot) <= 0) {
                    swap(array, array2, index++, i);
                }
            }
            swap(array, array2, index, end);
            return (index);
        }

        private void qsort(Object[] array,Object[] array2, int begin, int end, Comparator cmp) {
            if (end > begin) {
                int index = partition(array, array2, begin, end, cmp);
                qsort(array, array2, begin, index - 1, cmp);
                qsort(array, array2, index + 1, end, cmp);
            }
        }

        public void sort(Object[] array, Object[] array2, Comparator cmp) {
            qsort(array, array2,  0, array.length - 1, cmp);
        }
    }

}

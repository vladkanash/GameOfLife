package GameOfLife



/**
 * Created by Vlad Kanash on 10.05.2015.
 */

 object  comparators
{
  val intCompare: (Int, Int) => Boolean = (a:Int, b:Int) => if (a > b) true else false
  val strLenCompare: (String, String) => Boolean = (a: String, b: String) => if (a.length > b.length) true else false

}

class Sort[T] (comp : (T, T) => Boolean, map: Array[(Int, T)])
{
  private def ScalaSort (list: Array[(Int, T)]): Unit =
  {
    def Swap(a:Int, b:Int): Unit = {
      val t = list(a)
      list(a) = list(b)
      list(b) = t
    }


    def inSort (first: Int, last: Int): Unit = {
      val p = list((first + last) / 2)._2
      var left  = first
      var right = last

      do
      {

        while (comp(p, list(left)._2))  left += 1
        while (comp(list(right)._2, p)) right -= 1

        if (left <= right)
        {
          if (comp(list(left)._2, list(right)._2))
              Swap(left, right)
          left += 1
          right -= 1

        }

      }while (left <= right)

      if (left < last)  inSort(left, last)
      if (first < right) inSort(first, right)
    }
    inSort(0, list.size - 1)
    }

  def SortAndPrint(): Array[(Int, T)] =
  {
    val time = System.currentTimeMillis()

    ScalaSort(map)

    val endTime = System.currentTimeMillis() - time

    //scalaArray.foreach(e => System.out.println(e._1 + " == " + e._2))
    System.out.println("Scala sort time : " + endTime + " millis")

    map
  }

}

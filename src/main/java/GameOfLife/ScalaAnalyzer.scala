package GameOfLife
//
//import scala.io.Source
//import scala.collection.JavaConverters._
//
///**
// * Created by  Vlad Kanash on 13.05.2015.
// */
//class ScalaAnalyzer(filename : String)
//{
//
//    private val TotalNum : List[Array[Int]] = getNumers().toList
//    private val SortedLens : List[(Int, Int)] = new Sort[Int](comparators.intCompare, getLenArray()).SortAndPrint().toList
//
//    val longestGameNum: Int = SortedLens.last._1
//    val longestGameLength: Int = SortedLens.last._2
//    val shortestGameNum: Int = SortedLens(0)._1
//    val shortestGameLength: Int = SortedLens(0)._2
//    val gamesCount: Int = Source.fromFile(filename).getLines().filterNot(_.isEmpty).size
//
//    val averageGameLength: Double = (for (num <-SortedLens) yield num._2).sum.toDouble / SortedLens.length
//    val averageCellsPlaced: Double = getAverageCellsPlaced()
//
//    val totalCells: Int = TotalNum.map(numer => (numer.length - 1) / 3).sum
//
//
//    private def getLenArray():Array[(Int, Int)] =
//      TotalNum.map({numers => (TotalNum.indexOf(numers) + 1, numers.last - numers(0))}).toArray
//
//
//    private def mapFile[B](func: String => B ): Iterator[B] =
//      Source.fromFile(filename).getLines().filterNot(_.isEmpty).map(func)
//
//
//    private def getNumers() =
//      mapFile(buffer => buffer.split("[^0-9]").filterNot(_.isEmpty).map(_.toInt))
//
//
//    private def getAverageCellsPlaced(): Double =
//      mapFile( buffer => buffer.split("[^T]").filterNot(_.isEmpty).length)
//        .toList.sum.toDouble/ this.gamesCount
//
//
//
//    def getGamesList() = {
//        val actStream = mapFile(buffer => buffer.split("[^TF]").filterNot(_.isEmpty).map({
//          case "T" => true
//          case "F" => false
//        })).toStream
//
//        val iter =  mapFile({ buffer =>
//          buffer.split("[^0-9]").filterNot(_.isEmpty).map(_.toInt).grouped(3).toList
//        }).toStream
//
//        val lastGens = for(game <- iter) yield game.last.last
//
//        val partialList = iter.zip(actStream).map(entry => entry._1.zip(entry._2))
//
//        partialList.map(game => game.map({
//          case (arr: Array[Int], b: Boolean) if arr.length == 3 => (arr(0), arr(1), arr(2), b)
//        })).zip(lastGens)
//
//      }
//
//
//    private def getStepList(index: Int): List[List[(Int, Int, Int, Boolean)]] = {
//      val game =  getGamesList().drop(index-1).head
//
//        def getSteps(list: List[(Int, Int, Int, Boolean)]): List[List[(Int, Int, Int, Boolean)]] = list match {
//          case Nil => Nil
//          case _ =>
//            val spannedList = list.span(entry => (entry._1 == list(0)._1) && (entry._4 == list(0)._4))
//            spannedList._1 :: getSteps(spannedList._2)
//          }
//
//        getSteps(game._1)
//    }
//
//    def getNotation(index: Int): java.util.List[String] =
//      {
//
//        val game =  getGamesList().drop(index-1).head
//        val lastString: String = "record ends at generation " + game._2
//
//        val list = getStepList(index).map(entry => entry(0) match {
//          case (g, _, _, true) => entry.length +  " cells were places at generation " + g
//          case (g, _, _, false) => entry.length +  " cells were deleted at generation " + g
//        })
//
//        (list :+ lastString).asJava
//      }
//
//    def getCellsNotation(index: Int): java.util.List[java.util.List[(Integer, Integer)]] =
//      getStepList(index).map(entry => entry.map(step => (Integer.valueOf(step._2), Integer.valueOf(step._3))).asJava).asJava
//
//}
//

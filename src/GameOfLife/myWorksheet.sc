import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem

class DemoActor(magicNumber: Int) extends Actor {
  def receive = {
    case x: Int => sender() ! (x + magicNumber)
  }
}

val a = 1

val b = a + 2

val c = List(1, 2, 4)

val d = List(3, 5)

val e = (c ::: d).sortWith((a, b) => a < b)




//val system = ActorSystem("mySystem")
//val myActor = system.actorOf(Props[DemoActor], "myactor2")




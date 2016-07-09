import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging

import akka.actor.ActorSystem

class ScalaCellGrid extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case "test" => log.info("received test")
    case _      => log.info("received unknown message")
  }
}

object EX {
  val system = ActorSystem("mySystem")
//  val myActor = system.actorOf(Props[ScalaCellGrid], "myactor2")
}




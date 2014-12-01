package sandbox.actors

import akka.actor.{Props, Actor, ActorLogging}
import sandbox.ActorActivator.pong
import scala.concurrent._

object Pong {
  def props() =
    Props(classOf[Pong])
}

class Pong() extends Actor with ActorLogging {

  import context.dispatcher

  var pingActor = self

  override def receive: Receive = {

    case ping =>
      log.debug(s"Pong received ping")
      pingActor = sender()
      Future {
        blocking {
          Thread.sleep(1000)
        }
        log.debug("Finally done !")
        pingActor ! pong
      }
  }

}

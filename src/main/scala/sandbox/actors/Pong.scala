package sandbox.actors

import akka.actor.{Props, Actor, ActorLogging}
import sandbox.ActorActivator.pong

object Pong {
  def props() =
    Props(classOf[Pong])
}

class Pong() extends Actor with ActorLogging {

  override def receive: Receive = {

    case ping =>
      log.debug(s"Pong received ping")
      sender() ! pong

  }

}

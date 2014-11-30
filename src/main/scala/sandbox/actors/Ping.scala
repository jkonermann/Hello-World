package sandbox.actors

import akka.actor.{ActorRef, Props, Actor, ActorLogging}
import sandbox.ActorActivator.{start, ping}

object Ping {
  def props(pongActor:ActorRef) =
    Props(classOf[Ping], pongActor)
}

class Ping(pongActor:ActorRef) extends Actor with ActorLogging {

  private var commander = self
  private var count = 0
  private var counter = 0

  override def receive: Receive = {

    case start(numberOfTimes) =>
      log.debug(s"Ping received start with count=$numberOfTimes")
      commander = sender()
      count = numberOfTimes
      pongActor ! ping

    case pong =>
      counter = counter + 1
      log.debug(s"Ping received pong with counter=$counter")
      if(counter == count) {
        commander ! "DONE"
      } else {
        sender() ! ping
      }

  }
}

package sandbox.actors

import akka.actor.{ActorRef, Props, ActorLogging, Actor}

object DemoActor2 {
  def props(magicNumber:Int, demo:ActorRef) =
    Props(classOf[DemoActor2], magicNumber, demo)
}

class DemoActor2(magicNumber:Int, demo:ActorRef) extends Actor with ActorLogging {

  var counter = 0

  override def receive: Receive = {

    case message:String =>
      log.info("Received String message: {} magicNumber='{}'", message, magicNumber)
      counter = counter + 1
      if (counter < magicNumber) {
        demo ! message + counter
      }
  }
}

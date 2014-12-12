package sandbox

import akka.actor.ActorSystem
import sandbox.actors.{DemoActor2, DemoActor}

object DaActivator {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem("scala-java-akka")

    val demo = system.actorOf(DemoActor.props(42), "demo")
    val demo2 = system.actorOf(DemoActor2.props(5, demo), "demo2")

    demo2 ! "hello1"

    Thread.sleep(500)
    system.shutdown()

  }

}

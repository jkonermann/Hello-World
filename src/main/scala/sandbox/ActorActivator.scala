package sandbox

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import sandbox.actors.{Pong, Ping}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object ActorActivator {

  case object ping
  case object pong
  case class start(count:Int)

  implicit val timeout = Timeout(5.seconds)

  def main(args: Array[String]) {

    val config = ConfigFactory.load()
    val numberOfTimes = config.getInt("numberOfTimes")

    val system = ActorSystem("ping-pong")
    val pong = system.actorOf(Pong.props()
      .withDispatcher("pong-dispatcher"), "pong")
    val ping = system.actorOf(Ping.props(pong), "ping")

    val result: Future[String] = (ping ? start(numberOfTimes)).mapTo[String]
    result.onComplete { s =>
      system.shutdown()
    }

  }

}

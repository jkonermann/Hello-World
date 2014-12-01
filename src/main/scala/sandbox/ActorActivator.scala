package sandbox

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.routing.FromConfig
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import sandbox.actors.{Pong, Ping}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object ActorActivator {

  case object ping
  case object pong
  case class start(count:Int)

  implicit val timeout = Timeout(15.seconds)

  def main(args: Array[String]) {

    val config = ConfigFactory.load()
    val numberOfTimes = config.getInt("numberOfTimes")

    val system = ActorSystem("ping-pong")
    val pong = system.actorOf(Pong.props()
      .withRouter(FromConfig())
      .withDispatcher("pong-dispatcher"), "pong")

    val ping1 = system.actorOf(Ping.props(pong), "ping1")

    val ping2 = system.actorOf(Ping.props(pong), "ping2")

    val ping3 = system.actorOf(Ping.props(pong), "ping3")

    val ping4 = system.actorOf(Ping.props(pong), "ping4")

    val future1 = (ping1 ? start(numberOfTimes)).mapTo[String]
    val future2 = (ping2 ? start(numberOfTimes)).mapTo[String]
    val future3 = (ping3 ? start(numberOfTimes)).mapTo[String]
    val future4 = (ping4 ? start(numberOfTimes)).mapTo[String]

    val aggregatedFutureResult = for {
      future1Result <- future1
      future2Result <- future2
      future3Result <- future3
      future4Result <- future4
    } yield (future1Result, future2Result, future3Result, future4Result)

    aggregatedFutureResult.onSuccess{ case (a,b,c,d) =>
      println(s"$a, $b, $c, $d")
      Thread.sleep(500)
      system.shutdown()
    }

  }

}

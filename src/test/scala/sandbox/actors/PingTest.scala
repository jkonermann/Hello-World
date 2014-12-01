package sandbox.actors

import akka.actor.ActorSystem
import akka.routing.FromConfig
import akka.testkit.{ImplicitSender, DefaultTimeout, TestKit}
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import org.scalatest.junit.JUnitRunner
import sandbox.ActorActivator.{start, pong, ping}
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class PingTest
  extends TestKit(ActorSystem("PingTest"))
  with DefaultTimeout
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  val pongActor = system.actorOf(Pong.props()
    .withRouter(FromConfig())
    .withDispatcher("pong-dispatcher"), "pong")
  val pingActor = system.actorOf(Ping.props(pongActor), "ping")

  override def afterAll() {
    shutdown()
  }

  "001: Ping " should {
    "send ping when received pong" in {
      within(500.millis) {
        pingActor ! pong
        expectMsg(ping)
      }
    }
  }

  "002: Pong " should {
    "send pong when received ping" in {
      within(1500.millis) {
        pongActor ! ping
        expectMsg(pong)
      }
    }
  }

  "003: Ping " should {
    "eventually send DONE after received start" in {
      within(15000.millis) {
        pingActor ! start(5)
        expectMsg("DONE")
      }
    }
  }

}



package sandbox.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, DefaultTimeout, TestKit}
import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import org.scalatest.junit.JUnitRunner
import sandbox.ActorActivator.{pong, ping}
import scala.concurrent.duration._

object PongTest {
  // Define your test specific configuration here
  val config = """
    akka {
      loglevel = "WARNING"
    }
               """
}

@RunWith(classOf[JUnitRunner])
class PongTest
  extends TestKit(ActorSystem("PongTest", ConfigFactory.parseString(PongTest.config)))
  with DefaultTimeout
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  val pongActor = system.actorOf(Pong.props(), "pong")

  override def afterAll() {
    shutdown()
  }

  "001: Pong " should {
    "send pong when received ping" in {
      within(500.millis) {
        pongActor ! ping
        expectMsg(pong)
      }
    }
  }

}



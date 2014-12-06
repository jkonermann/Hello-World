package sandbox

import akka.actor.ActorSystem
import akka.event.Logging
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import sandbox.actors.SimpleSoapServer
import sandbox.simple_soap.Stock
import spray.can.Http
import spray.http._
import HttpMethods._
import scala.concurrent.duration._

import scala.util.{Failure, Success}

object SimpleSoapActivator {

  case object Stop

  def main(args: Array[String]) {

    val stock = new Stock()

    implicit val system = ActorSystem("simple-soap")
    val log = Logging(system, getClass)

    implicit val timeout: Timeout = Timeout(15.seconds)
    import system.dispatcher // implicit execution context

    system.actorOf(SimpleSoapServer.props(), "simpleSoapServer")

    def stockPrise(name: String) {

      val request = HttpRequest(
        method = POST,
        uri = "http://localhost:8080/GetStockPrice",
        entity = stock.createRequestXml(name).toString())

        (IO(Http) ? request).mapTo[HttpResponse] onComplete {
        case Success(response) => log.info("StockPrice for name: {} = {}", name, stock.getStockPrice(response))
        case Failure(error) => log.warning("Failed to GetStockPrice for name: {} {}", error)
      }
    }

    stockPrise("apple")
    stockPrise("google")
    stockPrise("IBM")
    stockPrise("microsoft")
    stockPrise("abn")

    system.scheduler.scheduleOnce(2.second) {
      System.exit(0)
    }

  }

}

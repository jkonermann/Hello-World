package sandbox.actors

import akka.actor.{ActorSystem, Props, ActorLogging, Actor}
import akka.io.IO
import sandbox.SimpleSoapActivator.Stop
import sandbox.simple_soap.Stock
import spray.can.Http
import spray.http.{HttpMethods, HttpRequest, Uri}
import HttpMethods._

object SimpleSoapServer {
  def props() =
    Props(classOf[SimpleSoapServer])
}

class SimpleSoapServer extends Actor with ActorLogging {

  val stock = new Stock()
  var httpListener = self

  implicit val system = ActorSystem()
  IO(Http) ! Http.Bind(self, interface = "localhost", port = 8080)

  override def receive: Receive = {

    case _: Http.Connected => sender ! Http.Register(self)

    case bound: Http.Bound =>
      log.debug("*** Received {}", bound)
      httpListener = sender()

    case b@ Http.Unbound =>
      log.debug("*** unbound received")
      Thread.sleep(500)
      context.system.shutdown()

    case Http.CommandFailed =>

    case request@ HttpRequest(POST, Uri.Path("/GetStockPrice"), _, _, _) =>
      sender ! stock.createHttpResponse(request)

    case Stop =>
      log.debug("*** stop received")
      httpListener ! Http.Unbind



    case other => log.debug("*** Did not handle: {}", other)
  }


}

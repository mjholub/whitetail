package me.mjholub.whitetail

import jkugiya.ulid.ULID
import akka.actor.Props
import scala.annotation.meta.setter
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.event.LoggingReceive
import akka.actor.ReceiveTimeout
import akka.io.Tcp.SO.KeepAlive
import java.net.InetSocketAddress

case class SimplePeer(
  id: ULID
)

object PeerProtocol {

def props(services: WrappedServices.ServiceInfo) = Props(new PeerProtocol(services))

case object GetPeerInfo

case class PeerInformation(
  id: ULID,
  provides: Option[List[WrappedServices.ServiceInfo]],
  latency_5m: Option[Double],
  latency_current: Option[Double],
  conn_age: Int, // in seconds
  conn_limit: Int,
  times_flagged: Int
) {
def flag = this.copy(times_flagged = times_flagged + 1)
}
}

class PeerProtocol(services: WrappedServices.ServiceInfo) extends Actor with ActorLogging {
  import PeerProtocol._

  import concurrent.duration._

  val generator = ULID.getGenerator()
  val servicesAvailable = WrappedServices.getProvidedServices()
  var selfInfo = PeerInformation(generator.generate(), servicesAvailable, None, None, 0, 10000, 0)
  // random INetSocketAddr 
  val rand = new scala.util.Random
  val socketport = rand.between(1024, 65535)

  val socket = new InetSocketAddress("localhost", socketport) 

  context.setReceiveTimeout(10.seconds)

  def receive = LoggingReceive {
    case ReceiveTimeout =>
    // context.parent ! Send(KeepAlive)
    case GetPeerInfo =>

  }
}
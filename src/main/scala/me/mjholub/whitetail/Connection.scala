import me.mjholub.whitetail

import java.net.InetSocketAddress
import jkugiya.ulid.ULID
import akka.actor.{Actor, ActorRef}
import redis.clients.jedis
import akka.io.Tcp.Message
import me.mjholub.whitetail.SimplePeer
import akka.io.Tcp.Register

object ConnectionHandler {
  case class Send(message: Message)
  case class CreatePeerConnection(peer: SimplePeer)
}

class ConnectionHandler(endpoint: InetSocketAddress, internalPeerId: ULID) extends Actor {
  import akka.io.Tcp.{Bind, Bound, Connected, CommandFailed}
  import akka.io.IO
  import context.system
  import ConnectionHandler._

  // resuorce requests coordinator
  val coordinator = context.parent 

  override def receive: Receive = {
    case Bound(localAddress) =>
      println(s"Bound to $localAddress")
    case CommandFailed(_: Bind) =>
      println("Failed to bind")
      context stop self
    case Connected(remote, local) =>
      val connection = sender()
      val generator = ULID.getGenerator()
      val peer = SimplePeer(generator.generate())
      coordinator ! CreatePeerConnection(peer)
      connection ! Register(self)
  } 
}

abstract class ConnectionMetricsHandler extends Actor {
  import context.system
  import akka.actor._

  // getUploadSpeedCurrent will get the current total upload speed of the current host
  // expressed in bytes
  def getLatencyCurrent(querier: SimplePeer): Option[Double] = {
    None
  }

  // TODO: read/write ops on redis cache once the logic for sourcing relevant data is implemented  
  def writeLatency(latency: Double, peer: SimplePeer): Unit = {}

  def getLatency(period: Int): Option[List[(ULID, Double)]] = {
    None
}
}
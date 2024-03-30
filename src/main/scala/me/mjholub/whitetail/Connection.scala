import java.net.InetSocketAddress
import jkugiya.ulid.ULID
import akka.actor.{Actor, ActorRef}

object ConnectionHandler {
  case class CreatePeerConnection(peer:  PeerInformation)
  case class PeerConnectionCreated(connection: ActorRef, peer: PeerInformation)
}

class ConnectionHandler(endpoint: InetSocketAddress, internalPeerId: ULID) extends Actor {
  import Tcp._
  import context.system
  import ConnectionHandler._

  // resuorce requests coordinator
  val coordinator = context.parent

  IO(Tcp) ! Bind(self, endpoint)

  override def receive: Receive = {
    case Bound(localAddress) =>
      println(s"Bound to $localAddress")
    case CommandFailed(_: Bind) =>
      println("Failed to bind")
      context stop self
    case Connected(remote, local) =>
      val connection = sender()
      val peer = PeerInformation(ULID(), remote, internalPeerId)
      coordinator ! CreatePeerConnection(peer)
      connection ! Register(self)
  } 
}

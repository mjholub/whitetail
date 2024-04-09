package me.mjholub.whitetail

import akka.actor.ActorSystem
import akka.discovery.Discovery
import akka.discovery.ServiceDiscovery.Resolved
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.annotation.experimental

class ServiceRegistration {
  // use Akka service registration to ensure that the service
  // is discoverable
  def registerService(serviceName: String, port: Int): Future[Done] = {
    val registration =
      Discovery(system).registerService(serviceName, port, 10.seconds)
    registration
  }
}

// dummy message to request service list
class ServiceDiscoveryMessage {};

// ServiceLoad represents the current number of connections to a service
// and an average for past 5, 10, 30 minutes.
object WrappedServices {

  case class ServiceLoad(
      connections: Int,
      avg_5m: Double,
      avg_10m: Double,
      avg_30m: Double
  )

  case class ServiceInfo(
      name: String,
      load: ServiceLoad,
      additional_metadata: Map[String, Unit]
  )

  private case class ServiceConfig(
      port: Int,
      // must be local
      host: Option[String]
  )

  def getProvidedServices(): Option[List[ServiceInfo]] = {
    // TODO: read services as specified
    None
  }
  serviceList
}

// WARN: experimental
def scanAvailableServices(): Future[List[ServiceResponse]] = {
  val system    = ActorSystem("ServiceInfo")
  val discovery = Discovery(system).discovery

  discovery.lookup("localWrapper", resolveTimeout = 3.seconds).map { resolved =>
    resolved.serviceName match {
      case Some(name) =>
        println(
          s"Service $name discovered with endpoints: ${resolved.addresses}"
        )
        // Here, you would typically map the resolved endpoints to your ServiceResponse format
        List() // Return an empty list for now
      case None =>
        println("Service discovery failed.")
        List() // Return an empty list if discovery fails
    }
  }
}

def selectPrefferedRemoteServiceProvider(): Option[ServiceResponse] = {
  val services    = getAvailableServices()
  val serviceList = services.keys.toList

  // sort by load
  val sortedServices = serviceList.sortBy { service =>
    services(service).load.sum
  }

  // select the instance with the lowest load
  sortedServices.headOption
}

def verifyContentConsistency(service: ServiceResponse): Boolean = {
  // check if the service is consistent with the local service
  // in terms of the content it provides
  val localService  = ServiceResponse()
  val localContent  = localService.content
  val remoteContent = service.content

  localContent == remoteContent

  // TODO: add actual hash checking etc...
}

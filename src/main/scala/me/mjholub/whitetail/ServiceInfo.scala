package me.mjholub.whitetail


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
}

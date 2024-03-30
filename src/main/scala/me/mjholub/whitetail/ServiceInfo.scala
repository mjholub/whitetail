
class ServiceInfo extends ServiceResponse {
  struct StaticServiceResponse {
  val serviceName: String
  val proxyFor: String
  val metadata: Option[Map[String, String]]
  val cap: Option[Int]
  }
  struct DynamicServiceResponse {
    // similar to load average, but denoting number of connections
    // within the last 5, 15, and 60 minutes
    var load: List[Int]
  }

  def getAvailableServices(): Map[StaticServiceResponse, DynamicServiceResponse] = {
    // for now, we'll just read that from a json file, but
    // in the future, hopefully the encapsulated services
    // will have a dedicated API for this
    val source = Source.fromFile("services.json")

    val services = source.getLines().mkString
    source.close()

    val json = Json.parse(services)
    val serviceList = (json \ "services").as[List[ServiceResponse]]

    serviceList
  }
}

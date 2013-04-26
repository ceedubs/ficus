package net.ceedubs.ficus

import org.specs2.mutable.Specification
import com.typesafe.config.{Config, ConfigFactory}
import FicusConfig._
import net.ceedubs.ficus.readers.ValueReader

case class ServiceConfig(urls: Set[String], maxConnections: Int, httpsRequired: Boolean)

class ExampleSpec extends Specification {

  // an example config snippet for us to work with
  val config = ConfigFactory.parseString(
    """
      |services {
      |  users {
      |    urls = ["localhost:8001"]
      |    maxConnections = 100
      |    httpsRequired = true
      |  }
      |  analytics {
      |    urls = ["localhost:8002", "localhost:8003"]
      |    maxConnections = 25
      |  }
      |}
    """.stripMargin)

  "Ficus config" should {
    "make Typesafe config more Scala-friendly" in {
      val userServiceConfig = config.as[Config]("services.users")
      userServiceConfig.as[Set[String]]("urls") must beEqualTo(Set("localhost:8001"))
      userServiceConfig.as[Int]("maxConnections") must beEqualTo(100)
      userServiceConfig.as[Option[Boolean]]("httpsRequired") must beSome(true)

      val analyticsServiceConfig = config.as[Config]("services.analytics")
      analyticsServiceConfig.as[List[String]]("urls") must beEqualTo(List("localhost:8002", "localhost:8003"))
      val analyticsServiceRequiresHttps = analyticsServiceConfig.as[Option[Boolean]]("httpsRequired") getOrElse false
      analyticsServiceRequiresHttps must beFalse
    }

    "Be easily extensible" in {
      // need to define an implicit ValueReader[ServiceConfig] to be able to extract a ServiceConfig
      // if we try to call as[ServiceConfig] without one, the compiler will give you an error
      implicit val serviceConfigReader: ValueReader[ServiceConfig] = new ValueReader[ServiceConfig] {
        def read(config: Config, path: String): ServiceConfig = {
          val serviceConfig = config.as[Config](path)
          ServiceConfig(
            urls = serviceConfig.as[Set[String]]("urls"),
            maxConnections = serviceConfig.getInt("maxConnections"), // the old-fashioned way is fine too!
            httpsRequired = serviceConfig.as[Option[Boolean]]("httpsRequired") getOrElse false
          )
        }
      }

      // so we don't have to add a "services." prefix for each service
      val servicesConfig = config.as[Config]("services")

      val userServiceConfig: ServiceConfig = servicesConfig.as[ServiceConfig]("users")
      userServiceConfig.maxConnections must beEqualTo(100)

      val analyticsServiceConfig: ServiceConfig = servicesConfig.as[ServiceConfig]("analytics")

      // the analytics service config doesn't define an "httpsRequired" value, but the serviceConfigReader defaults
      // to false if it is empty with its 'getOrElse false' on the extracted Option
      analyticsServiceConfig.httpsRequired must beFalse

      val servicesMap = config.as[Map[String,ServiceConfig]]("services")
      servicesMap must beEqualTo(Map("users" -> userServiceConfig, "analytics" -> analyticsServiceConfig))
    }
  }
}

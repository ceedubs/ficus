package ceedubs.config

import org.specs2.mutable.Specification
import com.typesafe.config.{Config, ConfigFactory}
import KindsafeConfig._
import ceedubs.config.readers.ValueReader

case class ServiceConfig(urls: Set[String], maxConnections: Int, httpsRequired: Boolean)

class Examples extends Specification {
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

  "Kindsafe config" should {
    "make Typesafe config more Scala-friendly" in {
      val userServiceConfig = config.getAs[Config]("services.users")
      userServiceConfig.getAs[Set[String]]("urls") must beEqualTo(Set("localhost:8001"))
      userServiceConfig.getAs[Int]("maxConnections") must beEqualTo(100)
      userServiceConfig.getAs[Option[Boolean]]("httpsRequired") must beSome(true)

      val analyticsServiceConfig = config.getAs[Config]("services.analytics")
      analyticsServiceConfig.getAs[List[String]]("urls") must beEqualTo(List("localhost:8002", "localhost:8003"))
      val analyticsServiceRequiresHttps = analyticsServiceConfig.getAs[Option[Boolean]]("httpsRequired") getOrElse false
      analyticsServiceRequiresHttps must beFalse
    }

    "Be easily extensible" in {
      // need to define an implicit ValueReader[ServiceConfig] to be able to extract a ServiceConfig
      // if we try to call getAs[ServiceConfig] without one, the compiler will give you an error
      implicit val serviceConfigReader: ValueReader[ServiceConfig] = new ValueReader[ServiceConfig] {
        def get(config: Config, path: String): ServiceConfig = {
          val serviceConfig = config.getAs[Config](path)
          ServiceConfig(
            urls = serviceConfig.getAs[Set[String]]("urls"),
            maxConnections = serviceConfig.getInt("maxConnections"), // the old-fashioned way is fine too!
            httpsRequired = serviceConfig.getAs[Option[Boolean]]("httpsRequired") getOrElse false
          )
        }
      }

      // so we don't have to add a "services." prefix for each service
      val servicesConfig = config.getAs[Config]("services")

      val userServiceConfig: ServiceConfig = servicesConfig.getAs[ServiceConfig]("users")
      userServiceConfig.maxConnections must beEqualTo(100)

      val analyticsServiceConfig: ServiceConfig = servicesConfig.getAs[ServiceConfig]("analytics")
      // the analytics service config doesn't define an "httpsRequired" value, but the serviceConfigReader defaults
      // to false if it is empty with its 'getOrElse false' on the extracted Option
      analyticsServiceConfig.httpsRequired must beFalse

    }
  }
}

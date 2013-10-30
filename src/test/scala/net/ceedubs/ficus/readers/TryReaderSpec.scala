package net.ceedubs.ficus.readers

import com.typesafe.config.{ConfigException, Config, ConfigFactory}
import net.ceedubs.ficus.Spec

class TryReaderSpec extends Spec with TryReader with AnyValReaders { def is =
  "A try value reader should" ^
    "return a success when a value can be read" ! success ^
    "return a failure when a value cannot be read" ! cannotBeRead ^
    "handle an unexpected exception type" ! unexpectedException

  def success = {
    val cfg = ConfigFactory.parseString("myValue = true")
    tryValueReader[Boolean].read(cfg, "myValue") must beSuccessfulTry[Boolean].withValue(true)
  }

  def cannotBeRead = {
    val cfg = ConfigFactory.parseString("myValue = true")
    tryValueReader[Boolean].read(cfg, "wrongKey") must beFailedTry[Boolean].withThrowable[ConfigException.Missing]
  }

  def unexpectedException = {
    val cfg = ConfigFactory.parseString("myValue = true")
    implicit val stringValueReader: ValueReader[String] = new ValueReader[String] {
      def read(config: Config, path: String): String = throw new NullPointerException("oops")
    }
    tryValueReader[String].read(cfg, "wrongKey") must beFailedTry[String].withThrowable[NullPointerException]("oops")
  }
}

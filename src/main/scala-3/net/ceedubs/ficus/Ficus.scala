package net.ceedubs.ficus

import com.typesafe.config.Config
import net.ceedubs.ficus.readers._

trait FicusInstances
    extends AnyValReaders
    with StringReader
    with SymbolReader
    with OptionReader
    with CollectionReaders
    with ConfigReader
    with DurationReaders
    with TryReader
    with ConfigValueReader
    with BigNumberReaders
    with ISOZonedDateTimeReader
    with PeriodReader
    with LocalDateReader
    with URIReaders
    with URLReader
    with InetSocketAddressReaders

object Ficus extends FicusInstances {
  extension (config: Config) {
    def to[A](path: String)(implicit reader: ValueReader[A]): A = reader.read(config, path)

    def as[A](implicit reader: ValueReader[A]): A = to(".")

    def getAs[A](path: String)(implicit reader: ValueReader[Option[A]]): Option[A] = reader.read(config, path)

    def getOrElse[A](path: String, default: => A)(implicit reader: ValueReader[Option[A]]): A =
      getAs[A](path).getOrElse(default)

    def apply[A](key: ConfigKey[A])(implicit reader: ValueReader[A]): A = to[A](key.path)
  }
}

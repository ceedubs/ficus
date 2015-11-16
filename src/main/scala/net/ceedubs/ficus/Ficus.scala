package net.ceedubs.ficus

import com.typesafe.config.Config
import net.ceedubs.ficus.readers._

trait FicusInstances extends AnyValReaders with StringReader with OptionReader
    with CollectionReaders with ConfigReader with DurationReaders
    with TryReader with ConfigValueReader with BigNumberReaders
    with EnumerationReader

object Ficus extends FicusInstances {
  implicit def toFicusConfig(config: Config): FicusConfig = SimpleFicusConfig(config)
}

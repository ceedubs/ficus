package net.ceedubs.ficus.readers

trait AllValueReaderInstances extends AnyValReaders with StringReader with OptionReader
    with CollectionReaders with ConfigReader with DurationReaders with CompanionApplyReader

object AllValueReaderInstances extends AllValueReaderInstances

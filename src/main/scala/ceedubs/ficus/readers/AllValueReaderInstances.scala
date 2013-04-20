package ceedubs.ficus.readers

trait AllValueReaderInstances extends AnyValReaders with StringReader with OptionReaders
    with CollectionReaders with ConfigReader

object AllValueReaderInstances extends AllValueReaderInstances

package ceedubs.config.readers

trait AllValueReaderInstances extends AnyValReaders with StringReader with OptionReaders
    with CollectionReaders

object AllValueReaderInstances extends AllValueReaderInstances

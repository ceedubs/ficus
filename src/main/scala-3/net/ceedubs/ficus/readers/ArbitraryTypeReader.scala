package net.ceedubs.ficus.readers

trait ArbitraryTypeReader {
  implicit def arbitraryTypeValueReader[T]: Generated[ValueReader[T]] = ???
}

object ArbitraryTypeReader extends ArbitraryTypeReader
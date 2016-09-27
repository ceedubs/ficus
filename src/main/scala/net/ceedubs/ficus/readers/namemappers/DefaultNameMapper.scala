package net.ceedubs.ficus.readers.namemappers

/**
  * Default implementation for name mapper, names in code equivalent to names in configuration
  */
case object DefaultNameMapper extends NameMapper {

  override def map(name: String): String = name

}

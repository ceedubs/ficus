package ceedubs.config.readers

import com.typesafe.config.Config
import collection.JavaConverters._

trait CollectionConfigValueReaders {
  def listValueReader[A]: ConfigValueReader[List[A]] = new ConfigValueReader[List[A]] {
    def get(config: Config, path: String): List[A] = config.getList(path).unwrapped().asScala.toList.asInstanceOf[List[A]]
  }

  // weird name so it doesn't look like a setter method
  def aSetValueReader[A]: ConfigValueReader[Set[A]] = new ConfigValueReader[Set[A]] {
    def get(config: Config, path: String): Set[A] = config.getList(path).unwrapped().asScala.toSet.asInstanceOf[Set[A]]
  }
}

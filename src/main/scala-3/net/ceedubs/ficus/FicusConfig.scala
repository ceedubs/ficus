package net.ceedubs.ficus

import com.typesafe.config.Config
import net.ceedubs.ficus.readers.{AllValueReaderInstances, ValueReader}

trait FicusConfig {
  def config: Config

  def self: FicusConfig = this

  def as[A](path: String)(using reader: ValueReader[A]): A = reader.read(config, path)

  def to[A](using ValueReader[A]): A = as[A](".")

  def getAs[A](path: String)(implicit reader: ValueReader[Option[A]]): Option[A] = reader.read(config, path)

  def getOrElse[A](path: String, default: => A)(implicit reader: ValueReader[Option[A]]): A =
    getAs[A](path).getOrElse(default)

  def apply[A](key: ConfigKey[A])(implicit reader: ValueReader[A]): A = as[A](key.path)
}

final case class SimpleFicusConfig(config: Config) extends FicusConfig

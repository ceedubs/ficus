package net.ceedubs.ficus.readers

import com.typesafe.config.Config

trait ValueReader[A] {
  def read(config: Config, path: String): A
}

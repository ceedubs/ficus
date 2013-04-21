package net.ceedubs.ficus.readers

import com.typesafe.config.Config

trait ValueReader[A] {
  def get(config: Config, path: String): A
}

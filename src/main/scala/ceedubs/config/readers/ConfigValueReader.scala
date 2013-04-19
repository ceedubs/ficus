package ceedubs.config.readers

import com.typesafe.config.Config

trait ConfigValueReader[A] {
  def get(config: Config, path: String): A
}

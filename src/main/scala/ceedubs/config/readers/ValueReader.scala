package ceedubs.config.readers

import com.typesafe.config.Config

trait ValueReader[A] {
  def get(config: Config, path: String): A
}

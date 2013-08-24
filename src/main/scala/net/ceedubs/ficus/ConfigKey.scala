package net.ceedubs.ficus

trait ConfigKey[A] {
  def path: String
}

case class SimpleConfigKey[A](path: String) extends ConfigKey[A]

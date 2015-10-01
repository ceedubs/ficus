package net.ceedubs.ficus.readers

import com.typesafe.config.{ConfigException, Config}

trait AnyValReaders {
  implicit val booleanValueReader: ValueReader[Boolean] = new ValueReader[Boolean] {
    def read(config: Config, path: String): Boolean = config.getBoolean(path)
  }

  implicit val intValueReader: ValueReader[Int] = new ValueReader[Int] {
    def read(config: Config, path: String): Int = config.getInt(path)
  }

  implicit val longValueReader: ValueReader[Long] = new ValueReader[Long] {
    def read(config: Config, path: String): Long = config.getLong(path)
  }

  implicit val doubleValueReader: ValueReader[Double] = new ValueReader[Double] {
    def read(config: Config, path: String): Double = config.getDouble(path)
  }
  
  implicit val bigDecimalReader: ValueReader[BigDecimal] = new ValueReader[BigDecimal] {
    def read(config: Config, path: String): BigDecimal = {
      val s = config.getString(path)
      try {
        BigDecimal(s)
      } catch {
        case e:Throwable => throw new ConfigException.WrongType(config.origin(),path,"scala.math.BigDecimal","String",e)
      }
    }
  }
  
  implicit val bigIntReader: ValueReader[BigInt] = new ValueReader[BigInt] {
    def read(config: Config, path: String): BigInt = {
      val s = config.getString(path)
      try {
        BigInt(s)
      } catch {
        case e:Throwable => throw new ConfigException.WrongType(config.origin(),path,"scala.math.BigInt","String",e)
      }
    }
  }
}

object AnyValReaders extends AnyValReaders

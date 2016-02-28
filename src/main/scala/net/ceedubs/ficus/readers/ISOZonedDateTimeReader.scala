package net.ceedubs.ficus.readers

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import com.typesafe.config.Config

trait ISOZonedDateTimeReader {
  implicit val isoZonedDateTimeReader: ValueReader[ZonedDateTime] = new ValueReader[ZonedDateTime] {
    override def read(config: Config, path: String): ZonedDateTime = {
      val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
      ZonedDateTime.parse(config.getString(path), dateTimeFormatter)
    }
  }
}

object ISOZonedDateTimeReader extends ISOZonedDateTimeReader

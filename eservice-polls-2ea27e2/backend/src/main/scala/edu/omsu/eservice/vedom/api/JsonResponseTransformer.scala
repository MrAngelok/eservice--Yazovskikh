package edu.omsu.eservice.vedom.api

import org.json4s.JsonAST.{JNull, JString}
import org.json4s.jackson.Serialization
import org.json4s.{CustomSerializer, DefaultFormats}
import spark.ResponseTransformer

import java.text.SimpleDateFormat
import java.util.Date

class JsonResponseTransformer(useEnvelope: Boolean = false) extends ResponseTransformer {
  override def render(model: scala.AnyRef): String = {
    val customSqlDateSerializer = new CustomSerializer[Date](_ =>
      ( {
        case JString(x) =>
          val dateFormatter = new SimpleDateFormat("dd.MM.yyyy")
          new Date(dateFormatter.parse(x).getTime)
        case JNull => null
      }, {
        case date: Date =>
          val dateFormatter = new SimpleDateFormat("dd.MM.yyyy")
          JString(dateFormatter.format(date))
      }))

    val envelopeString = if (!useEnvelope) model else model match {
      case re: Envelope => re
      case None => Envelope(data = None)
      case _ => Envelope(data = model)

    }
    implicit val formats = DefaultFormats.preservingEmptyValues ++ List(customSqlDateSerializer)
    Serialization.write(envelopeString)
  }
}

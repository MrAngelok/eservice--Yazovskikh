package edu.omsu.eservice.vedom.api

import edu.omsu.eservice.vedom.entity.{AppException, StateCodes}
import edu.omsu.eservice.vedom.entity.StateCodes.StateCode

case class Envelope(
                     success: Boolean,
                     code: String,
                     message: String,
                     data: Option[Any]
                   )

object Envelope {
  def apply(exc: AppException): Envelope = {
    Envelope(
      success = false,
      code = exc.code,
      message = exc.message,
      data = exc.data)
  }

  def apply(err: StateCode): Envelope = {
    Envelope(
      success = false,
      code = err.code,
      message = err.message,
      data = None)
  }

  def apply(data: Any): Envelope = {
    Envelope(
      success = true,
      code = StateCodes.SUCCESS.code,
      message = StateCodes.SUCCESS.message,
      data = Some(data))
  }

  def apply(dataOpt: Option[Any]): Envelope = {
    Envelope(
      success = true,
      code = StateCodes.SUCCESS.code,
      message = StateCodes.SUCCESS.message,
      data = dataOpt)
  }
}

package edu.omsu.eservice.vedom.entity

class AppException(val message: String, val code: String, val data: Option[Any]) extends RuntimeException(message) {
  def this(exceptionMessage: String) {
    this(exceptionMessage.toString, "UNHANDLED_EXCEPTION", None)
  }

  def this(stateCode: StateCodes.StateCode) {
    this(stateCode.message, stateCode.code, None)
  }

  def this(stateCode: StateCodes.StateCode, data: Any) {
    this(stateCode.message, stateCode.code, Option(data))
  }
}
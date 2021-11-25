package edu.omsu.eservice.vedom.entity

object StateCodes {
  val SUCCESS: StateCodes.StateCode = StateCode("OK", "Операция выполнена успешно")
  val PARAM_ERROR: StateCodes.StateCode = StateCode("PARAM_ERROR", "Параметр указан неверно")
  val INTERNAL_ERROR: StateCodes.StateCode = StateCode("INTERNAL_ERROR", "Внутренняя ошибка")

  case class StateCode(code: String, message: String)


}

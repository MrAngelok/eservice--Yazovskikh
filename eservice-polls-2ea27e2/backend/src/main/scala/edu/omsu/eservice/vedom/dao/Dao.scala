package edu.omsu.eservice.vedom.dao

import scalikejdbc._

import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.{HttpsURLConnection, SSLContext, TrustManager, X509TrustManager}
import javax.sql.DataSource

class Dao(ds: DataSource) {
  val fuckingOracle: DBConnectionAttributes = DBConnectionAttributes(driverName = Some("oracle.jdbc.driver.OracleDriver"))

  def readonly[T](f: DBSession => T): T =
    DB(ds.getConnection, fuckingOracle) readOnly f

  def autocommit[T](f: DBSession => T): T =
    DB(ds.getConnection, fuckingOracle) autoCommit f

  def disableSslCheck(): Unit = {
    val newTrustManager: TrustManager = new X509TrustManager {
      def getAcceptedIssuers: Array[X509Certificate] = null

      def checkClientTrusted(certs: Array[X509Certificate], authType: String): Unit = {}

      def checkServerTrusted(certs: Array[X509Certificate], authType: String): Unit = {}
    }
    val trustAllCerts = Array[TrustManager](newTrustManager)
    try {
      val sc: SSLContext = SSLContext.getInstance("SSL")
      sc.init(null, trustAllCerts, new SecureRandom)
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory)
    } catch {
      case e: Exception => e.printStackTrace()

    }

  }

  def sspIdsByPersonId(personId: Long): List[Long] =
    readonly { implicit session =>
      sql"""
         select
          ссп_ид2
         from ип8_студ_инфо
         where члвк_ид = $personId
         """.map { rs => rs.long(1) }.list().apply()
    }


  //  def getSomeData(personId: Long): List[SomeEntity] =
  //    readonly { implicit session =>
  //      sql"""
  //           select some_field
  //           from
  //             some_table
  //           where some_where
  //           order by ...
  //         """
  //        .map { rs =>
  //          SomeEntity(
  //            someField = rs.string("some_field")
  //          )
  //        }.list().apply
  //    }
}

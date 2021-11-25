package edu.omsu.eservice.vedom

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariDataSource
import edu.omsu.eservice.vedom.api.Auth.Profile
import edu.omsu.eservice.vedom.api.{Api, Auth, Envelope}
import edu.omsu.eservice.vedom.dao.Dao
import edu.omsu.eservice.vedom.entity.AppException
import org.json4s.{DefaultFormats, Formats}
import org.pac4j.core.profile.ProfileManager
import org.pac4j.sparkjava.SparkWebContext
import org.slf4j.LoggerFactory
import spark._

import java.text.SimpleDateFormat

object AppController {
  private val logger = LoggerFactory.getLogger(classOf[App])

  def main(args: Array[String]) {
    implicit val formats: Formats = new DefaultFormats {
      override def dateFormatter: SimpleDateFormat = new SimpleDateFormat("dd.MM.yyyy")
    }
    val config = ConfigFactory.load()

    val ds = new HikariDataSource()
    ds.setDataSourceClassName(config.getString("db.driver"))
    ds.addDataSourceProperty("url", config.getString("db.url"))
    ds.setUsername(config.getString("db.user"))
    ds.setPassword(config.getString("db.password"))
    ds.setMaximumPoolSize(config.getInt("db.poolMaxSize"))
    ds.setConnectionTimeout(config.getInt("db.connectionTimeoutMillis"))
    ds.setConnectionTestQuery(config.getString("db.poolValidationQuery"))

    val dao = new Dao(ds)
    dao.disableSslCheck()
    val client = Auth.makeDasClient(config, dao)
    val api = new Api(config, dao, client)

    // AppException will not cause 404 error, but
    // return Json object with error message instead
    Spark.exception(classOf[AppException], new ExceptionHandler[AppException] {
      override def handle(exception: AppException, request: Request, response: Response): Unit = {
        val context = new SparkWebContext(request, response)
        val manager = new ProfileManager[Profile](context)
        if (!manager.isAuthenticated) {
          logger.error("[" + request.requestMethod() + " " + request.uri() + "] " + exception.getMessage)
        } else {
          val profile: Profile = manager.get(true).get()
          logger.error(profile.username + "(" + profile.personId + ") [" + request.requestMethod() + " " + request.uri() + "] " + exception.getMessage)
        }
        response.header("Content-type", "application/json; charset=\"UTF-8\"")
        response.status(200)
        val envelope: Envelope = Envelope(exception)
        response.body(api.envelopeTransformer.render(envelope))
      }
    })

    //    def getSome(profile: Profile, req: Request, resp: Response): List[SomeEntity] = {
    //      servicesSome.someMethod()
    //    }

    //    api.get("some url", getSome)
    //    api.post("some url", postSome)
    //    api.put("some url", putSome)
    //    api.delete("some url", delSome)
  }
}

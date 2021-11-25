package edu.omsu.eservice.vedom.api

import com.typesafe.config.Config
import edu.omsu.eservice.vedom.api.Auth.Profile
import edu.omsu.eservice.vedom.dao.Dao
import edu.omsu.eservice.vedom.security.DasSparkIntegration
import org.pac4j.core.profile.ProfileManager
import org.pac4j.oauth.client.OAuth20Client
import org.pac4j.sparkjava.SparkWebContext
import org.slf4j.LoggerFactory
import spark._

class Api(config: Config, dao: Dao, client: OAuth20Client[Profile]) {
  private val logger = LoggerFactory.getLogger(classOf[Api])
  private val appRoot = if (config.hasPath("app.root")) config.getString("app.root") else ""

  if (config.hasPath("app.port"))
    Spark.port(config.getInt("app.port"))

  val appUrl = config.getString("app.url")
  val dasServerUri = config.getString("das.server.uri")
  val dasHost = config.getString("das.server.host")


  DasSparkIntegration.init(appRoot, appUrl, dasHost, dasServerUri, client)

  Spark.exception(classOf[Exception], (ex: Exception, request: Request, response: Response) => {
    response.status(500)
    logger.error(ex.getMessage, ex)
  })

  val transformer = new JsonResponseTransformer(false)
  val envelopeTransformer = new JsonResponseTransformer(true)

  def get(path: String, fn: (Profile, Request, Response) => AnyRef) {
    Spark.get(appRoot + path, new Route {
      override def handle(request: Request, response: Response): AnyRef = {
        response.`type`("application/json")
        val context = new SparkWebContext(request, response)
        val manager = new ProfileManager[Profile](context)
        val profiles = manager.getAll(true)

        if (profiles.isEmpty) {
          Spark.halt(401)
          return null
        }

        fn(profiles.get(0), request, response)
      }
    }, envelopeTransformer)
  }

  def post(path: String, fn: (Profile, Request, Response) => AnyRef) {
    Spark.post(appRoot + path, "application/json", new Route {
      override def handle(request: Request, response: Response): AnyRef = {
        val context = new SparkWebContext(request, response)
        val manager = new ProfileManager[Profile](context)
        val profiles = manager.getAll(true)

        if (profiles.isEmpty) {
          Spark.halt(401)
          return null
        }


        fn(profiles.get(0), request, response)
      }
    }, envelopeTransformer)
  }


  def put(path: String, fn: (Profile, Request, Response) => AnyRef) {
    Spark.put(appRoot + path, "application/json", new Route {
      override def handle(request: Request, response: Response): AnyRef = {
        val context = new SparkWebContext(request, response)
        val manager = new ProfileManager[Profile](context)
        val profiles = manager.getAll(true)

        if (profiles.isEmpty) {
          Spark.halt(401)
          return null
        }


        fn(profiles.get(0), request, response)
      }
    }, envelopeTransformer)
  }

  def delete(path: String, fn: (Profile, Request, Response) => AnyRef) {
    Spark.delete(appRoot + path, "application/json", new Route {
      override def handle(request: Request, response: Response): AnyRef = {
        val context = new SparkWebContext(request, response)
        val manager = new ProfileManager[Profile](context)
        val profiles = manager.getAll(true)

        if (profiles.isEmpty) {
          Spark.halt(401)
          return null
        }


        fn(profiles.get(0), request, response)
      }
    }, envelopeTransformer)
  }

}

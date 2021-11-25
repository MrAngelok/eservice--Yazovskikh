package edu.omsu.eservice.vedom.api

import com.fasterxml.jackson.databind.JsonNode
import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.model.OAuth2AccessToken
import com.typesafe.config.Config
import edu.omsu.eservice.vedom.dao.Dao
import org.pac4j.core.http.url.DefaultUrlResolver
//import edu.omsu.eservice.vedom.security.DasOAuth2Client
import org.pac4j.oauth.client.OAuth20Client
import org.pac4j.oauth.config.OAuth20Configuration
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition
import org.pac4j.oauth.profile.{JsonHelper, OAuth20Profile}

object Auth {

  case class Profile(personId: Long, username: String, domain: String, sspIds: Seq[Long]) extends OAuth20Profile

  //
  //  class PersonAuthorizer extends ProfileAuthorizer[Profile] {
  //    override def isProfileAuthorized(context: WebContext, profile: Profile): Boolean = true
  //
  //    override def isAuthorized(context: WebContext, profiles: util.List[Profile]): Boolean =
  //      isAnyAuthorized(context, profiles)
  //  }

  case class InsufficientScopeException(var forwardUri: String) extends RuntimeException {


    //          InsufficientScopeException(String forwardUri) {
    //              this.forwardUri = forwardUri;
    //          }
  }


  def makeDasClient(config: Config, dao: Dao): OAuth20Client[Profile] = {
    val dasServerUri = config.getString("das.server.uri")
    val dasDataUri = config.getString("das.server.data.uri")
    val dasClientId = config.getString("das.client.id")
    val dasSecret = config.getString("das.client.secret")
    val scope = config.getString("das.server.scope")

    val clConf = new OAuth20Configuration();
    clConf.setScope(config.getString("das.server.scope"));
    clConf.setKey(config.getString("das.client.id"))
    clConf.setSecret(config.getString("das.client.secret"))
    clConf.setWithState(true)
    clConf.setApi(new DefaultApi20 {
      override def getAccessTokenEndpoint = config.getString("das.server.uri") + "/oauth/token"

      override def getAuthorizationBaseUrl: String = config.getString("das.server.uri") + "/oauth/authorize"
    })

    clConf.setProfileDefinition(new OAuth20ProfileDefinition[Profile, OAuth20Configuration]() {
      override def getProfileUrl(accessToken: OAuth2AccessToken, configuration: OAuth20Configuration): String =
        config.getString("das.server.data.uri") + "/whois/" + scope

      override def extractUserProfile(body: String): Profile = {
        val json = JsonHelper.getFirstNode(body)
        val data = JsonHelper.getElement(json, "data").asInstanceOf[JsonNode]

        if (data != null) {
          val username = JsonHelper.getElement(data, "username").asInstanceOf[String]
          val domain = JsonHelper.getElement(data, "domain").asInstanceOf[String]
          val personId = JsonHelper.getElement(data, "personId").asInstanceOf[Int].toLong //why int?

          Profile(personId, username, domain, dao.sspIdsByPersonId(personId))
        } else
          throw new InsufficientScopeException(JsonHelper.getElement(json, "forwardUri").asInstanceOf[String])
      }
    })

    val cl = new OAuth20Client[Profile]()
    cl.setConfiguration(clConf)
    cl.setUrlResolver(new DefaultUrlResolver(true))
    cl.setCallbackUrl(config.getString("app.root") + "/j_oauth_check")
    cl
  }
}

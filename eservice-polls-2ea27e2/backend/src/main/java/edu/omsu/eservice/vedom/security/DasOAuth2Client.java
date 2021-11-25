package edu.omsu.eservice.vedom.security;

class Bar {

}
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.github.scribejava.core.builder.api.BaseApi;
//import com.github.scribejava.core.builder.api.DefaultApi20;
//import com.github.scribejava.core.model.OAuth2AccessToken;
//import com.github.scribejava.core.model.OAuthConfig;
//import com.github.scribejava.core.oauth.OAuth20Service;
//import com.github.scribejava.core.utils.OAuthEncoder;
//import org.pac4j.core.authorization.authorizer.ProfileAuthorizer;
//import org.pac4j.core.exception.HttpAction;
//import org.pac4j.oauth.client.BaseOAuth20StateClient;
//import org.pac4j.oauth.profile.JsonHelper;
//import org.pac4j.oauth.profile.OAuth20Profile;
//
//public class DasOAuth2Client<T extends OAuth20Profile> extends BaseOAuth20StateClient<T> {
//
//    public interface ProfileExtractor<T> {
//        T extract(JsonNode data);
//    }
//
//    public static class InsufficientScopeException extends RuntimeException {
//        public final String forwardUri;
//
//        public InsufficientScopeException(String forwardUri) {
//            this.forwardUri = forwardUri;
//        }
//    }
//
//    final String baseUri;
//    final String dataUri;
//    final String scope;
//    final ProfileExtractor<T> profileExtractor;
//    public final ProfileAuthorizer<T> profileAuthorizer;
//
//    public DasOAuth2Client(String baseUri, String dataUri, String scope, DasOAuth2Client.ProfileExtractor<T> profileExtractor, ProfileAuthorizer<T> profileAuthorizer) {
//        this.baseUri = baseUri;
//        this.dataUri = dataUri;
//        this.scope = scope;
//        this.profileExtractor = profileExtractor;
//        this.profileAuthorizer = profileAuthorizer;
//        setResponseType("code");
//    }
//
//    @Override
//    protected BaseApi<OAuth20Service> getApi() {
//        return new DefaultApi20() {
//            private static final String AUTHORIZATION_URL = "%s/oauth/authorize?client_id=%s&response_type=%s&redirect_uri=%s&scope=%s&state=%s";
//
//            @Override
//            public String getAccessTokenEndpoint() {
//                return baseUri + "/oauth/token";
//            }
//
//            @Override
//            public String getAuthorizationUrl(OAuthConfig config) {
//                return String.format(AUTHORIZATION_URL, baseUri, config.getApiKey(), config.getResponseType(), OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(config.getScope()), config.getState());
//            }
//        };
//    }
//
//    @Override
//    protected String getProfileUrl(OAuth2AccessToken oAuth2AccessToken) {
//        return dataUri + "/whois/" + scope;
//    }
//
//    @Override
//    protected String getOAuthScope() {
//        return scope;
//    }
//
//    @Override
//    protected boolean hasOAuthGrantType() {
//        return true;
//    }
//
//    @Override
//    protected T extractUserProfile(String body) throws HttpAction {
//        JsonNode json = JsonHelper.getFirstNode(body);
//        JsonNode data = (JsonNode) JsonHelper.getElement(json, "data");
//
//        if (data != null)
//            return profileExtractor.extract(data);
//        else
//            throw new InsufficientScopeException((String) JsonHelper.getElement(json, "forwardUri"));
//    }
//}

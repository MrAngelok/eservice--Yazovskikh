package edu.omsu.eservice.vedom.security;

//import edu.omsu.eservice.vedom.security.DasOAuth2Client.InsufficientScopeException;

import edu.omsu.eservice.vedom.api.Auth;
import org.pac4j.core.authorization.authorizer.ProfileAuthorizer;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.oauth.client.OAuth20Client;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.DefaultHttpActionAdapter;
import org.pac4j.sparkjava.SecurityFilter;
import org.pac4j.sparkjava.SparkWebContext;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static spark.Spark.*;

public class DasSparkIntegration {
    static class PersonAuthorizer extends ProfileAuthorizer<Auth.Profile> {
        @Override
        protected boolean isProfileAuthorized(WebContext context, Auth.Profile profile) {
            return true;
        }

        @Override
        public boolean isAuthorized(WebContext context, List<Auth.Profile> profiles) {
            return isAnyAuthorized(context, profiles);
        }
    }

    static class LogoutRoute implements Route {
        final String logoutRedirectUrl;

        public LogoutRoute(String logoutRedirectUrl) {
            this.logoutRedirectUrl = logoutRedirectUrl;
        }

        @Override
        public Object handle(Request req, Response resp) throws Exception {
            SparkWebContext context = new SparkWebContext(req, resp);
            ProfileManager manager = new ProfileManager(context);
            manager.logout();

            if (req.headers("X-Remote-Logout") == null) {
                /* Local logout initiated by user via
                 in-app logout button click */
                resp.redirect(logoutRedirectUrl);
                return "";
            }

            /* Remote logout via crossdomain HTTP
            initiated by DAS */
            return "";
        }
    }

    static class ProbeAuthRoute implements Route {
        @Override
        public String handle(Request request, Response response) throws Exception {
            final SparkWebContext context = new SparkWebContext(request, response);

            if (new ProfileManager(context).isAuthenticated() == false)
                halt(SC_FORBIDDEN);

            return "";
        }
    }

    static final String AUTHORIZER_NAME = "student";

    static public void init(String appRoot, String appUrl, String dasHost, String dasServerUri, OAuth20Client client) {
        Config config = new Config(appRoot + "/j_oauth_check", client);
        config.setHttpActionAdapter(new DefaultHttpActionAdapter()); // ???
        config.addAuthorizer(AUTHORIZER_NAME, new PersonAuthorizer());

        before(appRoot + "/", new SecurityFilter(config, "OAuth20Client", AUTHORIZER_NAME));
        get(appRoot + "/", (Request req, Response res) -> {
            if (!res.raw().isCommitted())
                res.redirect(appUrl);
            return null;
        });

        Filter corsFilter = (req, resp) -> {
            resp.header("Access-Control-Allow-Origin", dasHost);
            resp.header("Access-Control-Allow-Credentials", "true");
            if ("OPTIONS".equals(req.requestMethod())) {
                resp.header("Access-Control-Max-Age", "3600");
                resp.header("Access-Control-Allow-Methods", "GET");
            }
        };

        before(appRoot + "/auth/probe_auth", corsFilter);
        get(appRoot + "/auth/probe_auth", new ProbeAuthRoute());

        before(appRoot + "/j_xdomain_logout", corsFilter);
        get(appRoot + "/j_xdomain_logout", new LogoutRoute(dasServerUri + "/logout.do"));

        CallbackRoute callBackRoute = new CallbackRoute(config);
        get(appRoot + "/j_oauth_check", callBackRoute);
        post(appRoot + "/j_oauth_check", callBackRoute);

        exception(Auth.InsufficientScopeException.class, (Exception ex, Request request, Response response) -> {
            if (!response.raw().isCommitted())
                response.redirect(((Auth.InsufficientScopeException) ex).forwardUri());
        });
    }
}
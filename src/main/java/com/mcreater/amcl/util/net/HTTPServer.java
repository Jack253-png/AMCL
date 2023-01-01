package com.mcreater.amcl.util.net;

import fi.iki.elonen.NanoHTTPD;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mcreater.amcl.util.JsonUtils.GSON;

public class HTTPServer extends NanoHTTPD {
    public final String rooturl;
    private final List<Route> routes = new Vector<>();
    public HTTPServer(int port) {
        super(port);
        rooturl = "http://localhost:" + port;
    }

    public HTTPServer(String hostname, int port) {
        super(hostname, port);
        rooturl = "http://" + hostname + ":" + port;
    }
    public void addRoute(Route route) {
        routes.add(route);
    }
    public String getRootURL() {
        return rooturl;
    }

    public final Response serve(IHTTPSession session) {
        for (Route route : routes) {
            if (route.isMatch(session)) {
                try {
                    return route.serve(session);
                } catch (Throwable e) {
                    e.printStackTrace();
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", returnDefaultJson(Response.Status.INTERNAL_ERROR, new HashMap<>()));
                }
            }
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "application/json", returnDefaultJson(Response.Status.NOT_FOUND, new HashMap<>()));
    }

    public static final String returnDefaultJson(Response.IStatus status, Map<Object, Object> original) {
        original.put("code", status.getRequestStatus());
        original.put("status", status.getDescription());
        return GSON.toJson(original);
    }

    public interface Route {
        boolean isMatch(IHTTPSession session);
        String getProperty(String key);
        Response serve(IHTTPSession session) throws Throwable;
        IHTTPSession getSession();
    }

    public static class RouteImpl implements Route {
        private final Pattern pattern;
        private Matcher matcher;
        private final Method allowedMethod;
        private ThrowableRunnable<Response, RouteImpl> runnable;
        private IHTTPSession session;
        public RouteImpl(Method method, Pattern pattern, ThrowableRunnable<Response, RouteImpl> runnable) {
            this.allowedMethod = method;
            this.pattern = pattern;
            this.runnable = runnable;
        }
        public boolean isMatch(IHTTPSession session) {
            this.session = session;
            if (allowedMethod == session.getMethod()) {
                matcher = pattern.matcher(session.getUri());
                return matcher.matches();
            }
            return false;
        }

        public String getProperty(String key) {
            return matcher == null ? null : matcher.group(key);
        }

        public Response serve(IHTTPSession session) throws Throwable {
            return runnable.run(this);
        }

        public IHTTPSession getSession() {
            return session;
        }
    }

    public interface ThrowableRunnable<T, A> {
        T run(A t) throws Throwable;
    }
}

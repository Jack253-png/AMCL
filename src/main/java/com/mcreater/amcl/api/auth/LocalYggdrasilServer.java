package com.mcreater.amcl.api.auth;

import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.JsonUtils;
import com.mcreater.amcl.util.KeyUtils;
import com.mcreater.amcl.util.net.HTTPServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import static com.mcreater.amcl.util.FileUtils.HashHelper.computeTextureHash;
import static java.nio.charset.StandardCharsets.UTF_8;

public class LocalYggdrasilServer extends HTTPServer {
    private final KeyPair keyPair = KeyUtils.generateKey();
    private final List<Player> players = new Vector<>();
    private final Logger logger = LogManager.getLogger(LocalYggdrasilServer.class);
    public LocalYggdrasilServer(int port) {
        super(port);
        addRoute(new RouteImpl(Method.GET, Pattern.compile("^/$"), this::root));
        addRoute(new RouteImpl(Method.GET, Pattern.compile("/status"), this::status));
        addRoute(new RouteImpl(Method.GET, Pattern.compile("/sessionserver/session/minecraft/profile/(?<uuid>[a-f0-9]*)"), this::getProfile));
        addRoute(new RouteImpl(Method.GET, Pattern.compile("/textures/(?<hash>[a-f0-9]*)"), this::getTexture));
    }
    public List<Player> getPlayers() {
        return players;
    }
    public static class Player {
        public String uuid;
        public String name;
        public File skin;
        public File cape;
        public boolean is_slim;

        public String skin_hash;
        public String cape_hash;

        public Player(String uuid, String name, File skin, File cape, boolean is_slim) throws IOException {
            this.uuid = uuid;
            this.name = name;
            this.skin = skin;
            this.cape = cape;
            this.is_slim = is_slim;
            if (skin != null) skin_hash = computeTextureHash(ImageIO.read(skin));
            if (cape != null) cape_hash = computeTextureHash(ImageIO.read(cape));
        }
    }
    private Response getTexture(RouteImpl route) throws IOException {
        String hash = route.getProperty("hash");

        for (Player player : players) {
            if (hash.equals(player.cape_hash)){
                return returnImage(player.cape, hash);
            }
            else if (hash.equals(player.skin_hash)){
                return returnImage(player.skin, hash);
            }
        }
        return notFound();
    }
    private Response getProfile(RouteImpl route) {
        String uuid = route.getProperty("uuid");

        for (Player player : players) {
            if (uuid.equals(player.uuid)) {
                Map<String, Object> tex = new HashMap<>();
                if (player.skin != null) {
                    if (player.is_slim) {
                        tex.put("SKIN", J8Utils.createMap(
                                "url", getRootURL() + "/textures/" + player.skin_hash,
                                "metadata", J8Utils.createMap(
                                        "model", "slim"
                                )));
                    } else {
                        tex.put("SKIN", J8Utils.createMap("url", getRootURL() + "/textures/" + player.skin_hash));
                    }
                }
                if (player.cape != null) {
                    tex.put("CAPE", J8Utils.createMap("url", getRootURL() + "/textures/" + player.cape_hash));
                }

                Map<String, Object> textureResponse = J8Utils.createMap(String.class, Object.class,
                        "timestamp", System.currentTimeMillis(),
                        "profileId", player.uuid,
                        "profileName", player.name,
                        "textures", tex
                );
                String traseStr = new String(Base64.getEncoder().encode(JsonUtils.GSON.toJson(textureResponse).getBytes(UTF_8)));
                return ok(
                        J8Utils.createMap(
                                "id", player.uuid,
                                "name", player.name,
                                "properties", J8Utils.createList(
                                        J8Utils.createMap(
                                                "name", "textures",
                                                "value", traseStr
                                        )
                                )
                        )
                );
            }
        }
        return notFound();
    }

    private Response returnImage(File f, String hash) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ImageIO.write(ImageIO.read(f), "png", buf);
        Response response = newFixedLengthResponse(Response.Status.OK, "image/png", new ByteArrayInputStream(buf.toByteArray()), buf.toByteArray().length);
        response.addHeader("Etag", String.format("\"%s\"", hash));
        response.addHeader("Cache-Control", "max-age=2592000, public");
        logger.info(String.format("accessing : %s", f));
        return response;
    }
    private Response root(RouteImpl route){
        if (keyPair != null) {
            return ok(J8Utils.createMap(
                    "signaturePublickey", KeyUtils.toPEMPublicKey(keyPair.getPublic()),
                    "skinDomains", J8Utils.createList("127.0.0.1", "localhost"),
                    "meta", J8Utils.createMap(
                            "serverName", "AMCL",
                            "implementationName", "AMCL",
                            "implementationVersion", "1.0",
                            "feature.non_email_login", true
                    )
            ));
        }
        else {
            return ok(J8Utils.createMap(
                    "skinDomains", J8Utils.createList("127.0.0.1", "localhost"),
                    "meta", J8Utils.createMap(
                            "serverName", "AMCL",
                            "implementationName", "AMCL",
                            "implementationVersion", "1.0",
                            "feature.non_email_login", true
                    )
            ));
        }
    }

    private Response status(RouteImpl route){
        return ok(J8Utils.createMap("user.count", 0, "token.count", 0, "pendingAuthentication.count", 0));
    }

    protected Response ok(Object response) {
        logger.info(String.format("responding text %s with code %d", response, Response.Status.OK.getRequestStatus()));
        return newFixedLengthResponse(Response.Status.OK, "text/json", JsonUtils.GSON.toJson(response));
    }

    protected Response notFound() {
        logger.info(String.format("respoding with code %d", Response.Status.NOT_FOUND.getRequestStatus()));
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_HTML, "404 not found");
    }

    protected Response noContent() {
        logger.info(String.format("respoding with code %d", Response.Status.NO_CONTENT.getRequestStatus()));
        return newFixedLengthResponse(Response.Status.NO_CONTENT, MIME_HTML, "{}");
    }

    protected Response badRequest() {
        String.format("respoding with code %d", Response.Status.BAD_REQUEST.getRequestStatus());
        return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_HTML, "400 bad request");
    }

    protected Response internalError() {
        String.format("respoding with code %d", Response.Status.INTERNAL_ERROR.getRequestStatus());
        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_HTML, "500 internal error");
    }
}

package com.mcreater.amcl.api.auth;

import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.JsonUtils;
import com.mcreater.amcl.util.KeyUtils;
import fi.iki.elonen.NanoHTTPD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class YggdrasilServer extends NanoHTTPD {
    private final KeyPair keyPair = KeyUtils.generateKey();
    private Player current_player;
    public final String rooturl;
    public static final int DEFAULT_PORT = 10078;
    private final Logger logger = LogManager.getLogger(YggdrasilServer.class);
    public YggdrasilServer(int port) {
        super(port);
        rooturl = "http://localhost:" + port;
        logger.info(String.format("openning yggdrasil server on %s", rooturl));
    }
    public YggdrasilServer(){
        this(DEFAULT_PORT);
    }
    public void setCurrent_player(Player p){
        current_player = p;
    }
    private static void putInt(byte[] array, int offset, int x) {
        array[offset] = (byte) (x >> 24 & 0xff);
        array[offset + 1] = (byte) (x >> 16 & 0xff);
        array[offset + 2] = (byte) (x >> 8 & 0xff);
        array[offset + 3] = (byte) (x & 0xff);
    }
    private static String computeTextureHash(BufferedImage img) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        int width = img.getWidth();
        int height = img.getHeight();
        byte[] buf = new byte[4096];

        putInt(buf, 0, width);
        putInt(buf, 4, height);
        int pos = 8;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                putInt(buf, pos, img.getRGB(x, y));
                if (buf[pos] == 0) {
                    buf[pos + 1] = buf[pos + 2] = buf[pos + 3] = 0;
                }
                pos += 4;
                if (pos == buf.length) {
                    pos = 0;
                    digest.update(buf, 0, buf.length);
                }
            }
        }
        if (pos > 0) {
            digest.update(buf, 0, pos);
        }

        byte[] sha256 = digest.digest();
        return String.format("%0" + (sha256.length << 1) + "x", new BigInteger(1, sha256));
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
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        logger.info(String.format("Request Header : %s, Url : %s, Method : %s", session.getHeaders(), uri, session.getMethod().toString()));
        if (uri.length() <= 1){
            return root();
        }
        else if (uri.startsWith("/status")){
            return status();
        }
        else if (uri.startsWith("/sessionserver/session/minecraft/profile/")){
            return getProfile(uri);
        }
        else if (uri.startsWith("/textures/")){
            try {
                return getTexture(uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return notFound();
    }
    private Response getTexture(String uri) throws IOException {
        String hash = uri.replace("/textures/", "");
        if (hash.equals(current_player.cape_hash)){
            return returnImage(current_player.cape, hash);
        }
        else if (hash.equals(current_player.skin_hash)){
            return returnImage(current_player.skin, hash);
        }
        else {
            return notFound();
        }
    }
    private Response getProfile(String uri){
        String uuid = uri.replace("/sessionserver/session/minecraft/profile/", "");

        if (uuid.equals(current_player.uuid)){
            Map<String, Object> tex = new HashMap<>();
            if (current_player.skin != null) {
                if (current_player.is_slim) {
                    tex.put("SKIN", J8Utils.createMap(
                            "url", rooturl + "/textures/" + current_player.skin_hash,
                            "metadata", J8Utils.createMap(
                                    "model", "slim"
                            )));
                } else {
                    tex.put("SKIN", J8Utils.createMap("url", rooturl + "/textures/" + current_player.skin_hash));
                }
            }
            if (current_player.cape != null){
                tex.put("CAPE", J8Utils.createMap("url", rooturl + "/textures/" + current_player.cape_hash));
            }

            Map<String, Object> textureResponse = J8Utils.createMap(String.class, Object.class,
                    "timestamp", System.currentTimeMillis(),
                    "profileId", current_player.uuid,
                    "profileName", current_player.name,
                    "textures", tex
            );
            String traseStr = new String(Base64.getEncoder().encode(JsonUtils.GSON.toJson(textureResponse).getBytes(UTF_8)));
            return ok(
                    J8Utils.createMap(
                            "id", current_player.uuid,
                            "name", current_player.name,
                            "properties", J8Utils.createList(
                                    J8Utils.createMap(
                                            "name", "textures",
                                            "value", traseStr
                                    )
                            )
                    )
            );

        }
        return notFound();
    }

    private Response returnImage(File f, String hash) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ImageIO.write(ImageIO.read(f), "png", buf);
        Response response = newFixedLengthResponse(Response.Status.OK, "image/png", new ByteArrayInputStream(buf.toByteArray()), buf.toByteArray().length);
        response.addHeader("Etag", String.format("\"%s\"", hash));
        response.addHeader("Cache-Control", "max-age=2592000, public");
        return response;
    }
    private Response root(){
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

    private Response status(){
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

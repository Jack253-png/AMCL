package com.mcreater.amcl.api.auth.users;

import com.mcreater.amcl.api.auth.LocalYggdrasilServer;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class OffLineUser extends AbstractUser {
    public enum SkinType {
        STEVE("000000000000300a9d83f9ec9e7fae8e", false),
        ALEX("000000000000300a9d83f9ec9e7fae8d", true);
        public final String uuid;
        public final boolean isSlim;
        SkinType(String uuid, boolean isSlim) {
            this.uuid = uuid;
            this.isSlim = isSlim;
        }
    }
    public boolean is_slim;
    public String skin;
    public String cape;
    public OffLineUser(String username, String uuid, boolean is_slim, String skin, String cape) {
        super(String.valueOf(new Random().nextInt(Integer.MAX_VALUE)), username, uuid, String.valueOf(new Random().nextInt(Integer.MAX_VALUE)));
        this.is_slim = is_slim;
        this.skin = skin;
        this.cape = cape;
    }

    public boolean hasCustomSkin() {
        return skinUseable() || capeUseable();
    }
    public File tryGetSkin() {
        return skinUseable() ? new File(skin) : null;
    }
    public File tryGetCape() {
        return capeUseable() ? new File(cape) : null;
    }
    public LocalYggdrasilServer.Player toYggdrasilPlayer() throws IOException {
        return new LocalYggdrasilServer.Player(
                uuid,
                username,
                tryGetSkin(),
                tryGetCape(),
                is_slim
        );
    }

    public void refresh() {
        // Do Nothing
    }

    public boolean vaildate() {
        return true;
    }

    private boolean fileUseable(String path){
        try {
            ImageIO.read(new File(path));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String toString(){
        return super.toString() + String.format("\nskin : %s\ncape : %s\nis Alex Model : %s", skin, cape, is_slim);
    }

    public boolean skinUseable(){
        return fileUseable(skin);
    }
    public boolean capeUseable(){
        return fileUseable(cape);
    }
}

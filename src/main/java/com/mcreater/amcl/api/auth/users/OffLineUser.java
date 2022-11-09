package com.mcreater.amcl.api.auth.users;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Random;

public class OffLineUser extends AbstractUser {
    public enum SkinType {
        STEVE("000000000000300a9d83f9ec9e7fae8e"),
        ALEX("000000000000300a9d83f9ec9e7fae8d");
        public final String uuid;
        SkinType(String uuid) {
            this.uuid = uuid;
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

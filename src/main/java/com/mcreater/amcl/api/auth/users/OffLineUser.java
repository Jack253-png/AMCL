package com.mcreater.amcl.api.auth.users;

import com.mcreater.amcl.controls.skin.SkinView;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class OffLineUser extends AbstractUser{
    public static final String STEVE = "000000000000300a9d83f9ec9e7fae8e";
    public static final String ALEX = "000000000000300a9d83f9ec9e7fae8d";
    public boolean is_slim;
    public String skin;
    public String cape;
    public OffLineUser(String username, String uuid, boolean is_slim, String skin, String cape) {
        super(String.valueOf(new Random().nextInt(Integer.MAX_VALUE)), username, uuid);
        this.is_slim = is_slim;
        this.skin = skin;
        this.cape = cape;
    }

    public void refresh() {
        // Do Nothing
    }

    private boolean fileUseable(String path){
        try {
            ImageIO.read(new File(path));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean skinUseable(){
        return fileUseable(skin);
    }
    public boolean capeUseable(){
        return fileUseable(cape);
    }
}

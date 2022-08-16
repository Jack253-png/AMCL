package com.mcreater.amcl.api.auth.users;

import com.mcreater.amcl.api.auth.MSAuth;

import java.util.Vector;

public class MicrosoftUser extends AbstractUser{
    final Vector<MSAuth.McProfileModel.McSkinModel> skins;
    public MicrosoftUser(String accessToken, String username, String uuid, Vector<MSAuth.McProfileModel.McSkinModel> skins) {
        super(accessToken, username, uuid);
        this.skins = skins;
    }
    public String toString(){
        return super.toString() + "\nSkins : " + skins;
    }
}

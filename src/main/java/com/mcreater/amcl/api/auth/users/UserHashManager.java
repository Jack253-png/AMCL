package com.mcreater.amcl.api.auth.users;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.util.J8Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.Objects;

public class UserHashManager {
    static final String AMCL_USER_DAT = "AMCL/user.dat";
    static Logger logger = LogManager.getLogger(UserHashManager.class);
    private static boolean createFile() throws IOException {
        return new File(AMCL_USER_DAT).createNewFile();
    }
    public static boolean deleteFile() throws IOException {
        return new File(AMCL_USER_DAT).delete();
    }
    public static void writeSafe(AbstractUser user) {
        try {
            write(user);
        }
        catch (Exception ignored){

        }
    }
    public static void clearUserData(){
        try {deleteFile();} catch (Exception ignored){}
        Launcher.configReader.configModel.last_uuid = null;
        Launcher.configReader.configModel.last_name = null;
        Launcher.configReader.configModel.last_accessToken = null;
        Launcher.configReader.configModel.last_refreshToken = null;
        Launcher.configReader.configModel.last_userType = "OFFLINE";
        Launcher.configReader.configModel.last_is_slim = false;
        Launcher.configReader.configModel.last_skin_path = null;
        Launcher.configReader.configModel.last_cape_path = null;
        Launcher.configReader.configModel.last_elytra_path = null;
        Launcher.configReader.write();
    }
    public static boolean vaildateSafe(AbstractUser user){
        try {
            return vaildate(read(), user);
        }
        catch (Exception e){
            logger.error("failed to validate user", e);
            return false;
        }
    }
    public static void write(AbstractUser user) throws Exception {
        deleteFile();
        createFile();
        ObjectOutputStream stream = new ObjectOutputStream(Files.newOutputStream(new File(AMCL_USER_DAT).toPath()));
        stream.writeObject(user);
        stream.close();
        logger.info(String.format("writed user : %s", J8Utils.rawToString(user)));
    }

    public static AbstractUser read() throws Exception {
        createFile();
        ObjectInputStream stream = new ObjectInputStream(Files.newInputStream(new File(AMCL_USER_DAT).toPath()));
        AbstractUser user = (AbstractUser) stream.readObject();
        stream.close();
        return user;
    }

    public static boolean vaildate(AbstractUser user1, AbstractUser user2) {
        if (user1.getClass() != user2.getClass()){
            return false;
        }
        if (user1 instanceof MicrosoftUser) {
            return base_vaildate(user1, user2);
        }
        else if (user1 instanceof OffLineUser) {
            final OffLineUser u1 = (OffLineUser) user1;
            final OffLineUser u2 = (OffLineUser) user2;

            return u1.username.equals(u2.username) &&
                    u1.uuid.equals(u2.uuid) &&
                    Objects.equals(u1.cape, u2.cape) &&
                    Objects.equals(u1.skin, u2.skin) &&
                    Objects.equals(u1.elytra, u2.elytra) &&
                    u1.is_slim == u2.is_slim;
        }
        else {
            return false;
        }
    }
    private static boolean base_vaildate(AbstractUser user1, AbstractUser user2){
        return user1.username.equals(user2.username) &&
                user1.uuid.equals(user2.uuid) &&
                user1.accessToken.equals(user2.accessToken) &&
                user1.refreshToken.equals(user2.refreshToken);
    }
}

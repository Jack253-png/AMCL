package com.mcreater.amcl.audio;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.dialogs.PopupMessage;
import com.sun.media.jfxmedia.AudioClip;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.Vector;

public class BGMManager {
    static Vector<AudioClip> musics = new Vector<>();
    static Vector<String> names = new Vector<>();
    private static long seed;
    public static AudioClip currentClip;
    private static Thread musicThread;
    public static void init() throws IOException, URISyntaxException {
        File c = new File("AMCL/Musics");
        c.mkdirs();
        for (File file : c.listFiles()){
            if (file.getName().endsWith(".mp3")) {
                musics.add(getClip(file.toURI()));
                names.add(file.getName());
            }
            else{
                PopupMessage.createMessage(String.format(Launcher.languageManager.get("ui.bgmmanager.loadfailed"), file.getName()), PopupMessage.MessageTypes.LABEL, null);
            }
        }
    }
    private static AudioClip getClip(URI p) throws URISyntaxException, IOException {
        return AudioClip.load(p);
    }
    public static void start(){
        start(new Random().nextInt(10000000));
    }
    public static void start(long seed){
        if (musics.size() > 0) {
            BGMManager.seed = seed;
            Random r = new Random();
            r.setSeed(BGMManager.seed);
            musicThread = new Thread("BGM Thread") {
                public void run() {
                    while (true) {
                        int index = r.nextInt(musics.size());
                        PopupMessage.createMessage(String.format(Launcher.languageManager.get("ui.bgmmanager.playing"), names.get(index)), PopupMessage.MessageTypes.LABEL, null);
                        AudioClip clip = musics.get(index);
                        if (currentClip != clip) {
                            currentClip = clip;
                            currentClip.play(30);
                            synchronized (currentClip) {
                                try {
                                    currentClip.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            currentClip.stop();
                        }
                    }
                }
            };
            musicThread.start();
        }
    }
    public static void stop(){
        currentClip.stop();
        musicThread.stop();
    }
}
package com.mcreater.amcl.download.tasks;

import com.mcreater.amcl.game.launch.Launch;

import java.io.IOException;

public class ForgeExtractTask extends AbstractTask{
    public Integer exit = null;
    public ForgeExtractTask(String command) {
        super(command);
    }

    @Override
    public Integer execute() throws IOException {
        Process p = Runtime.getRuntime().exec(command);
        while (true){
            try{
                System.out.println(Launch.ret(p.getInputStream()));
                System.out.println(Launch.ret(p.getErrorStream()));
                exit = p.exitValue();
                return exit;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

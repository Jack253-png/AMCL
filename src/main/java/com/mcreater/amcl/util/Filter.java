package com.mcreater.amcl.util;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class Filter {
    public static WritableImage GaussianBlur(BufferedImage image){
        WritableImage result = new WritableImage(image.getWidth(), image.getHeight());
        int pixel;
        int width = image.getWidth(),height = image.getHeight();
        int[][][] picture = new int[3][width][height];
        for(int i=image.getMinX();i<width;i++){
            for(int j=image.getMinY();j<height;j++){
                pixel = image.getRGB(i,j);
                //获取每个点的RGB值
                picture[0][i][j] = (pixel & 0xff0000) >> 16;
                picture[1][i][j] = (pixel & 0xff00) >> 8;
                picture[2][i][j] = (pixel & 0xff);
            }
        }
        picture = GaussianBlur.GaussianBlur(picture,20);
        for(int i=image.getMinX();i<width;i++){
            for(int j=image.getMinY();j<height;j++){
                result.getPixelWriter().setColor(
                        i,
                        j,
                        new Color((double) picture[0][i][j] / 255,
                                (double) picture[1][i][j] / 255,
                                (double) picture[2][i][j] / 255,
                                1
                        )
                );
            }
        }
        return result;
    }
}

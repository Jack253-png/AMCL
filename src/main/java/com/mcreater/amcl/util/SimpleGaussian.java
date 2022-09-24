package com.mcreater.amcl.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SimpleGaussian {
    public static BufferedImage get(BufferedImage img){
        int height = img.getHeight();
        int width = img.getWidth();
        int[][] martrix = new int[3][3];
        int[] values = new int[9];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                readPixel(img, i, j, values);
                fillMatrix(martrix, values);
                img.setRGB(i, j, avgMatrix(martrix));
            }
        return img;
    }

    private static void readPixel(BufferedImage img, int x, int y, int[] pixels) {
        int xStart = x - 1;
        int yStart = y - 1;
        int current = 0;
        for (int i = xStart; i < 3 + xStart; i++)
            for (int j = yStart; j < 3 + yStart; j++) {
                int tx = i;
                if (tx < 0) {
                    tx = -tx;

                } else if (tx >= img.getWidth()) {
                    tx = x;
                }
                int ty = j;
                if (ty < 0) {
                    ty = -ty;
                } else if (ty >= img.getHeight()) {
                    ty = y;
                }
                pixels[current++] = img.getRGB(tx, ty);

            }
    }

    private static void fillMatrix(int[][] matrix, int[] values) {
        int filled = 0;
        for (int[] x : matrix) {
            for (int j = 0; j < x.length; j++) {
                x[j] = values[filled++];
            }
        }
    }

    private static int avgMatrix(int[][] matrix) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (int[] x : matrix) {
            for (int j = 0; j < x.length; j++) {
                if (j == 1) {
                    continue;
                }
                Color c = new Color(x[j]);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        r = (int) (r / 0.75);
        g = (int) (g / 0.75);
        b = (int) (b / 0.75);
        return new Color(r / 8, g / 8, b / 8).getRGB();

    }
}

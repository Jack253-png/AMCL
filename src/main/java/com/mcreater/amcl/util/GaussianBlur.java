package com.mcreater.amcl.util;

public final class GaussianBlur {
    private static final int precision = 10000; // 精度，由于返回的是int数组，精度较低，因此需要将所有值同时扩大数倍，可以理解为把小数点向右移动
    private static final double E = 2.718281828459045;//自然常数e
    private static final double PI = 3.141592653589793;//圆周率

    /**
     * 快速高斯模糊
     * @param picture 三维数组,picture[a][b][c]，a表示颜色，012分别为R,G,B;b和c代表尺寸，宽度为b，高度为c
     * @param radius 半径
     * @return 格式如同picture的数组
     */
    public static int[][][] GaussianBlur(int[][][] picture,int radius){
        int i, j, x, R, G, B, proportion, subscript;
        int[] matrix = LinearNormalDistribution(radius,1.5);
        int width = picture[0].length, height = picture[0][0].length;
        int[][][] color_1 = new int[3][width][height]; // 用来存高斯模糊后的数据
        int[][][] color_2 = new int[3][width][height]; // 临时存储纵向滤波之后的数据
        //纵向滤波
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                R = G = B = 0;
                for (x = j - radius; x <= j + radius; x++) {
                    proportion = matrix[x + radius - j];
                    subscript = (x >= 0 && x < height) ? x : 2 * j - x; // 如果坐标越界了，则计算对称点来代替
                    R += picture[0][i][subscript] * proportion;
                    G += picture[1][i][subscript] * proportion;
                    B += picture[2][i][subscript] * proportion;
                }
                color_2[0][i][j] = R / precision;
                color_2[1][i][j] = G / precision;
                color_2[2][i][j] = B / precision;
            }
        }
        //横向滤波
        for (i = 0; i < height; i++) {
            for (j = 0; j < width; j++) {
                R = G = B = 0;
                for (x = j - radius; x <= j + radius; x++) {
                    proportion = matrix[x + radius - j];
                    subscript = (x >= 0 && x < width) ? x : 2 * j - x;
                    R += color_2[0][subscript][i] * proportion;
                    G += color_2[1][subscript][i] * proportion;
                    B += color_2[2][subscript][i] * proportion;
                }
                //注意for语句中i代表高度，j代表宽度，所以下面三个语句的i和j并没有写错位置
                color_1[0][j][i] = R / precision;
                color_1[1][j][i] = G / precision;
                color_1[2][j][i] = B / precision;
            }
        }
        return color_1;
    }

    /**
     * 慢速高斯模糊，采用二维正态分布的方法来处理图像
     * @param picture 三维数组,picture[a][b][c]，a表示颜色，012分别为R,G,B;b和c代表尺寸，宽度为b，高度为c
     * @param radius 半径
     * @return 格式如同picture的数组
     */
    public static int[][][] SlowGaussianBlur(int[][][] picture,int radius){
        //flag为真时计算加权，为假时直接代入矩阵
        int[][] matrix = NormalDistribution(radius,1.5);
        int i, j, x, y, R, G, B, proportion, left, right, width = picture[0].length, height = picture[0][0].length;
        int[][][] color = new int[3][width][height];
        //选取每个点
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                //选取半径为radius的矩阵
                R = G = B = 0;
                for (x = i - radius; x <= i + radius; x++) {
                    for (y = j - radius; y <= j + radius; y++) {
                        //求出颜色
                        proportion = matrix[x + radius - i][y + radius - j];
                        left = (x >= 0 && x < width) ? x : 2 * i - x;
                        right = (y >= 0 && y < height) ? y : 2 * j - y;
                        R += picture[0][left][right] * proportion;
                        G += picture[1][left][right] * proportion;
                        B += picture[2][left][right] * proportion;
                    }
                }
                color[0][i][j] = R / precision;
                color[1][i][j] = G / precision;
                color[2][i][j] = B / precision;
            }
        }
        return color;
    }

    /**
     * 用一维正态分布函数来计算“权值列向量”，效率较高
     * @param radius 模糊半径
     * @param SIGMA 正态分布参数，如果自己没把握，就填1.5
     * @return “权值列向量”
     */
    private static int[] LinearNormalDistribution(int radius,double SIGMA){
        int[] matrix = new int[2 * radius + 1]; // 定义一个列向量
        int sum, i;
        //计算各个点的正态分布值
        sum = matrix[radius] = (int) (precision / (2 * PI * SIGMA * SIGMA)); // sum的初值为向量中心点的值，例如向量(1,2,3,2,1)，则初值为3
        for (i = 1; i <= radius; i++) {
            //根据对称性，可以减少一倍的运算量，i=0的情况已经在sum初值那一步考虑
            matrix[radius-i] = matrix[radius+i] = (int) ((Math.pow(E, -i * i / (2 * SIGMA * SIGMA)) / (2 * PI * SIGMA * SIGMA)) * precision);
            sum += matrix[radius+i] * 2; // 计算向量所有值之和
        }
        for (i = 0; i < 2 * radius + 1; i++) {
            matrix[i] = matrix[i] * precision / sum; // 所有值都除以sum，确保它们的和为“1”，由于扩大了10000倍，所以这个“1”实际上应该是10000
        }
        return matrix;
    }

    /**
     * 用二维正态分布函数来计算权值矩阵，效率较低
     * @param radius 模糊半径
     * @param SIGMA 正态分布参数，如果自己没把握，就填1.5
     * @return 权值矩阵
     */
    private static int[][] NormalDistribution(int radius, double SIGMA) {
        int sum = 0, i, j;
        int[][] matrix = new int[2 * radius + 1][2 * radius + 1]; // 定义一个矩阵
        //计算各个点的正态分布值
        for (i = 0; i <= radius; i++) {
            for (j = 0; j <= radius; j++) {
                //写入矩阵并累加，根据矩阵的对称性可以减少3/4的运算量
                matrix[radius-i][radius-j]
                        = matrix[radius-i][radius+j]
                        = matrix[radius+i][radius-j]
                        = matrix[radius+i][radius+j]
                        = (int) (Math.pow(E, -(i * i + j * j) / (2 * SIGMA * SIGMA)) / (2 * PI * SIGMA * SIGMA) * precision);
                sum += 4 * matrix[radius+i][radius+j];
            }
        }
        //计算权值
        for (i = 0; i <= 2 * radius; i++) {
            for (j = 0; j <= 2 * radius; j++) {
                matrix[i][j] = matrix[i][j] * precision / sum; // 所有值都除以sum，确保它们的和为“1”，由于扩大了10000倍，所以这个“1”实际上应该是10000
            }
        }
        return matrix;
    }
}

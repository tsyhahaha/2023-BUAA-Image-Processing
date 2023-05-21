package ImgProcess.Method;

import ImgProcess.Utils.Function;
import ImgProcess.Utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Enhance {
    /** 灰度线性拉伸 **/
    public static BufferedImage lineEnhance(BufferedImage src) {
        if(src == null) {
            return null;
        }
        src = Utils.convert2Gray(src);
        for (int i = 0; i < src.getWidth(); i++) {
            for (int j = 0; j < src.getHeight(); j++) {
                int rgb = src.getRGB(i, j);
                Color color = new Color(rgb);
                double input = color.getRed();
                double output;
                output = Function.f(input);
                src.setRGB(i, j, new Color((int)output, (int)output, (int)output).getRGB());
            }
        }
        return src;
    }

    /** 直方图均衡 **/
    public static BufferedImage ImgCalcHist(BufferedImage src) {
        BufferedImage result = Utils.convert2Gray(src);
        int height = result.getHeight();
        int width = result.getWidth();
        int size = height * width;

        System.out.println(height+" "+width);
        // 直方图
        int[] hist = new int[256];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = result.getRGB(i, j);
                int index = new Color(pixel).getRed();
                hist[index]++;
            }
        }
        // 归一化
        float[] hist_normalization = new float[256];
        for (int i = 0; i < 255; i++) {
            hist_normalization[i] = (float) hist[i] / size;
        }
        // 累积直方图
        float[] hist_add = new float[256];
        for (int i = 0; i < 256; i++) {
            if (0 == i) hist_add[i] = hist_normalization[i];
            else hist_add[i] = hist_add[i - 1] + hist_normalization[i];
        }
        //直方图均衡化,映射
        int[] hist_result = new int[256];
        for (int i = 0; i < 256; i++) {
            hist_result[i] = (int) (255.0 * hist_add[i] + 0.5);
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = result.getRGB(i, j);
                Color color = new Color(pixel);
                int gray = hist_result[color.getRed()];
                result.setRGB(i, j, new Color(gray, gray, gray).getRGB());
            }
        }
        return result;
    }
}

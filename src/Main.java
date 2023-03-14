import org.opencv.core.*;

import java.awt.*;
import java.awt.image.BufferedImage;


public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        String imgPath = "data\\dog.jpg";

        BufferedImage src = Utils.readImg(imgPath);
        String gray_src = "data\\dog_dead.jpg";
        String processed_src_2 = "data\\dog_hist.jpg";
        String processed_src_3 = "data\\dog_histPlusLineEn.jpg";
        String processed_src_4 = "data\\dog_lineEn.jpg";

        //Imgproc.resize(src, src, new Size(src.cols()/2,src.rows()/2));
        // 原图的灰度图
        assert src != null;
        BufferedImage dst0 = Utils.convert2Gray(src);
        Utils.writeImg(dst0, gray_src);
        // 仅做直方图均衡
        BufferedImage dst = ImgCalcHist(src);
        Utils.writeImg(dst, processed_src_2);
        // 仅灰度线性拉伸
        BufferedImage _dst3 = ImgCalcHist(src);
        BufferedImage dst3 = lineEnhance(_dst3);
        Utils.writeImg(dst3, processed_src_3);
        // 先做直方图均衡，再做灰度线性拉伸
        BufferedImage dst4 = lineEnhance(src);
        Utils.writeImg(dst4, processed_src_4);

    }

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

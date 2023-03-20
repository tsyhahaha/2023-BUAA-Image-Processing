import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Utils {

    public static BufferedImage readImg(String path) {
        try {
            // 读取图片文件
            File imageFile = new File(path);
            return ImageIO.read(imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeImg(BufferedImage image, String fileName) {
        try {
            String format = fileName.substring(fileName.indexOf(".")+1);
            File output = new File(fileName);
            ImageIO.write(image, format, output);
        } catch (IOException e) {}
    }

    public static BufferedImage convert2Gray(BufferedImage image) {
        // 获取图片的宽度和高度
        int width = image.getWidth();
        int height = image.getHeight();

        // 创建新的灰度图像
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        // 遍历原图像的像素值，并将像素的RGB颜色分量转换为灰度值
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                Color color = new Color(pixel);
                int max, min;
                {
                    max = color.getBlue();
                    min = color.getRed();
                    if(color.getRed() > color.getBlue()) {
                        min = color.getBlue();
                        if(color.getBlue() > color.getGreen()) {
                            min = color.getGreen();
                        }
                        max = color.getRed();
                        if(color.getRed()<color.getGreen()) {
                            max = color.getGreen();
                        }
                    }

                }

                int gray = (int) (0.8*max+0.2*min);
//                int gray = (int)(0.3 * color.getRed() + 0.59*color.getGreen() + 0.11*color.getBlue());
                // 将灰度值设置为像素的RGB颜色分量
                Color grayColor = new Color(gray, gray, gray);
                grayImage.setRGB(x, y, grayColor.getRGB());
            }
        }
        return grayImage;
    }
}
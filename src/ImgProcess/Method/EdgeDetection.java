package ImgProcess.Method;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static ImgProcess.Method.Fix.nonMaxSup;

public class EdgeDetection {

    public static BufferedImage sobelEdgeDetection(BufferedImage image, int type) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Sobel kernels
        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0, gy = 0;

                // Convolution with Sobel kernels
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        Color pixel = new Color(image.getRGB(x + i, y + j));
                        int grayscale = (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;

                        gx += grayscale * sobelX[j + 1][i + 1];
                        gy += grayscale * sobelY[j + 1][i + 1];
                    }
                }

                int gradient = (int) Math.sqrt(gx * gx + gy * gy);

                // Set the gradient as the new pixel value
                Color newPixel = new Color(gradient % 256, gradient % 256, gradient % 256);
                result.setRGB(x, y, newPixel.getRGB());
            }
        }
        if(type == 1) {
            return nonMaxSup(result, 5);
        }
        return result;
    }

    public static BufferedImage laplacianEdgeDetection(BufferedImage image, int type) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Laplacian kernel
        int[][] laplacianKernel = {{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}};

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int sum = 0;

                // Convolution with Laplacian kernel
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        Color pixel = new Color(image.getRGB(x + i, y + j));
                        int grayscale = (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;

                        sum += grayscale * laplacianKernel[j + 1][i + 1];
                    }
                }

                // Set the absolute sum as the new pixel value
                int newValue = Math.min(Math.max(sum, 0), 255);
                Color newPixel = new Color(newValue, newValue, newValue);
                result.setRGB(x, y, newPixel.getRGB());
            }
        }
        if (type == 1) {
            System.out.println("in processing of non max suppress...");
            return nonMaxSup(result, 4);
        }
        return result;
    }


}

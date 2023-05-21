package ImgProcess.Method;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.pow;

public class Fix {
    public static BufferedImage GammaFix(BufferedImage image, float kFactor) {
        int[] LUT = new int[256];
        for (int i = 0; i < 256; i++) {
            float f = (float) ((i - 0.5) / 255);
            f = (float) (pow(f, kFactor));
            LUT[i] = (int) (f * 255.0 - 0.5f);
        }
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = image.getRGB(i, j);
                Color color = new Color(pixel);
                int B = color.getBlue();
                int G = color.getGreen();
                int R = color.getRed();
                result.setRGB(i, j, new Color(LUT[R], LUT[G], LUT[B]).getRGB());
            }
        }
        return result;
    }

    public static BufferedImage GaussianSmoothing(BufferedImage image, double sigma, int size) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Generate Gaussian kernel
        double[][] kernel = generateGaussianKernel(sigma, size);

        // Apply Gaussian smoothing
        BufferedImage smoothed = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = size / 2; y < height - size / 2; y++) {
            for (int x = size / 2; x < width - size / 2; x++) {
                double sumR = 0.0, sumG = 0.0, sumB = 0.0;

                // Convolution with Gaussian kernel
                for (int j = -size / 2; j <= size / 2; j++) {
                    for (int i = -size / 2; i <= size / 2; i++) {
                        int pixelX = x + i;
                        int pixelY = y + j;
                        Color pixel = new Color(image.getRGB(pixelX, pixelY));

                        double weight = kernel[j + size / 2][i + size / 2];
                        sumR += weight * pixel.getRed();
                        sumG += weight * pixel.getGreen();
                        sumB += weight * pixel.getBlue();
                    }
                }

                int smoothedR = (int) Math.round(sumR);
                int smoothedG = (int) Math.round(sumG);
                int smoothedB = (int) Math.round(sumB);
                Color smoothedPixel = new Color(smoothedR, smoothedG, smoothedB);
                smoothed.setRGB(x, y, smoothedPixel.getRGB());
            }
        }

        // Copy smoothed image back to the original image
        for (int y = size / 2; y < height - size / 2; y++) {
            for (int x = size / 2; x < width - size / 2; x++) {
                image.setRGB(x, y, smoothed.getRGB(x, y));
            }
        }
        return image;
    }

    private static double[][] generateGaussianKernel(double sigma, int size) {
        double[][] kernel = new double[size][size];
        double twoSigmaSquare = 2.0 * sigma * sigma;
        double sum = 0.0;

        for (int y = -size / 2; y <= size / 2; y++) {
            for (int x = -size / 2; x <= size / 2; x++) {
                double exponent = -(x * x + y * y) / twoSigmaSquare;
                double weight = Math.exp(exponent) / (Math.PI * twoSigmaSquare);
                kernel[y + size / 2][x + size / 2] = weight;
                sum += weight;
            }
        }

        // Normalize the kernel
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                kernel[y][x] /= sum;
            }
        }

        return kernel;
    }

    public static BufferedImage nonMaxSup(BufferedImage inputImage, int alpha) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Iterate over each pixel in the image
        int max = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int centerPixel = inputImage.getRGB(x, y) & 255;
                if (centerPixel > max) {
                    max = centerPixel;
                }
            }
        }
        for (int y = 1;y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int centerPixel = inputImage.getRGB(x, y);
                int[] neighborPixels = getNeighborPixels(inputImage, x, y);

                boolean isMaximum = true;
                int maxvalue = 0;
                int maxvalue2 = 0;
                int rank = 0;

                // Check if the center pixel is a local maximum in the neighborhood
                for (int neighborPixel : neighborPixels) {
                    if (centerPixel < neighborPixel) {
                        rank += 1;
                        if(neighborPixel > maxvalue) {
                            maxvalue2 = maxvalue;
                            maxvalue = neighborPixel;
                        } else if(neighborPixel > maxvalue2) {
                            maxvalue2 = neighborPixel;
                        }
                    }
                    if (rank > 2) {
                        isMaximum = false;
                        break;
                    }
                }
                int region = 20;
                if (rank == 2 && (Math.abs((maxvalue2 + maxvalue) / 2 - centerPixel) & 255) > region ||
                rank == 1 && (Math.abs(maxvalue - centerPixel) & 255) < region) {
                    isMaximum = false;
                }
                if (isMaximum && (centerPixel & 255) > max / alpha) {
                    int rgb = (255 << 16) | (255 << 8) | (255);
                    outputImage.setRGB(x, y, rgb); // Set edge pixel to white
                } else {
                    outputImage.setRGB(x, y, 0); // Set non-edge pixel to black
                }
            }
        }
        return outputImage;
    }

    private static int[] getNeighborPixels(BufferedImage image, int x, int y) {
        int[] neighborPixels = new int[8];
        int index = 0;

        for (int ky = -1; ky <= 1; ky++) {
            for (int kx = -1; kx <= 1; kx++) {
                if (kx == 0 && ky == 0) {
                    continue; // Skip the center pixel
                }

                int pixel = image.getRGB(x + kx, y + ky);
                neighborPixels[index] = pixel;
                index++;
            }
        }
        return neighborPixels;
    }
}

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;


public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        String imgPath = "data\\dog.jpg";
        /*
         * IMREAD_UNCHANGED = -1 ：不进行转化，比如保存为了16位的图片，读取出来仍然为16位。
         * IMREAD_GRAYSCALE = 0 ：进行转化为灰度图，比如保存为了16位的图片，读取出来为8位，类型为CV_8UC1。
         * IMREAD_COLOR = 1 ：进行转化为三通道图像。
         * IMREAD_ANYDEPTH = 2 ：如果图像深度为16位则读出为16位，32位则读出为32位，其余的转化为8位。
         * IMREAD_ANYCOLOR = 4 ：图像以任何可能的颜色格式读取
         * IMREAD_LOAD_GDAL = 8 ：使用GDAL驱动读取文件，GDAL(Geospatial Data Abstraction
         * Library)是一个在X/MIT许可协议下的开源栅格空间数据转换库。它利用抽象数据模型来表达所支持的各种文件格式。
         *	它还有一系列命令行工具来进行数据转换和处理。
         */
        Mat src = Imgcodecs.imread(imgPath, 0);
        String gray_src = "data\\dog_processed_1.jpg";
        String processed_src_2 = "data\\dog_processed_2.jpg";
        String processed_src_3 = "data\\dog_processed_3.jpg";
        String processed_src_4 = "data\\dog_processed_4.jpg";

        //Imgproc.resize(src, src, new Size(src.cols()/2,src.rows()/2));
        // 原图的灰度图
        Imgcodecs.imwrite(gray_src, src);
        // 仅做直方图均衡
        Mat dst = ImgCalcHist(src);
        Imgcodecs.imwrite(processed_src_2, dst);
        // 仅灰度线性拉伸
        Mat _dst3 = ImgCalcHist(src);
        Mat dst3 = lineEnhance(_dst3);
        Imgcodecs.imwrite(processed_src_3, dst3);
        // 先做直方图均衡，再做灰度线性拉伸
        Mat dst4 = lineEnhance(src);
        Imgcodecs.imwrite(processed_src_4, dst4);

    }

    /** 灰度线性拉伸 **/
    public static Mat lineEnhance(Mat src) {
        Mat result = src.clone();
        for (int i = 0; i < src.height(); i++) {
            for (int j = 0; j < src.width(); j++) {
                double[] inputArr = result.get(i, j);
                if(inputArr == null) {
                    result.put(i, j, 0);
                } else {
                    double input = inputArr[0];
                    double output;
                    output = Function.f(input);
                    result.put(i, j, output);
                }
            }
        }
        return result;
    }

    /** 直方图均衡 **/
    public static Mat ImgCalcHist(Mat src) {
        Mat result = src.clone();
        int height = result.height();
        int width = result.width();
        int size = height * width;

        // 直方图
        int[] hist = new int[256];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int index = (int) result.get(i, j)[0];
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
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = (int) result.get(i, j)[0];
                result.put(i, j, hist_result[pixel]);
            }
        }
        return result;
    }
}

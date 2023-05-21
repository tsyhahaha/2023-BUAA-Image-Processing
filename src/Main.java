import ImgProcess.Method.EdgeDetection;
import ImgProcess.Utils.Utils;

import java.awt.image.BufferedImage;

import static ImgProcess.Method.Fix.GaussianSmoothing;


public class Main {
    public static void main(String[] args) {
        String imgPath = "data\\lena.jpg";

        BufferedImage src = Utils.readImg(imgPath);
        assert src != null;
        src = Utils.convert2Gray(src);

        src = GaussianSmoothing(src, 2, 3);

        BufferedImage result = EdgeDetection.laplacianEdgeDetection(src, 0);
        Utils.writeImg(result, "data/laplacian_with_Gauss.jpg");
        BufferedImage result2 = EdgeDetection.sobelEdgeDetection(src, 0);
        Utils.writeImg(result2, "data/sobel_with_Gauss.jpg");
    }


}

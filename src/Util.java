import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for miscellaneous OpenCV operations.
 *
 * @author Liam Wang
 */

public class Util {

    /**
     * Filter list to only contain contours with a given number of vertices
     *
     * @param input List of contours
     * @param vertices Number of vertices of a contour to include
     * @param epsilon Epsilon to use for OpenCV Imgproc.approxPolyDP function
     * @return Filtered list
     */
    public static List<MatOfPoint> filterPoly(List<MatOfPoint> input, int vertices, int epsilon) {
        List<MatOfPoint> output = new ArrayList<>(input.size());
        for (MatOfPoint mat : input) {
            MatOfPoint2f mat2f = new MatOfPoint2f();
            MatOfPoint2f polymat2f = new MatOfPoint2f();
            mat.convertTo(mat2f, CvType.CV_32FC2);
            Imgproc.approxPolyDP(mat2f, polymat2f, epsilon, true);
            MatOfPoint result = new MatOfPoint();
            polymat2f.convertTo(result, CvType.CV_32S);
            if (result.rows() == vertices) {
                output.add(result);
            }
        }
        return output;
    }


    /**
     * Read an an image file into a BufferedImage using ImageIO
     *
     * @param path The path to the file
     * @return Resulting BufferedImage
     */
    public static BufferedImage readImage(String path) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            return null;
        }
        return image;
    }

    /**
     * Convert an BufferedImage to OpenCV Mat
     * Source: https://stackoverflow.com/a/34293310
     *
     * @param bufferedImage The BufferedImage to convert
     * @return Resulting Mat
     */
    public static Mat bufferedImageToMat(BufferedImage bufferedImage) {
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    /**
     * Convert an OpenCV Mat to BufferedImage
     * Source: https://stackoverflow.com/a/30263918
     *
     * @param matrix The Mat to convert
     * @param bufferedImage An optional BufferedImage to reuse (null if not used)
     * @return Resulting BufferedImage
     */
    public static BufferedImage matToBufferedImage(Mat matrix, BufferedImage bufferedImage)
    {
        if ( matrix != null ) {
            int cols = matrix.cols();
            int rows = matrix.rows();
            int elemSize = (int)matrix.elemSize();
            byte[] data = new byte[cols * rows * elemSize];
            int type;
            matrix.get(0, 0, data);
            switch (matrix.channels()) {
                case 1:
                    type = BufferedImage.TYPE_BYTE_GRAY;
                    break;
                case 3:
                    type = BufferedImage.TYPE_3BYTE_BGR;
                    // bgr to rgb
                    byte b;
                    for(int i=0; i<data.length; i=i+3) {
                        b = data[i];
                        data[i] = data[i+2];
                        data[i+2] = b;
                    }
                    break;
                default:
                    return null;
            }

            // Reuse existing BufferedImage if possible
            if (bufferedImage == null || bufferedImage.getWidth() != cols || bufferedImage.getHeight() != rows || bufferedImage.getType() != type) {
                bufferedImage = new BufferedImage(cols, rows, type);
            }
            bufferedImage.getRaster().setDataElements(0, 0, cols, rows, data);
        } else { // mat was null
            bufferedImage = null;
        }
        return bufferedImage;
    }

    /**
     * Save a png image
     *
     * @param image The image to save
     * @param path The path at which to save the image (e.g. C:\image.png)
     */
    public static void savePNGImage(BufferedImage image, String path) {
        try {
            File outputfile = new File(path);
            ImageIO.write(image, "png", outputfile);
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Take a single picture using a usb camera
     *
     * @param camIndex Camera index
     * @return Resulting Mat
     */
    public static Mat takePicture(int camIndex) {
        VideoCapture camera = new VideoCapture(camIndex);
        Mat frame = new Mat();
        camera.read(frame);
        camera.release();
        return frame;
    }
}

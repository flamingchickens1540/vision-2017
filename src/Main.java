import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.calib3d.*;

import java.util.List;

public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {

        // Create instance of an image processing pipeline generated by GRIP (must end with filterContours?!?!)
        ContoursPipeline pipeline = new ContoursPipeline();

        // Distortion coefficients and camera matrix for the webcam on my computer
        // TODO: Write calibration program to calculate and load these automatically
        Mat cameraMatrix = Mat.eye(3, 3, CvType.CV_64F);
        cameraMatrix.put(0, 0, 492.27331);
        cameraMatrix.put(1, 1, 492.14526);
        cameraMatrix.put(0, 2, 327.04178);
        cameraMatrix.put(1, 2, 202.45735);
        cameraMatrix.put(2,2,1.0);
        MatOfDouble distCoeffs = new MatOfDouble(0.0426968969, -0.0503434464, -0.0003085729, 0.0001547382, 0);

        // Coordinates of vertices for a sample rectangle
        MatOfPoint3f objectPoints = new MatOfPoint3f(
                new Point3(2.064, 3.4925, 0.0),
                new Point3(-2.064, 3.4925, 0.0),
                new Point3(-2.064, -3.4925, 0.0),
                new Point3(2.064, -3.4925, 0.0));

        // Start the usb camera with the index n
        VideoCapture camera = new VideoCapture(0);

        // Create a window for displaying the output
        ImageDisplayWindow window = new ImageDisplayWindow("Quad Finder Output", 1000, 1000);

        Mat frame = new Mat();
        while (true) {
            camera.read(frame); // Take a picture with the camera
            pipeline.process(frame); // Process with the image with GRIP pipeline

            // Filter contours with four vertices
            List<MatOfPoint> quads = Util.filterPoly(pipeline.filterContoursOutput(),4, 10);
            // TODO: filter quadrilaterals with the wrong length to width ratio
            // TODO: filter non-convex quadrilaterals

            Imgproc.drawContours(frame, quads, -1, new Scalar(0, 0, 255), 3);

            window.updateImage(Util.matToBufferedImage(frame, null));

            // TODO: solve for the position of ALL rectangles in image
            if (quads.size()>=1) {
                MatOfPoint2f imagePoints = new MatOfPoint2f(quads.get(0).toArray());
                // TODO: figure out which rectangle points correspond to which points on image

                Mat rvec = new Mat();
                Mat tvec = new Mat();
                Calib3d.solvePnP(objectPoints, imagePoints, cameraMatrix, distCoeffs, rvec, tvec);

            }
        }
//        camera.release();
        // TODO: figure out how to release the camera when the window is closed
    }
}
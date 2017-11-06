import javax.swing.*;
import java.awt.*;

/**
 * Simple class for displaying images on a window
 *
 * @author Liam Wang
 */
class ImageDisplayWindow {
    private JFrame frame;
    private JLabel oldLabel;

    /**
     * @param title Title of window
     * @param width Initial width of window
     * @param height Initial height of window
     */
    ImageDisplayWindow(String title, int width, int height) {
        frame=new JFrame(title);
        frame.setLayout(new FlowLayout());
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Update the image displayed on the window
     *
     * @param image Image to be displayed on the window
     */
    void updateImage(Image image) {

        // Resize image to fit window size
        int imgHeight;
        int imgWidth;
        if ((float)image.getHeight(null)/image.getWidth(null) > (float)frame.getHeight()/frame.getWidth()) {
            imgHeight = frame.getHeight();
            imgWidth = imgHeight*image.getWidth(null)/image.getHeight(null);
        } else {
            imgWidth = frame.getWidth();
            imgHeight = imgWidth*image.getHeight(null)/image.getWidth(null);
        }

        JLabel label=new JLabel();
        label.setIcon(new ImageIcon(image.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH)));
        if (oldLabel!=null) {
            frame.remove(oldLabel);
        }
        frame.add(label);
        oldLabel = label;
        frame.setVisible(true);
    }
}

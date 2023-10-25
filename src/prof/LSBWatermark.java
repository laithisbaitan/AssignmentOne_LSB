package prof;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LSBWatermark {
    public static void main(String[] args) {
        try {
            // Load the original image and WM image
            BufferedImage originalImage = ImageIO.read(new File("C:\\Users\\MKhanafseh\\Desktop\\Birzeit\\FistSemester2023-2024\\EncryptionTheory\\WaterMark\\LSBWM\\OneDrive_1_10-11-2023\\Test.png"));
            BufferedImage watermarkImage = ImageIO.read(new File("C:\\Users\\Mkhanafseh\\Desktop\\Birzeit\\FistSemester2023-2024\\EncryptionTheory\\WaterMark\\LSBWM\\OneDrive_1_10-11-2023\\111.png"));

            // Ensure the images have the same dimensions
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            watermarkImage = resizeImage(watermarkImage, width, height);

            // Embed watermark using LSB
            BufferedImage watermarkedImage = embedWatermark(originalImage, watermarkImage);

            // Save the watermarked image
            ImageIO.write(watermarkedImage, "PNG", new File("C:\\Users\\Mkhanafseh\\Desktop\\Birzeit\\FistSemester2023-2024\\EncryptionTheory\\WaterMark\\LSBWM\\OneDrive_1_10-11-2023\\watermarked_image.png"));

            System.out.println("WM now embeded successfuly, congrate");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Resize the WM image to match the dimensions of the original image
    private static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        resizedImage.getGraphics().drawImage(image, 0, 0, width, height, null);
        return resizedImage;
    }

    // Embed the WM in the LSB of the original image
    private static BufferedImage embedWatermark(BufferedImage originalImage, BufferedImage watermarkImage) {
        BufferedImage watermarkedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                int originalRGB = originalImage.getRGB(x, y);
                int watermarkRGB = watermarkImage.getRGB(x, y);

                int combinedRGB = originalRGB & 0xFFFEFEFE; // Clear the least significant bit
                combinedRGB |= watermarkRGB >> 16 & 0x1; // Set the watermark's red component LSB
                combinedRGB |= watermarkRGB >> 8 & 0x1;  // Set the watermark's green component LSB
                combinedRGB |= watermarkRGB & 0x1;       // Set the watermark's blue component LSB

                watermarkedImage.setRGB(x, y, combinedRGB);
            }
        }

        return watermarkedImage;
    }
}

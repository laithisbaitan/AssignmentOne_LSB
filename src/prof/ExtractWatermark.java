package prof;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ExtractWatermark {
    public static void main(String[] args) {
        try {
            // Load the WM image
            BufferedImage watermarkedImage = ImageIO.read(new File("C:\\Users\\Mkhanafseh\\Desktop\\Birzeit\\FistSemester2023-2024\\EncryptionTheory\\WaterMark\\LSBWM\\OneDrive_1_10-11-2023\\watermarked_image.png"));

            // Extract watermark from the watermarked image
            BufferedImage extractedWatermark = extractWatermark(watermarkedImage);

            // Save the extracted watermark image
            ImageIO.write(extractedWatermark, "PNG", new File("C:\\Users\\Mkhanafseh\\Desktop\\Birzeit\\FistSemester2023-2024\\EncryptionTheory\\WaterMark\\LSBWM\\OneDrive_1_10-11-2023\\extracted_watermark.png"));

            System.out.println("WM is extracted successfully, Congrat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Extract the watermark from the LSB of the WM image
    private static BufferedImage extractWatermark(BufferedImage watermarkedImage) {
        int width = watermarkedImage.getWidth();
        int height = watermarkedImage.getHeight();
        BufferedImage extractedWatermark = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int watermarkedRGB = watermarkedImage.getRGB(x, y);

                // Extract the LSBs as WM pixels
                int watermarkPixel = (watermarkedRGB & 0x1) * 255; // Reconstruct pixel value

                // Create a grayscale pixel with the extracted value
                int grayscalePixel = (watermarkPixel << 16) | (watermarkPixel << 8) | watermarkPixel;

                extractedWatermark.setRGB(x, y, grayscalePixel);
            }
        }

        return extractedWatermark;
    }
}
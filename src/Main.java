import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static int AVG_BIT_MESSAGE = 4000;
    public static int BITS_SPACE;
    public static int BIT_SKIP = 0;
    public static int messageIndex = 0;
    public static StringBuilder extractMessage = new StringBuilder();

    public static void main(String[] args) throws IOException {
        String cleanImage = "PlayGamesAd.png";
        StringBuilder messageToHide = stringToBinary("Hi my name is laith");

        hideMessage(cleanImage, messageToHide);

        StringBuilder bitMessage = getMessage("ModifiedPlayGamesAd.png");
        System.out.println("Bit message: " + bitMessage.substring(0, messageToHide.length()*8));

        String message = binaryToString(String.valueOf(bitMessage));

        System.out.println("Secret message was: " + message);

//        mm("PlayGamesAd.png");
    }

//    public static void mm(String cleanImageName) throws IOException {
//        File file = new File("C:\\ALL\\college\\year5 semmester 1\\COMP438 encryption\\Assignment 1\\AssignmentOne_LSB\\src\\CleanImages\\" +
//                cleanImageName);
//        BufferedImage img = ImageIO.read(file);
//
//        // Saving the modified image as PNG
//        file = new File("C:\\ALL\\college\\year5 semmester 1\\COMP438 encryption\\Assignment 1\\AssignmentOne_LSB\\src\\CoverImages\\" +
//                "Modified" + cleanImageName);
//        ImageIO.write(img, "png", file); // Use "png" format for lossless compression
//        System.out.println("Finished!!!");
//    }


    public static StringBuilder stringToBinary(String messageToHide) {
        // Convert the message to binary
        StringBuilder binaryMessage = new StringBuilder();
        for (char c : messageToHide.toCharArray()) {
            String binaryChar = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            binaryMessage.append(binaryChar);
        }
        return binaryMessage;
    }
    public static String binaryToString(String binaryMessage) {
        StringBuilder originalMessage = new StringBuilder();
        for (int i = 0; i < binaryMessage.length(); i += 8) {
            if (i + 8 <= binaryMessage.length()) {
                String binaryChar = binaryMessage.substring(i, i + 8);
                int charValue = Integer.parseInt(binaryChar, 2);
                originalMessage.append((char) charValue);
            } else {
                // Handle the case when there are fewer than 8 bits left
                break;
            }
        }
        return originalMessage.toString();
    }

    public static void hideMessage(String cleanImageName, StringBuilder messageToHide) throws IOException {
        // Reading the image
        File file = new File("C:\\ALL\\college\\year5 semmester 1\\COMP438 encryption\\Assignment 1\\AssignmentOne_LSB\\src\\CleanImages\\" +
                cleanImageName);
        BufferedImage img = ImageIO.read(file);
        BITS_SPACE = (img.getHeight() * img.getWidth() * 3) / AVG_BIT_MESSAGE;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (messageIndex < messageToHide.length()) {
                    // Get each pixel pixel
                    int pixel = img.getRGB(x, y);
                    // Extract the color value from the pixel
                    Color color = new Color(pixel, true);
                    // Split the R G B into values to be modified
                    int red = color.getRed();
                    int green = color.getGreen();
                    int blue = color.getBlue();
                    // Modifying the RGB values and create color
                    red = modifyBit(red, messageToHide);
                    green = modifyBit(green, messageToHide);
                    blue = modifyBit(blue, messageToHide);
                    // Set the new modified color back to the pixel
                    color = new Color(red, green, blue);
                    img.setRGB(x, y, color.getRGB());
                }else{
                    break;
                }
            }
        }
        // Saving the modified image
        file = new File("C:\\ALL\\college\\year5 semmester 1\\COMP438 encryption\\Assignment 1\\AssignmentOne_LSB\\src\\CoverImages\\" +
                "Modified" + cleanImageName);
        ImageIO.write(img, "png", file);
        System.out.println("Finished!!!");
        System.out.println("Red: "+ new Color(img.getRGB(0,0)).getRed());
    }

    public static int modifyBit(int component, StringBuilder messageToHide) {
        if (BIT_SKIP == 0) {
            BIT_SKIP = BITS_SPACE;
            int bitToEmbed;
            bitToEmbed = messageToHide.charAt(messageIndex) - '0';
//            System.out.println("Bit to embed: " + bitToEmbed);

//            System.out.println("Before: "+ component);
            component = (component & ~1) | bitToEmbed;
            System.out.println("After: "+ component);
            messageIndex++;
        } else {
            BIT_SKIP--;
        }
        return component;
    }

    public static StringBuilder getMessage(String CoverImageName) throws IOException {
        BIT_SKIP = BITS_SPACE;
        // Reading the image
        File file = new File("C:\\ALL\\college\\year5 semmester 1\\COMP438 encryption\\Assignment 1\\AssignmentOne_LSB\\src\\CoverImages\\" +
                CoverImageName);
        BufferedImage img = ImageIO.read(file);
        BITS_SPACE = (img.getHeight() * img.getWidth() * 3) / AVG_BIT_MESSAGE;
        extractMessage.setLength(0);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                // Get each pixel pixel
                int pixel = img.getRGB(x, y);
                // Extract the color value from the pixel
                Color color = new Color(pixel, true);
                // Split the R G B into values to be modified
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                // Modifying the RGB values and create color
                extractBit(red);
                extractBit(green);
                extractBit(blue);
            }
        }
        return extractMessage;
    }

    public static void extractBit(int component) {
        if (BIT_SKIP == 0) {
            BIT_SKIP = BITS_SPACE;
            System.out.println("component: "+ component);
            int bitToExtract = component & 1;
            extractMessage.append(bitToExtract);
        } else {
            BIT_SKIP--;
        }
    }
}

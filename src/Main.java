import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static java.lang.System.exit;
import static javax.swing.UIManager.get;
import static javax.swing.UIManager.getColor;

public class Main {
    //AVG_BIT_MESSAGE = avg num of words in Email * avg num of char for word * num of bits for each char
    // = 100 * 5 * 8
    public static int AVG_BIT_MESSAGE = 4000;
    public static int BITS_SPACE;
    public static int BIT_SKIP = 0;
    public static ArrayList<Integer> orderOfBits = new ArrayList<>();
    public static int orderIndex = 0;
    public static StringBuilder extractedBits = new StringBuilder();


    public static void main(String[] args) throws IOException {
        //choose image in CleanImages folder && Message to hide in it
        String cleanImage = "PlayGamesAd.png";
        //70 Words message
//        String message = "Amidst the bustling crowd, " +
//                "under the radiant sun, a lone sparrow " +
//                "sings a melody of hope, while the river " +
//                "whispers secrets to the ancient trees. " +
//                "Find solace in the embrace of nature's wisdom, " +
//                "where time's tapestry weaves stories of life's eternal dance.";

        // ~4000 bit message
//        String message = "Within the tranquil forest, " +
//                "under the sun's warm embrace, a solitary robin sings a tune of optimism, " +
//                "while the stream shares its secrets with the ancient oaks. Discover solace " +
//                "in the wisdom of nature, where the tapestry of time weaves tales of life's " +
//                "eternal dance. The moonlight gently illuminates a path among the stars. " +
//                "Night conceals dreams at the threshold of consciousness.  " +
//                "Unlock the universe's whispers; they hold the key " +
//                "to your soul's depths and the mysteries of existence.";
        String message = "hi my name is laith";

        StringBuilder messageToHide = stringToBinary(message);
        System.out.println(messageToHide.length());
        System.out.println("Hiding: '"+message+"' In: "+cleanImage);

        hideMessage(cleanImage, messageToHide);

        //**********************************************************************
        System.out.println("Extracting message from: Modified"+cleanImage);
        //choose Cover image from CoverImages to check for secret messages
        StringBuilder bitMessage = getMessage("ModifiedPlayGamesAd.png");

        System.out.println("Bits extracted: " + bitMessage);

        String ExtractedMessage = binaryToString(String.valueOf(bitMessage));
        System.out.println("Secret message was: " + ExtractedMessage);
    }

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
        if (messageToHide.length() > AVG_BIT_MESSAGE){
            System.out.println("Message to hide is too large for this algorithm!!!");
            exit(0);
        }
        // Reading the image
        File file = new File("C:\\ALL\\college\\year5 semmester 1\\COMP438 encryption\\Assignment 1\\AssignmentOne_LSB\\src\\CleanImages\\" +
                cleanImageName);
        BufferedImage img = ImageIO.read(file);

        //Calculate the distance between each bit
        BITS_SPACE = ((img.getHeight() * img.getWidth() * 3) / AVG_BIT_MESSAGE)-1;
        BIT_SKIP = BITS_SPACE;

        //Order of the bits to distribute
        orderOfBits = shuffleOrder(AVG_BIT_MESSAGE, BITS_SPACE);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (orderIndex < messageToHide.length()) {
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
        System.out.println("Finished hiding message!!!");
    }

    // laith code
    public static int modifyBit(int component, StringBuilder messageToHide) {
        //check to embed the bit or not
        if (BIT_SKIP == 0) {
            BIT_SKIP = BITS_SPACE;
            int messageIndex = orderOfBits.get(orderIndex);

            if (messageIndex > messageToHide.length()){
                component = (component & ~1);
            }else {
                int bitToEmbed = messageToHide.charAt(messageIndex) - '0';
//            System.out.println("Bit to embed: " + bitToEmbed);

//            System.out.println("Before: "+ component);
                component = (component & ~1) | bitToEmbed;
//            System.out.println("After: "+ component);
            }
            orderIndex++;
        } else {
            BIT_SKIP--;
        }
        return component;
    }

//****************************************************************************************************************

    public static StringBuilder getMessage(String CoverImageName) throws IOException {
        BIT_SKIP = BITS_SPACE;
        //Order of the bits to distribute
        orderOfBits = shuffleOrder(AVG_BIT_MESSAGE, BITS_SPACE);

        // Reading the image
        File file = new File("C:\\ALL\\college\\year5 semmester 1\\COMP438 encryption\\Assignment 1\\AssignmentOne_LSB\\src\\CoverImages\\" +
                CoverImageName);
        BufferedImage img = ImageIO.read(file);
        extractedBits.setLength(0);

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
        StringBuilder extractMessage = reOrganize();
        return extractMessage;
    }

    public static void extractBit(int component) {
        if (BIT_SKIP == 0) {
            BIT_SKIP = BITS_SPACE;
//            System.out.println("component: "+ component);
            //get LSB from component
            int bitToExtract = component & 1;
            extractedBits.append(bitToExtract);
        } else {
            BIT_SKIP--;
        }
    }

    public static StringBuilder reOrganize(){
        StringBuilder ordered = new StringBuilder();
        for (int i = 0; i < orderOfBits.size(); i++){
            ordered.append(extractedBits.charAt(orderOfBits.get(i)));
        }
        return ordered;
    }

    //*****************************************************************************
    public static ArrayList<Integer> shuffleOrder(int numOfBitsAvailable, long seed) {
        ArrayList<Integer> order = new ArrayList<>();
        for (int i = 0; i <= numOfBitsAvailable; i++) {
            order.add(i);
        }

        // Create a random object with the seed
        Random random = new Random(seed);

        // Shuffle the numbers based on random with seed
        Collections.shuffle(order, random);

        return order;
    }

}

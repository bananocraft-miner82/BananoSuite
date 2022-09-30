package miner82.bananosuite.io;

import miner82.bananosuite.classes.MonKeyType;
import miner82.bananosuite.classes.MonkeyMap;
import miner82.bananosuite.configuration.ConfigEngine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MonKeyGenerator {

    public static boolean generateMonKeyImage(ConfigEngine configEngine, MonkeyMap map) {

        if (map == null
                || configEngine == null) {

            return false;

        }

        try {

            File outputFile = new File(map.getFullFilePath());

            if (outputFile.exists()) {

                return false;

            }

            if (map.getMapType() == MonKeyType.MonKey) {

                return generateMonKey(configEngine, map, outputFile);

            } else {

                return generateQRCode(configEngine, map, outputFile);
            }

        } catch (Exception ex) {

            System.out.println("Error generating " + map.getMapType().name() +  ": " + map.getWalletAddress() + System.lineSeparator() + ex.toString());
            ex.printStackTrace();

        }

        return false;

    }

    private static boolean generateMonKey(ConfigEngine configEngine, MonkeyMap map, File outputFile) {


        System.out.println("Attempting MonKey download...");

        try {

            FileManager.downloadFile(configEngine.getMonKeyImageSourceURL(map.getWalletAddress()), map.getFullFilePath());

            BufferedImage image = ImageManager.resizeImage(ImageIO.read(outputFile), 128, 128);

            if (map.hasFrame()
                    && configEngine.getAvailableFrames().containsKey(map.getFrame())) {

                try {
                    String frameImageFileName = configEngine.getAvailableFrames().getOrDefault(map.getFrame(), "");

                    if (frameImageFileName != "") {

                        File frameFile = new File(configEngine.getMonKeyFrameDirectoryPath() + frameImageFileName);
                        BufferedImage frame = ImageIO.read(frameFile);

                        Graphics g = image.getGraphics();
                        g.drawImage(frame, 0, 0, null);

                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            ImageIO.write(image, "png", outputFile);

            return image != null;

        } catch (Exception ex) {

            ex.printStackTrace();

        }

        return false;

    }

    private static boolean generateQRCode(ConfigEngine configEngine, MonkeyMap map, File outputFile) {

        System.out.println("Attempting QR Code generation...");

        try {

            BufferedImage image = ImageManager.generateQRCodeImage(map.getWalletAddress());

            Graphics2D gO = image.createGraphics();
            gO.setColor(Color.BLUE);
            gO.setFont(new Font("SansSerif", Font.BOLD, 12));

            if (map.hasAdditionalText()) {

                gO.drawString(map.getAdditionalText().trim(), 5, 126);

            }

            ImageIO.write(image, "png", outputFile);

            return image != null;

        } catch (Exception ex) {

        }

        return false;

    }

}
package graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class SpriteLoader {

    private SpriteLoader() { }

    public static SpriteSet loadGooseSprites() {
        try {
            BufferedImage sheet = loadSpriteSheet("/entities/goose/goose_walk_spritesheet.png");

            BufferedImage[] down = new BufferedImage[3];
            BufferedImage[] left = new BufferedImage[3];
            BufferedImage[] right = new BufferedImage[3];
            BufferedImage[] up = new BufferedImage[3];

            int frameWidth = 16;
            int frameHeight = 16;

            down[0] = sheet.getSubimage(0, 0, frameWidth, frameHeight);
            down[1] = sheet.getSubimage(frameWidth, 0, frameWidth, frameHeight);
            down[2] = sheet.getSubimage(frameWidth * 2, 0, frameWidth, frameHeight);

            left[0] = sheet.getSubimage(0, frameHeight, frameWidth, frameHeight);
            left[1] = sheet.getSubimage(frameWidth, frameHeight, frameWidth, frameHeight);
            left[2] = sheet.getSubimage(frameWidth * 2, frameHeight, frameWidth, frameHeight);

            right[0] = sheet.getSubimage(0, frameHeight * 2, frameWidth, frameHeight);
            right[1] = sheet.getSubimage(frameWidth, frameHeight * 2, frameWidth, frameHeight);
            right[2] = sheet.getSubimage(frameWidth * 2, frameHeight * 2, frameWidth, frameHeight);

            up[0] = sheet.getSubimage(0, frameHeight * 3, frameWidth, frameHeight);
            up[1] = sheet.getSubimage(frameWidth, frameHeight * 3, frameWidth, frameHeight);
            up[2] = sheet.getSubimage(frameWidth * 2, frameHeight * 3, frameWidth, frameHeight);

            return new SpriteSet(up, right, down, left);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load goose sprites", e);
        }
    }
    
    private static BufferedImage loadSpriteSheet(String path) {
        try {
            File file = new File("src/assets" + path);
            System.out.println("Loading sprite sheet from: " + file.getAbsolutePath());
            System.out.println("Exists: " + file.exists());
            return ImageIO.read(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sprite sheet: " + path, e);
        }
    }
}
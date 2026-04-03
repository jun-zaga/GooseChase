package graphics;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

public final class SpriteLoader {

    private SpriteLoader() { }

    public static SpriteSet loadGooseSprites() {
        return loadSpriteSheet("/assets/entities/goose/goose_walk_spritesheet.png", 16, 16);
    }

    public static SpriteSet loadSpriteSheet(String path, int frameWidth, int frameHeight) {
        try (InputStream is = SpriteLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Sprite sheet not found: " + path);
            }

            BufferedImage sheet = ImageIO.read(is);

            BufferedImage[] down = new BufferedImage[3];
            BufferedImage[] left = new BufferedImage[3];
            BufferedImage[] right = new BufferedImage[3];
            BufferedImage[] up = new BufferedImage[3];

            for (int i = 0; i < 3; i++) {
                down[i] = sheet.getSubimage(i * frameWidth, 0 * frameHeight, frameWidth, frameHeight);
                left[i] = sheet.getSubimage(i * frameWidth, 1 * frameHeight, frameWidth, frameHeight);
                right[i] = sheet.getSubimage(i * frameWidth, 2 * frameHeight, frameWidth, frameHeight);
                up[i] = sheet.getSubimage(i * frameWidth, 3 * frameHeight, frameWidth, frameHeight);
            }

            return new SpriteSet(up, right, down, left);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sprite sheet: " + path, e);
        }
    }
}
package graphics;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public final class SpriteLoader {

    private SpriteLoader() { }

    public static SpriteSet loadGooseSprites() {
        try {
            BufferedImage[] up = new BufferedImage[] {
                    ImageIO.read(SpriteLoader.class.getResourceAsStream("/entities/goose/goose_walk_up_1.png")),
                    ImageIO.read(SpriteLoader.class.getResourceAsStream("/entities/goose/goose_walk_up_2.png"))
            };

            BufferedImage[] right = new BufferedImage[] {
                    ImageIO.read(SpriteLoader.class.getResourceAsStream("/entities/goose/goose_walk_right_1.png")),
                    ImageIO.read(SpriteLoader.class.getResourceAsStream("/entities/goose/goose_walk_right_2.png"))
            };

            BufferedImage[] down = new BufferedImage[] {
                    ImageIO.read(SpriteLoader.class.getResourceAsStream("/entities/goose/goose_walk_down_1.png")),
                    ImageIO.read(SpriteLoader.class.getResourceAsStream("/entities/goose/goose_walk_down_2.png"))
            };

            BufferedImage[] left = new BufferedImage[] {
                    ImageIO.read(SpriteLoader.class.getResourceAsStream("/entities/goose/goose_walk_left_1.png")),
                    ImageIO.read(SpriteLoader.class.getResourceAsStream("/entities/goose/goose_walk_left_2.png"))
            };

            return new SpriteSet(up, right, down, left);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load goose sprites.", e);
        }
    }
}
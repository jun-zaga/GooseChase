package world;

import java.awt.image.BufferedImage;

public class Tile {

    private final BufferedImage image;
    private final boolean solid;

    public Tile(BufferedImage image, boolean solid) {
        this.image = image;
        this.solid = solid;
    }

    public BufferedImage getImage() {
        return image;
    }

    public boolean isSolid() {
        return solid;
    }
}
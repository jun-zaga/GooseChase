package graphics;

import java.awt.image.BufferedImage;

public class SpriteSet {

    private final BufferedImage[] up;
    private final BufferedImage[] right;
    private final BufferedImage[] down;
    private final BufferedImage[] left;

    public SpriteSet(BufferedImage[] up, BufferedImage[] right, BufferedImage[] down, BufferedImage[] left) {
        this.up = up;
        this.right = right;
        this.down = down;
        this.left = left;
    }

    public BufferedImage[] getUp() {
        return up;
    }

    public BufferedImage[] getRight() {
        return right;
    }

    public BufferedImage[] getDown() {
        return down;
    }

    public BufferedImage[] getLeft() {
        return left;
    }

    public BufferedImage getFrame(String direction, int frame) {
        return switch (direction.toLowerCase()) {
            case "up" -> up[frame];
            case "right" -> right[frame];
            case "down" -> down[frame];
            case "left" -> left[frame];
            default -> down[1];
        };
    }
}
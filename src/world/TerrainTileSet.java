package world;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import util.GameConstants;

public class TerrainTileSet {

    private final BufferedImage[] tiles;
    private final int tileSize;

    public TerrainTileSet(String sheetPath) {
        this.tileSize = GameConstants.TILE_SIZE;
        this.tiles = loadSheet(sheetPath);
    }

    private BufferedImage[] loadSheet(String sheetPath) {
        try {
            BufferedImage sheet = ImageIO.read(new File("src/assets/tiles/" + sheetPath));
            BufferedImage[] result = new BufferedImage[18];

            for (int i = 0; i < 18; i++) {
                BufferedImage raw = sheet.getSubimage(i * 16, 0, 16, 16);
                result[i] = scaleTile(raw, tileSize);
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load tile sheet: " + sheetPath, e);
        }
    }

    private BufferedImage scaleTile(BufferedImage original, int size) {
        BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);

        g2.drawImage(original, 0, 0, size, size, null);
        g2.dispose();

        return scaled;
    }

    public BufferedImage get(int index) {
        return tiles[index];
    }
}
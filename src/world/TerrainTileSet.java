package world;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import util.GameConstants;

public class TerrainTileSet {

    private static final int SOURCE_TILE_SIZE = 16;

    private final BufferedImage[] tiles;
    private final int tileSize;

    public TerrainTileSet(String fileName) {
        this.tileSize = GameConstants.TILE_SIZE;
        this.tiles = loadSheet(fileName);
    }

    private BufferedImage[] loadSheet(String fileName) {
        try {
            File file = new File("src/assets/tiles/" + fileName);
            BufferedImage sheet = ImageIO.read(file);

            if (sheet == null) {
                throw new RuntimeException("ImageIO.read returned null for: " + file.getAbsolutePath());
            }

            if (sheet.getWidth() % SOURCE_TILE_SIZE != 0 || sheet.getHeight() % SOURCE_TILE_SIZE != 0) {
                throw new RuntimeException(
                    "Tile sheet dimensions must be divisible by " + SOURCE_TILE_SIZE +
                    ". Got " + sheet.getWidth() + "x" + sheet.getHeight()
                );
            }

            int cols = sheet.getWidth() / SOURCE_TILE_SIZE;
            int rows = sheet.getHeight() / SOURCE_TILE_SIZE;
            int totalTiles = cols * rows;

            BufferedImage[] result = new BufferedImage[totalTiles];

            for (int i = 0; i < totalTiles; i++) {
                int col = i % cols;
                int row = i / cols;

                BufferedImage raw = sheet.getSubimage(
                    col * SOURCE_TILE_SIZE,
                    row * SOURCE_TILE_SIZE,
                    SOURCE_TILE_SIZE,
                    SOURCE_TILE_SIZE
                );

                result[i] = scaleTile(raw, tileSize);
            }

            System.out.println(
                "[TileSet] width=" + sheet.getWidth() +
                ", height=" + sheet.getHeight() +
                ", cols=" + cols +
                ", rows=" + rows +
                ", total=" + totalTiles
            );

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load tile sheet: " + fileName, e);
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
        if (index < 0 || index >= tiles.length) {
            throw new IllegalArgumentException(
                "Tile index out of range: " + index + ", loaded tiles: " + tiles.length
            );
        }
        return tiles[index];
    }

    public int size() {
        return tiles.length;
    }
}
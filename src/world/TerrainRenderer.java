package world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TerrainRenderer {

    private final TerrainManager terrainManager = new TerrainManager();

    public void drawTile(Graphics2D g2, TerrainType[][] terrainMap, int row, int col, int x, int y, int tileSize) {
        drawBase(g2, x, y, tileSize);

        for (TerrainType type : TerrainType.values()) {
            if (type == TerrainType.GRASS) continue;
            if (!type.isEnabled()) continue;

            int overlayIndex = TerrainAutoTile.getOverlayIndex(terrainMap, row, col, type);
            if (overlayIndex == TerrainAutoTile.NONE) continue;

            BufferedImage img = terrainManager.getSet(type).get(overlayIndex);
            g2.drawImage(img, x, y, null);
        }
    }

    private void drawBase(Graphics2D g2, int x, int y, int tileSize) {
        BufferedImage grass = terrainManager.getSet(TerrainType.GRASS).get(TerrainAutoTile.FULL);
        g2.drawImage(grass, x, y, null);
    }
}
package world;

import graphics.Camera;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import util.GameConstants;

public class TileMap {

    private final TerrainType[][] terrainMap;
    private final BufferedImage[][] bakedMap;
    private final int tileSize;

    private final TerrainManager terrainManager;

    public TileMap() {
        this.tileSize = GameConstants.TILE_SIZE;
        this.terrainManager = new TerrainManager();

        this.terrainMap = loadMapFromCSV("/maps/Map_02.csv");
        this.bakedMap = new BufferedImage[getRows()][getCols()];

        bakeEntireMap();
    }

    private void bakeEntireMap() {
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                bakedMap[row][col] = buildTileImage(row, col);
            }
        }
    }

    public boolean collidesWithSolidTile(double x, double y, int width, int height) {
        int startCol = (int) (x / tileSize);
        int endCol   = (int) ((x + width - 1) / tileSize);
        int startRow = (int) (y / tileSize);
        int endRow   = (int) ((y + height - 1) / tileSize);

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (isSolidTileAtWorldPosition(col * tileSize, row * tileSize)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSolidTile(int row, int col) {
        if (isOutOfBounds(row, col)) return true;

        TerrainType terrain = terrainMap[row][col];

        return terrain == TerrainType.WATER;
    }

    private BufferedImage buildTileImage(int row, int col) {
        BufferedImage result = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();

        TerrainType current = terrainMap[row][col];

        // Always start with water as the lowest base layer
        g2.drawImage(
                terrainManager.getSet(TerrainType.WATER).get(TerrainAutoTile.FULL),
                0,
                0,
                null
        );

        // Draw all higher terrains in enum order
        for (TerrainType type : TerrainType.values()) {
            if (type == TerrainType.WATER) continue;
            if (!type.isEnabled()) continue;

            int overlayIndex = TerrainAutoTile.getOverlayIndex(terrainMap, row, col, type);
            if (overlayIndex == TerrainAutoTile.NONE) continue;

            g2.drawImage(
                    terrainManager.getSet(type).get(overlayIndex),
                    0,
                    0,
                    null
            );
        }

        // Patch correction:
        // if a single-tile patch is fully surrounded by a higher-priority terrain,
        // redraw the surrounding full tile first, then the patch tile on top.
        for (TerrainType centerType : TerrainType.values()) {
            if (centerType == TerrainType.WATER) continue;
            if (current != centerType) continue;

            for (TerrainType surroundingType : TerrainType.values()) {
                if (surroundingType.getPriority() <= centerType.getPriority()) continue;
                if (!surroundingType.isEnabled()) continue;

                if (TerrainAutoTile.isPatchInsideHigherTerrain(
                        terrainMap, row, col, centerType, surroundingType)) {

                    g2.drawImage(
                            terrainManager.getSet(surroundingType).get(TerrainAutoTile.FULL),
                            0,
                            0,
                            null
                    );

                    g2.drawImage(
                            terrainManager.getSet(centerType).get(TerrainAutoTile.PATCH),
                            0,
                            0,
                            null
                    );
                }
            }
        }

        g2.dispose();
        return result;
    }

    public void draw(Graphics2D g2, Camera camera) {
        int camX = camera.getX();
        int camY = camera.getY();

        int startCol = Math.max(0, camX / tileSize);
        int endCol = Math.min(getCols(), (camX + GameConstants.SCREEN_WIDTH) / tileSize + 2);
        int startRow = Math.max(0, camY / tileSize);
        int endRow = Math.min(getRows(), (camY + GameConstants.SCREEN_HEIGHT) / tileSize + 2);

        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                int screenX = (col * tileSize) - camX;
                int screenY = (row * tileSize) - camY;

                g2.drawImage(bakedMap[row][col], screenX, screenY, null);
            }
        }
    }

    public void setTerrainAt(int row, int col, TerrainType terrain) {
        if (isOutOfBounds(row, col)) return;

        terrainMap[row][col] = terrain;
        rebakeAround(row, col);
    }

    public void rebakeAround(int row, int col) {
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (!isOutOfBounds(r, c)) {
                    bakedMap[r][c] = buildTileImage(r, c);
                }
            }
        }
    }

    public boolean isSolidTileAtWorldPosition(double worldX, double worldY) {
        int col = (int) (worldX / tileSize);
        int row = (int) (worldY / tileSize);

        return isSolidTile(row, col);
    }

    private boolean isOutOfBounds(int row, int col) {
        return row < 0 || row >= getRows() || col < 0 || col >= getCols();
    }

    public int getRows() {
        return terrainMap.length;
    }

    public int getCols() {
        return terrainMap[0].length;
    }

    private TerrainType[][] loadMapFromCSV(String path) {
        int rows = GameConstants.WORLD_ROWS;
        int cols = GameConstants.WORLD_COLS;
        TerrainType[][] map = new TerrainType[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                map[r][c] = TerrainType.WATER;
            }
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("src/assets" + path)))) {

            String line;
            int row = 0;
            while ((line = br.readLine()) != null && row < rows) {
                if (line.isBlank()) continue;

                String[] values = line.split(",");
                for (int col = 0; col < values.length && col < cols; col++) {
                    int rawValue = Integer.parseInt(values[col].trim());
                    map[row][col] = TerrainType.fromMapValue(rawValue);
                }
                row++;
            }
        } catch (Exception e) {
            System.err.println("Error loading map: " + e.getMessage());
        }

        return map;
    }
}
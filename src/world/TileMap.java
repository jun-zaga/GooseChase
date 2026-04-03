package world;

import graphics.Camera;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import util.GameConstants;

public class TileMap {

    public static final int DIRT = 0;
    public static final int GRASS = 1;

    private final int[][] terrainMap;
    private final BufferedImage[][] bakedMap;
    private final int tileSize;

    private final TerrainTileSet grassSet;
    private final TerrainTileSet dirtSet;

    public TileMap() {
        this.tileSize = GameConstants.TILE_SIZE;
        this.grassSet = new TerrainTileSet("grass_tiles.png");
        this.dirtSet = new TerrainTileSet("dirt_tiles.png");
        this.terrainMap = loadMapFromCSV("/maps/Map_03.csv");
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

    private BufferedImage buildTileImage(int row, int col) {
        BufferedImage result = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();

        // Base dirt everywhere
        g2.drawImage(dirtSet.get(TerrainAutoTile.FULL), 0, 0, null);

        // Grass overlay on top
        int overlay = TerrainAutoTile.getGrassOverlayIndex(terrainMap, row, col, GRASS);
        if (overlay != TerrainAutoTile.NONE) {
            g2.drawImage(grassSet.get(overlay), 0, 0, null);
        }

        g2.dispose();
        return result;
    }

    public void rebakeTile(int row, int col) {
        if (row < 0 || row >= getRows() || col < 0 || col >= getCols()) {
            return;
        }

        bakedMap[row][col] = buildTileImage(row, col);
    }

    public void rebakeAround(int row, int col) {
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r >= 0 && r < getRows() && c >= 0 && c < getCols()) {
                    bakedMap[r][c] = buildTileImage(r, c);
                }
            }
        }
    }

    private int[][] loadMapFromCSV(String path) {
        int rows = GameConstants.WORLD_ROWS;
        int cols = GameConstants.WORLD_COLS;
        int[][] map = new int[rows][cols];

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("src/assets" + path)))) {

            String line;
            int row = 0;

            while ((line = br.readLine()) != null && row < rows) {
                if (line.isBlank()) {
                    row++;
                    continue;
                }

                String[] values = line.split(",");

                for (int col = 0; col < values.length && col < cols; col++) {
                    map[row][col] = Integer.parseInt(values[col].trim());
                }

                row++;
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load map CSV: " + path, e);
        }

        return map;
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

    public boolean isSolidTileAtWorldPosition(double worldX, double worldY) {
        int col = (int) (worldX / tileSize);
        int row = (int) (worldY / tileSize);

        if (row < 0 || row >= getRows() || col < 0 || col >= getCols()) {
            return true;
        }

        return false;
    }

    public boolean collidesWithSolidTile(double x, double y, int width, int height) {
        return isSolidTileAtWorldPosition(x, y)
                || isSolidTileAtWorldPosition(x + width - 1, y)
                || isSolidTileAtWorldPosition(x, y + height - 1)
                || isSolidTileAtWorldPosition(x + width - 1, y + height - 1);
    }

    public int getRows() {
        return terrainMap.length;
    }

    public int getCols() {
        return terrainMap[0].length;
    }

    public int getTerrainAt(int row, int col) {
        if (row < 0 || row >= getRows() || col < 0 || col >= getCols()) {
            return -1;
        }
        return terrainMap[row][col];
    }

    public void setTerrainAt(int row, int col, int terrain) {
        if (row < 0 || row >= getRows() || col < 0 || col >= getCols()) {
            return;
        }

        terrainMap[row][col] = terrain;
        rebakeAround(row, col);
    }
}
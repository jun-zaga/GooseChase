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

    private final TerrainTileSet dirtSet;
    private final TerrainTileSet grassSet;

    public TileMap() {
        this.tileSize = GameConstants.TILE_SIZE;
        this.dirtSet = new TerrainTileSet("dirt_tiles.png");
        this.grassSet = new TerrainTileSet("grass_tiles.png");
        
        // Load the raw data
        this.terrainMap = loadMapFromCSV("/maps/Map_03.csv");
        
        // Initialize the cache
        this.bakedMap = new BufferedImage[getRows()][getCols()];

        // Generate the visuals once
        bakeEntireMap();
    }

    /**
     * Loops through every coordinate and generates a static image 
     * based on the neighbors.
     */
    private void bakeEntireMap() {
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                bakedMap[row][col] = buildTileImage(row, col);
            }
        }
    }

        /**
     * Checks if a rectangular area in the world (defined by pixels) 
     * overlaps with any tile marked as solid (e.g., WATER).
     */
    public boolean collidesWithSolidTile(double x, double y, int width, int height) {
        // Determine the range of tiles the entity is currently overlapping
        int startCol = (int) (x / tileSize);
        int endCol   = (int) ((x + width - 1) / tileSize);
        int startRow = (int) (y / tileSize);
        int endRow   = (int) ((y + height - 1) / tileSize);

        // Loop through every tile the entity's bounding box touches
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                // If any of these tiles are solid, we have a collision
                if (isSolidTileAtWorldPosition(col * tileSize, row * tileSize)) {
                    return true;
                }
            }
        }
        return false;
    }

    private BufferedImage buildTileImage(int row, int col) {
        BufferedImage result = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();

        TerrainType current = terrainMap[row][col];

        // Base dirt everywhere
        g2.drawImage(dirtSet.get(TerrainAutoTile.FULL), 0, 0, null);

        // Grass overlay
        int grassIndex = TerrainAutoTile.getOverlayIndex(terrainMap, row, col, TerrainType.GRASS);
        if (grassIndex != TerrainAutoTile.NONE) {
            g2.drawImage(grassSet.get(grassIndex), 0, 0, null);
        }

        // Dirt patch fully surrounded by grass:
        // draw grass full underneath, then dirt patch on top
        if (current == TerrainType.DIRT &&
            TerrainAutoTile.isPatchInsideHigherTerrain(
                terrainMap, row, col, TerrainType.DIRT, TerrainType.GRASS)) {

            g2.drawImage(grassSet.get(TerrainAutoTile.FULL), 0, 0, null);
            g2.drawImage(dirtSet.get(TerrainAutoTile.PATCH), 0, 0, null);
        }

        g2.dispose();
        return result;
    }

    public void draw(Graphics2D g2, Camera camera) {
        int camX = camera.getX();
        int camY = camera.getY();

        // Calculate visible range (Culling)
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

    // --- UTILITIES & SETTERS ---

    public void setTerrainAt(int row, int col, TerrainType terrain) {
        if (isOutOfBounds(row, col)) return;

        terrainMap[row][col] = terrain;
        // When one tile changes, the 8 tiles around it must update their borders!
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

        if (isOutOfBounds(row, col)) return true;

        return terrainMap[row][col] == TerrainType.WATER;
    }

    private boolean isOutOfBounds(int row, int col) {
        return row < 0 || row >= getRows() || col < 0 || col >= getCols();
    }

    public int getRows() { return terrainMap.length; }
    public int getCols() { return terrainMap[0].length; }

    // --- LOADING LOGIC ---

    private TerrainType[][] loadMapFromCSV(String path) {
        int rows = GameConstants.WORLD_ROWS;
        int cols = GameConstants.WORLD_COLS;
        TerrainType[][] map = new TerrainType[rows][cols];

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
            // Fallback to empty dirt map
            for(int r=0; r<rows; r++) 
                for(int c=0; c<cols; c++) map[r][c] = TerrainType.DIRT;
        }
        return map;
    }
}
package world;

import graphics.Camera;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import util.GameConstants;

public class TileMap {

    private final int[][] mapData;
    private final Tile[] tiles;
    private final int tileSize;

    public TileMap() {
        this.tileSize = GameConstants.TILE_SIZE;
        this.tiles = loadTiles();
        this.mapData = loadMapFromCSV("/maps/Map_01.csv");
    }

    private Tile[] loadTiles() {
        Tile[] loadedTiles = new Tile[25];

        // fallback so missing ids do not crash
        for (int i = 0; i < loadedTiles.length; i++) {
            loadedTiles[i] = new Tile(loadImage("/tiles/grass.png"), false);
        }

        loadedTiles[0] = new Tile(loadImage("/tiles/dirt.png"), false);
        loadedTiles[1] = new Tile(loadImage("/tiles/dirt_grass_outer_corner_bl.png"), false);
        loadedTiles[2] = new Tile(loadImage("/tiles/dirt_grass_outer_corner_br.png"), false);
        loadedTiles[3] = new Tile(loadImage("/tiles/dirt_grass_outer_corner_tl.png"), false);
        loadedTiles[4] = new Tile(loadImage("/tiles/dirt_grass_outer_corner_tr.png"), false);
        loadedTiles[5] = new Tile(loadImage("/tiles/dirt_grass_edge_bottom.png"), false);
        loadedTiles[6] = new Tile(loadImage("/tiles/dirt_grass_edge_left.png"), false);
        loadedTiles[7] = new Tile(loadImage("/tiles/dirt_grass_edge_right.png"), false);
        loadedTiles[8] = new Tile(loadImage("/tiles/dirt_grass_edge_top.png"), false);
        loadedTiles[9] = new Tile(loadImage("/tiles/grass.png"), false);

        // 10 still unused unless you made one
        // loadedTiles[10] = ...

        loadedTiles[11] = new Tile(loadImage("/tiles/wood.png"), true);

        loadedTiles[12] = new Tile(loadImage("/tiles/grass_dirt_inner_corner_br.png"), false);
        loadedTiles[13] = new Tile(loadImage("/tiles/grass_dirt_inner_corner_bl.png"), false);
        loadedTiles[14] = new Tile(loadImage("/tiles/grass_dirt_inner_corner_tr.png"), false);
        loadedTiles[15] = new Tile(loadImage("/tiles/grass_dirt_inner_corner_tl.png"), false);
        loadedTiles[16] = new Tile(loadImage("/tiles/grass_dirt_cap_left.png"), false);
        loadedTiles[17] = new Tile(loadImage("/tiles/grass_dirt_cap_right.png"), false);
        loadedTiles[18] = new Tile(loadImage("/tiles/grass_dirt_cap_top.png"), false);
        loadedTiles[19] = new Tile(loadImage("/tiles/grass_dirt_cap_bottom.png"), false);
        loadedTiles[20] = new Tile(loadImage("/tiles/grass_dirt_patch.png"), false);
        return loadedTiles;
    }

    private BufferedImage loadImage(String path) {
        try {
            String fullPath = "src/assets" + path;
            return ImageIO.read(new java.io.File(fullPath));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load tile image: " + path, e);
        }
    }

    private int[][] loadMapFromCSV(String path) {
        int rows = GameConstants.WORLD_ROWS;
        int cols = GameConstants.WORLD_COLS;
        int[][] map = new int[rows][cols];

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new java.io.FileInputStream("src/assets" + path)
            ));

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

            br.close();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load map CSV: " + path, e);
        }

        return map;
    }

    public void draw(Graphics2D g2, Camera camera) {

        int startCol = Math.max(0, camera.getX() / tileSize);
        int endCol = Math.min(getCols(), (camera.getX() + GameConstants.SCREEN_WIDTH) / tileSize + 2);

        int startRow = Math.max(0, camera.getY() / tileSize);
        int endRow = Math.min(getRows(), (camera.getY() + GameConstants.SCREEN_HEIGHT) / tileSize + 2);

        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                int tileId = mapData[row][col];

                if (tileId < 0 || tileId >= tiles.length) {
                    tileId = 9;
                }

                Tile tile = tiles[tileId];

                int worldX = col * tileSize;
                int worldY = row * tileSize;
                int screenX = camera.worldToScreenX(worldX);
                int screenY = camera.worldToScreenY(worldY);

                g2.drawImage(tile.getImage(), screenX, screenY, tileSize, tileSize, null);
            }
        }
    }

    public boolean isSolidTileAtWorldPosition(double worldX, double worldY) {
        int col = (int) (worldX / tileSize);
        int row = (int) (worldY / tileSize);

        if (row < 0 || row >= getRows() || col < 0 || col >= getCols()) {
            return true;
        }

        int tileId = mapData[row][col];

        if (tileId < 0 || tileId >= tiles.length) {
            return true;
        }

        return tiles[tileId].isSolid();
    }

    public boolean collidesWithSolidTile(double x, double y, int width, int height) {
        return isSolidTileAtWorldPosition(x, y)
                || isSolidTileAtWorldPosition(x + width - 1, y)
                || isSolidTileAtWorldPosition(x, y + height - 1)
                || isSolidTileAtWorldPosition(x + width - 1, y + height - 1);
    }

    public int getRows() {
        return mapData.length;
    }

    public int getCols() {
        return mapData[0].length;
    }
}
package world;

import graphics.Camera;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;
import util.GameConstants;

public class TileMap {

    private final TerrainType[][] terrainMap;
    private final BufferedImage[][] bakedMap;
    private final int tileSize;

    private final TerrainManager terrainManager;

    public TileMap() {
        this.tileSize = GameConstants.TILE_SIZE;
        this.terrainManager = new TerrainManager();

        // this.terrainMap = loadMapFromCSV("/maps/Map_02.csv");
        this.terrainMap = createBiomeMap(200, 200);
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

        g2.drawImage(
                terrainManager.getSet(TerrainType.WATER).get(TerrainAutoTile.FULL),
                0,
                0,
                null
        );

        for (TerrainType type : TerrainType.values()) {
            if (type == TerrainType.WATER) continue;
            if (!type.isEnabled()) continue;

            int overlayIndex = TerrainAutoTile.getOverlayIndex(terrainMap, row, col, type);
            if (overlayIndex == TerrainAutoTile.NONE) continue;

            BufferedImage tileImage;
            if (type == TerrainType.GRASS) {
                tileImage = getGrassTileByOverlayIndex(overlayIndex, row, col);
            } else {
                tileImage = terrainManager.getSet(type).get(overlayIndex);
            }

            g2.drawImage(tileImage, 0, 0, null);
        }

        for (TerrainType centerType : TerrainType.values()) {
            if (centerType == TerrainType.WATER) continue;
            if (current != centerType) continue;

            for (TerrainType surroundingType : TerrainType.values()) {
                if (surroundingType.getPriority() <= centerType.getPriority()) continue;
                if (!surroundingType.isEnabled()) continue;

                if (TerrainAutoTile.isPatchInsideHigherTerrain(
                        terrainMap, row, col, centerType, surroundingType)) {

                    BufferedImage baseImage;
                    if (surroundingType == TerrainType.GRASS) {
                        baseImage = getGrassFullTile(row, col);
                    } else {
                        baseImage = terrainManager.getSet(surroundingType).get(TerrainAutoTile.FULL);
                    }

                    g2.drawImage(baseImage, 0, 0, null);
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

    private TerrainType[][] createBiomeMap(int rows, int cols) {
    TerrainType[][] map = new TerrainType[rows][cols];
    Random random = new Random(42);

    // 1) start with grass everywhere
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            map[r][c] = TerrainType.GRASS;
        }
    }

    // =========================================================
    // 2) winding river from upper-left-ish to lower-right-ish
    // =========================================================
    double riverX = cols * 0.22;
    double riverDrift = 0.0;

    for (int r = 0; r < rows; r++) {
        riverDrift += (random.nextDouble() - 0.5) * 1.4;
        riverDrift *= 0.92;

        riverX += riverDrift;
        riverX += Math.sin(r * 0.08) * 0.35;

        if (riverX < 20) riverX = 20;
        if (riverX > cols - 20) riverX = cols - 20;

        int centerX = (int) Math.round(riverX);

        int riverHalfWidth = 3 + (int) (2 * Math.sin(r * 0.06));
        if (riverHalfWidth < 2) riverHalfWidth = 2;

        for (int c = centerX - riverHalfWidth; c <= centerX + riverHalfWidth; c++) {
            if (inBounds(r, c, rows, cols)) {
                map[r][c] = TerrainType.WATER;
            }
        }

        // little width variation blobs
        if (r % 9 == 0) {
            for (int rr = r - 1; rr <= r + 1; rr++) {
                for (int cc = centerX - riverHalfWidth - 1; cc <= centerX + riverHalfWidth + 1; cc++) {
                    if (inBounds(rr, cc, rows, cols) && random.nextDouble() < 0.35) {
                        map[rr][cc] = TerrainType.WATER;
                    }
                }
            }
        }
    }

    // =========================================================
    // 3) sandy banks near river
    // =========================================================
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (map[r][c] != TerrainType.GRASS) continue;

            int waterNeighbors = countNearby(map, r, c, TerrainType.WATER, 2);
            if (waterNeighbors >= 3) {
                map[r][c] = TerrainType.SAND;
            }
        }
    }

    // =========================================================
    // 4) muddy wet ground around water and sand
    // =========================================================
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (map[r][c] != TerrainType.GRASS) continue;

            int wetNeighbors = countNearby(map, r, c, TerrainType.WATER, 3)
                    + countNearby(map, r, c, TerrainType.SAND, 2);

            if (wetNeighbors >= 4 && random.nextDouble() < 0.55) {
                map[r][c] = TerrainType.MUD;
            }
        }
    }

    // =========================================================
    // 5) central plains dirt patches
    // =========================================================
    for (int i = 0; i < 18; i++) {
        int centerR = 25 + random.nextInt(rows - 50);
        int centerC = 20 + random.nextInt(cols - 40);
        int radius = 4 + random.nextInt(10);

        paintBlob(map, centerR, centerC, radius, TerrainType.DIRT, random, 0.82);
    }

    // =========================================================
    // 6) mountain region (upper-right)
    // stone base
    // =========================================================
    int mountainCenterR = 42;
    int mountainCenterC = cols - 42;

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            double dr = r - mountainCenterR;
            double dc = c - mountainCenterC;

            double dist = Math.sqrt((dr * dr) / 1.2 + (dc * dc) / 1.0);

            if (dist < 34) {
                if (random.nextDouble() < 0.92) {
                    if (map[r][c] == TerrainType.GRASS || map[r][c] == TerrainType.DIRT) {
                        map[r][c] = TerrainType.STONE;
                    }
                }
            }
        }
    }

    // =========================================================
    // 7) snowy mountain cap over inner stone area
    // =========================================================
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            double dr = r - mountainCenterR;
            double dc = c - mountainCenterC;

            double dist = Math.sqrt((dr * dr) / 1.15 + (dc * dc) / 0.95);

            if (dist < 18) {
                if (map[r][c] == TerrainType.STONE && random.nextDouble() < 0.95) {
                    map[r][c] = TerrainType.SNOW;
                }
            } else if (dist < 24) {
                if (map[r][c] == TerrainType.STONE && random.nextDouble() < 0.35) {
                    map[r][c] = TerrainType.SNOW;
                }
            }
        }
    }

    // =========================================================
    // 8) foothills / extra stone fingers trailing out
    // =========================================================
    for (int i = 0; i < 8; i++) {
        int startR = mountainCenterR + random.nextInt(20) - 10;
        int startC = mountainCenterC + random.nextInt(20) - 10;

        int len = 15 + random.nextInt(20);
        double angle = Math.PI * 0.55 + (random.nextDouble() * 1.2);

        for (int step = 0; step < len; step++) {
            int rr = (int) Math.round(startR + Math.sin(angle) * step);
            int cc = (int) Math.round(startC - Math.cos(angle) * step);

            paintBlob(map, rr, cc, 2 + random.nextInt(2), TerrainType.STONE, random, 0.75);
        }
    }

    // =========================================================
    // 9) some muddy marsh area in lower-left
    // =========================================================
    for (int i = 0; i < 10; i++) {
        int centerR = rows - 35 + random.nextInt(20);
        int centerC = 18 + random.nextInt(28);
        int radius = 4 + random.nextInt(7);

        paintBlob(map, centerR, centerC, radius, TerrainType.MUD, random, 0.78);
    }

    // =========================================================
    // 10) some sandy dry patches in lower-middle
    // =========================================================
    for (int i = 0; i < 7; i++) {
        int centerR = rows - 45 + random.nextInt(20);
        int centerC = cols / 2 - 20 + random.nextInt(40);
        int radius = 3 + random.nextInt(6);

        paintBlob(map, centerR, centerC, radius, TerrainType.SAND, random, 0.72);
    }

    // =========================================================
    // 11) cleanup pass:
    // remove lonely single tiles by blending them into neighbors
    // =========================================================
    for (int pass = 0; pass < 2; pass++) {
        TerrainType[][] next = copyMap(map);

        for (int r = 1; r < rows - 1; r++) {
            for (int c = 1; c < cols - 1; c++) {
                TerrainType current = map[r][c];
                int same = countCardinalSame(map, r, c, current);

                if (same == 0) {
                    next[r][c] = mostCommonNeighbor(map, r, c);
                }
            }
        }

        map = next;
    }

    return map;
}

private boolean inBounds(int row, int col, int rows, int cols) {
    return row >= 0 && row < rows && col >= 0 && col < cols;
}

private void paintBlob(
        TerrainType[][] map,
        int centerRow,
        int centerCol,
        int radius,
        TerrainType type,
        Random random,
        double fillChance) {

    int rows = map.length;
    int cols = map[0].length;

    for (int r = centerRow - radius; r <= centerRow + radius; r++) {
        for (int c = centerCol - radius; c <= centerCol + radius; c++) {
            if (!inBounds(r, c, rows, cols)) continue;

            double dr = r - centerRow;
            double dc = c - centerCol;
            double dist = Math.sqrt(dr * dr + dc * dc);

            if (dist <= radius) {
                double edgeFade = 1.0 - (dist / Math.max(1.0, radius));
                double chance = fillChance * (0.65 + edgeFade * 0.5);

                if (random.nextDouble() < chance) {
                    map[r][c] = type;
                }
            }
        }
    }
}

private int countNearby(TerrainType[][] map, int row, int col, TerrainType type, int radius) {
    int count = 0;

    for (int r = row - radius; r <= row + radius; r++) {
        for (int c = col - radius; c <= col + radius; c++) {
            if (!inBounds(r, c, map.length, map[0].length)) continue;
            if (r == row && c == col) continue;

            if (map[r][c] == type) {
                count++;
            }
        }
    }

    return count;
}

private TerrainType[][] copyMap(TerrainType[][] map) {
    TerrainType[][] copy = new TerrainType[map.length][map[0].length];

    for (int r = 0; r < map.length; r++) {
        System.arraycopy(map[r], 0, copy[r], 0, map[r].length);
    }

    return copy;
}

private int countCardinalSame(TerrainType[][] map, int row, int col, TerrainType type) {
    int count = 0;

    if (map[row - 1][col] == type) count++;
    if (map[row + 1][col] == type) count++;
    if (map[row][col - 1] == type) count++;
    if (map[row][col + 1] == type) count++;

    return count;
}

private TerrainType mostCommonNeighbor(TerrainType[][] map, int row, int col) {
    int[] counts = new int[TerrainType.values().length];

    for (int r = row - 1; r <= row + 1; r++) {
        for (int c = col - 1; c <= col + 1; c++) {
            if (r == row && c == col) continue;
            TerrainType type = map[r][c];
            counts[type.ordinal()]++;
        }
    }

    TerrainType best = map[row][col];
    int bestCount = -1;

    for (TerrainType type : TerrainType.values()) {
        int count = counts[type.ordinal()];
        if (count > bestCount) {
            bestCount = count;
            best = type;
        }
    }

    return best;
}

private BufferedImage getGrassFullTile(int row, int col) {
    int roll = Math.floorMod((row * 37) + (col * 73), 100);

    int variant;
    if (roll < 98) {
        variant = 0;   // 98%
    } else if (roll == 98) {
        variant = 1;   // 1%
    } else {
        variant = 2;   // 1%
    }

    return terrainManager.getSet(TerrainType.GRASS).get(variant);
}

private BufferedImage getGrassTileByOverlayIndex(int overlayIndex, int row, int col) {
    if (overlayIndex == TerrainAutoTile.FULL) {
        return getGrassFullTile(row, col);
    }

    return terrainManager.getSet(TerrainType.GRASS).get(overlayIndex + 2);
}
}
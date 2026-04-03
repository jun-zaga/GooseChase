package world;

public final class TerrainAutoTile {

    public static final int NONE = -1;

    public static final int FULL = 0;

    public static final int INNER_TL = 14;
    public static final int INNER_TR = 13;
    public static final int INNER_BL = 16;
    public static final int INNER_BR = 15;

    public static final int EDGE_TOP = 7;
    public static final int EDGE_LEFT = 5;
    public static final int EDGE_RIGHT = 4;
    public static final int EDGE_BOTTOM = 2;

    public static final int CAP_LEFT = 9;
    public static final int CAP_RIGHT = 10;
    public static final int CAP_TOP = 11;
    public static final int CAP_BOTTOM = 12;

    public static final int OUTER_TL = 8;
    public static final int OUTER_TR = 6;
    public static final int OUTER_BL = 3;
    public static final int OUTER_BR = 1;

    public static final int PATCH = 17;

    private static final boolean DEBUG_AUTOTILE = true;

    private TerrainAutoTile() { }

    public static int getGrassOverlayIndex(int[][] terrainMap, int row, int col, int grassTerrainType) {
        boolean centerGrass = isGrass(terrainMap, row, col, grassTerrainType);

        if (centerGrass) {
            boolean top = isGrass(terrainMap, row - 1, col, grassTerrainType);
            boolean right = isGrass(terrainMap, row, col + 1, grassTerrainType);
            boolean bottom = isGrass(terrainMap, row + 1, col, grassTerrainType);
            boolean left = isGrass(terrainMap, row, col - 1, grassTerrainType);

            boolean topLeft = isGrass(terrainMap, row - 1, col - 1, grassTerrainType);
            boolean topRight = isGrass(terrainMap, row - 1, col + 1, grassTerrainType);
            boolean bottomLeft = isGrass(terrainMap, row + 1, col - 1, grassTerrainType);
            boolean bottomRight = isGrass(terrainMap, row + 1, col + 1, grassTerrainType);

            int count = count(top, right, bottom, left);
            int result;

            if (count == 0) {
                result = PATCH;
            } else if (count == 4) {
                if (!topLeft) result = INNER_BR;
                else if (!topRight) result = INNER_BL;
                else if (!bottomLeft) result = INNER_TR;
                else if (!bottomRight) result = INNER_TL;
                else result = FULL;
            } else {
                result = FULL;
            }

            if (DEBUG_AUTOTILE) {
                System.out.println(
                    "[GRASS] row=" + row +
                    " col=" + col +
                    " center=1" +
                    " | T=" + bit(top) +
                    " R=" + bit(right) +
                    " B=" + bit(bottom) +
                    " L=" + bit(left) +
                    " | TL=" + bit(topLeft) +
                    " TR=" + bit(topRight) +
                    " BL=" + bit(bottomLeft) +
                    " BR=" + bit(bottomRight) +
                    " | -> " + result + " (" + tileName(result) + ")"
                );
            }

            return result;
        }

        boolean grassUp = isGrass(terrainMap, row - 1, col, grassTerrainType);
        boolean grassRight = isGrass(terrainMap, row, col + 1, grassTerrainType);
        boolean grassDown = isGrass(terrainMap, row + 1, col, grassTerrainType);
        boolean grassLeft = isGrass(terrainMap, row, col - 1, grassTerrainType);

        boolean upBlob = grassUp && grassCellHasGrassNeighbor(terrainMap, row - 1, col, grassTerrainType);
        boolean rightBlob = grassRight && grassCellHasGrassNeighbor(terrainMap, row, col + 1, grassTerrainType);
        boolean downBlob = grassDown && grassCellHasGrassNeighbor(terrainMap, row + 1, col, grassTerrainType);
        boolean leftBlob = grassLeft && grassCellHasGrassNeighbor(terrainMap, row, col - 1, grassTerrainType);

        int result = NONE;

        if (rightBlob && downBlob && !leftBlob && !upBlob) {
            result = OUTER_TL;
        } else if (leftBlob && downBlob && !rightBlob && !upBlob) {
            result = OUTER_TR;
        } else if (rightBlob && upBlob && !leftBlob && !downBlob) {
            result = OUTER_BL;
        } else if (leftBlob && upBlob && !rightBlob && !downBlob) {
            result = OUTER_BR;
        } else if (downBlob && !upBlob) {
            result = EDGE_TOP;
        } else if (upBlob && !downBlob) {
            result = EDGE_BOTTOM;
        } else if (rightBlob && !leftBlob) {
            result = EDGE_LEFT;
        } else if (leftBlob && !rightBlob) {
            result = EDGE_RIGHT;
        }

        if (DEBUG_AUTOTILE) {
            System.out.println(
                "[BOUNDARY] row=" + row +
                " col=" + col +
                " center=0" +
                " | U=" + bit(upBlob) +
                " R=" + bit(rightBlob) +
                " D=" + bit(downBlob) +
                " L=" + bit(leftBlob) +
                " | -> " + result +
                (result == NONE ? " (NONE)" : " (" + tileName(result) + ")")
            );
        }

        return result;
    }

    private static boolean grassCellHasGrassNeighbor(int[][] terrainMap, int row, int col, int grassTerrainType) {
        if (!isGrass(terrainMap, row, col, grassTerrainType)) {
            return false;
        }

        return isGrass(terrainMap, row - 1, col, grassTerrainType)
            || isGrass(terrainMap, row, col + 1, grassTerrainType)
            || isGrass(terrainMap, row + 1, col, grassTerrainType)
            || isGrass(terrainMap, row, col - 1, grassTerrainType);
    }

    private static int count(boolean... values) {
        int count = 0;
        for (boolean value : values) {
            if (value) count++;
        }
        return count;
    }

    private static boolean isGrass(int[][] terrainMap, int row, int col, int grassTerrainType) {
        if (row < 0 || row >= terrainMap.length || col < 0 || col >= terrainMap[0].length) {
            return false;
        }
        return terrainMap[row][col] == grassTerrainType;
    }

    private static String bit(boolean value) {
        return value ? "1" : "0";
    }

    public static String tileName(int index) {
        return switch (index) {
            case NONE -> "NONE";
            case FULL -> "FULL";
            case INNER_TL -> "INNER_TL";
            case INNER_TR -> "INNER_TR";
            case INNER_BL -> "INNER_BL";
            case INNER_BR -> "INNER_BR";
            case EDGE_TOP -> "EDGE_TOP";
            case EDGE_LEFT -> "EDGE_LEFT";
            case EDGE_RIGHT -> "EDGE_RIGHT";
            case EDGE_BOTTOM -> "EDGE_BOTTOM";
            case CAP_LEFT -> "CAP_LEFT";
            case CAP_RIGHT -> "CAP_RIGHT";
            case CAP_TOP -> "CAP_TOP";
            case CAP_BOTTOM -> "CAP_BOTTOM";
            case OUTER_TL -> "OUTER_TL";
            case OUTER_TR -> "OUTER_TR";
            case OUTER_BL -> "OUTER_BL";
            case OUTER_BR -> "OUTER_BR";
            case PATCH -> "PATCH";
            default -> "UNKNOWN";
        };
    }
}
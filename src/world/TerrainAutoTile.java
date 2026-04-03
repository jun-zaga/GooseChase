package world;

public final class TerrainAutoTile {

    public static final int NONE = -1;
    public static final int FULL = 0;

    // Inner names describe where the grass border is on the tile (concave)
    public static final int INNER_TL = 1;
    public static final int INNER_TR = 3;
    public static final int INNER_BL = 6;
    public static final int INNER_BR = 8;

    // Standard edges
    public static final int EDGE_TOP = 7;
    public static final int EDGE_LEFT = 5;
    public static final int EDGE_RIGHT = 4;
    public static final int EDGE_BOTTOM = 2;

    // Caps
    public static final int CAP_LEFT = 9;
    public static final int CAP_RIGHT = 10;
    public static final int CAP_TOP = 11;
    public static final int CAP_BOTTOM = 12;

    // Outer names describe the exposed / empty corner (convex)
    public static final int OUTER_TL = 13;
    public static final int OUTER_TR = 14;
    public static final int OUTER_BL = 15;
    public static final int OUTER_BR = 16;

    public static final int PATCH = 17;

    // New combo outer-corner / edge-pair tiles
    public static final int OUTER_ALL_CORNERS = 18;
    public static final int OUTER_BL_TL_TR = 19;
    public static final int OUTER_TL_TR = 20;
    public static final int OUTER_TL_BL_BR = 21;
    public static final int OUTER_BL_BR = 22;
    public static final int OUTER_BL_BR_TR = 23;
    public static final int OUTER_BR_TR = 24;
    public static final int OUTER_TL_TR_BR = 25;
    public static final int OUTER_TL_BL = 26;
    public static final int OUTER_TL_BR = 27;
    public static final int OUTER_TR_BL = 28;
    public static final int EDGES_LEFT_RIGHT = 29;
    public static final int EDGES_TOP_BOTTOM = 30;

    // Mixed inner-corner + opposite isolated outer-corner
    public static final int INNER_TL_OUTER_BR = 31;
    public static final int INNER_TR_OUTER_BL = 32;
    public static final int INNER_BL_OUTER_TR = 33;
    public static final int INNER_BR_OUTER_TL = 34;

    private static final boolean DEBUG_AUTOTILE = false;

    private TerrainAutoTile() { }

    public static int getOverlayIndex(TerrainType[][] terrainMap, int row, int col, TerrainType overlayType) {
        if (!overlayType.isEnabled()) return NONE;

        boolean center = isTerrain(terrainMap, row, col, overlayType);

        boolean T  = isTerrain(terrainMap, row - 1, col,     overlayType);
        boolean R  = isTerrain(terrainMap, row,     col + 1, overlayType);
        boolean B  = isTerrain(terrainMap, row + 1, col,     overlayType);
        boolean L  = isTerrain(terrainMap, row,     col - 1, overlayType);

        boolean TL = isTerrain(terrainMap, row - 1, col - 1, overlayType);
        boolean TR = isTerrain(terrainMap, row - 1, col + 1, overlayType);
        boolean BL = isTerrain(terrainMap, row + 1, col - 1, overlayType);
        boolean BR = isTerrain(terrainMap, row + 1, col + 1, overlayType);

        // CENTER TILE IS THE OVERLAY TYPE
        if (center) {
            int count = count(T, R, B, L);
            int result = (count == 0) ? PATCH : FULL;

            if (DEBUG_AUTOTILE) {
                System.out.println(
                    "[CENTER] row=" + row +
                    " col=" + col +
                    " | T=" + bit(T) +
                    " R=" + bit(R) +
                    " B=" + bit(B) +
                    " L=" + bit(L) +
                    " | TL=" + bit(TL) +
                    " TR=" + bit(TR) +
                    " BL=" + bit(BL) +
                    " BR=" + bit(BR) +
                    " | -> " + result
                );
            }

            return result;
        }

        boolean upBlob = T && terrainCellHasCardinalNeighbor(terrainMap, row - 1, col, overlayType);
        boolean rightBlob = R && terrainCellHasCardinalNeighbor(terrainMap, row, col + 1, overlayType);
        boolean downBlob = B && terrainCellHasCardinalNeighbor(terrainMap, row + 1, col, overlayType);
        boolean leftBlob = L && terrainCellHasCardinalNeighbor(terrainMap, row, col - 1, overlayType);

        boolean topLeftBlob = TL && terrainCellHasCardinalNeighbor(terrainMap, row - 1, col - 1, overlayType);
        boolean topRightBlob = TR && terrainCellHasCardinalNeighbor(terrainMap, row - 1, col + 1, overlayType);
        boolean bottomLeftBlob = BL && terrainCellHasCardinalNeighbor(terrainMap, row + 1, col - 1, overlayType);
        boolean bottomRightBlob = BR && terrainCellHasCardinalNeighbor(terrainMap, row + 1, col + 1, overlayType);

        int result = NONE;

        // 1) Opposite edge pairs
        if (L && R && !T && !B) {
            result = EDGES_LEFT_RIGHT;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        if (T && B && !L && !R) {
            result = EDGES_TOP_BOTTOM;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        // 2) Straight edges
        if (T && !B && !L && !R) {
            result = EDGE_BOTTOM;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        if (B && !T && !L && !R) {
            result = EDGE_TOP;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        if (L && !R && !T && !B) {
            result = EDGE_RIGHT;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        if (R && !L && !T && !B) {
            result = EDGE_LEFT;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        // 3) Caps
        if (T && L && R && !B) {
            result = CAP_TOP;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        if (B && L && R && !T) {
            result = CAP_BOTTOM;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        if (T && B && L && !R) {
            result = CAP_LEFT;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        if (T && B && R && !L) {
            result = CAP_RIGHT;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        // 4) Mixed inner-corner + opposite isolated outer-corner
        if (T && L && !B && !R && BR && !TR && !BL) {
            result = INNER_TL_OUTER_BR;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        if (T && R && !B && !L && BL && !TL && !BR) {
            result = INNER_TR_OUTER_BL;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        if (B && L && !T && !R && TR && !TL && !BR) {
            result = INNER_BL_OUTER_TR;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        if (B && R && !T && !L && TL && !TR && !BL) {
            result = INNER_BR_OUTER_TL;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        // 5) Standard inner corners
        if (T && L && !B && !R) {
            result = INNER_TL;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        if (T && R && !B && !L) {
            result = INNER_TR;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        if (B && L && !T && !R) {
            result = INNER_BL;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        if (B && R && !T && !L) {
            result = INNER_BR;
            return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                    topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
        }

        // 6) Diagonal-only outer corner combos
        if (!T && !R && !B && !L) {

            if (topLeftBlob && topRightBlob && bottomLeftBlob && bottomRightBlob) {
                result = OUTER_ALL_CORNERS;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (bottomLeftBlob && topLeftBlob && topRightBlob && !bottomRightBlob) {
                result = OUTER_BL_TL_TR;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (topLeftBlob && bottomLeftBlob && bottomRightBlob && !topRightBlob) {
                result = OUTER_TL_BL_BR;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (bottomLeftBlob && bottomRightBlob && topRightBlob && !topLeftBlob) {
                result = OUTER_BL_BR_TR;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (topLeftBlob && topRightBlob && bottomRightBlob && !bottomLeftBlob) {
                result = OUTER_TL_TR_BR;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (topLeftBlob && topRightBlob && !bottomLeftBlob && !bottomRightBlob) {
                result = OUTER_TL_TR;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (bottomLeftBlob && bottomRightBlob && !topLeftBlob && !topRightBlob) {
                result = OUTER_BL_BR;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (topLeftBlob && bottomLeftBlob && !topRightBlob && !bottomRightBlob) {
                result = OUTER_TL_BL;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (bottomRightBlob && topRightBlob && !topLeftBlob && !bottomLeftBlob) {
                result = OUTER_BR_TR;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (topLeftBlob && bottomRightBlob && !topRightBlob && !bottomLeftBlob) {
                result = OUTER_TL_BR;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (topRightBlob && bottomLeftBlob && !topLeftBlob && !bottomRightBlob) {
                result = OUTER_TR_BL;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (topLeftBlob && !topRightBlob && !bottomLeftBlob && !bottomRightBlob) {
                result = OUTER_BR;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (topRightBlob && !topLeftBlob && !bottomLeftBlob && !bottomRightBlob) {
                result = OUTER_BL;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (bottomLeftBlob && !topLeftBlob && !topRightBlob && !bottomRightBlob) {
                result = OUTER_TR;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }

            if (bottomRightBlob && !topLeftBlob && !topRightBlob && !bottomLeftBlob) {
                result = OUTER_TL;
                return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                        topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
            }
        }

        return debugBoundary(row, col, upBlob, rightBlob, downBlob, leftBlob,
                topLeftBlob, topRightBlob, bottomLeftBlob, bottomRightBlob, result);
    }

    public static boolean isPatchInsideHigherTerrain(
            TerrainType[][] terrainMap,
            int row,
            int col,
            TerrainType centerType,
            TerrainType surroundingType) {

        if (!isTerrain(terrainMap, row, col, centerType)) return false;

        boolean T = isTerrain(terrainMap, row - 1, col,     surroundingType);
        boolean R = isTerrain(terrainMap, row,     col + 1, surroundingType);
        boolean B = isTerrain(terrainMap, row + 1, col,     surroundingType);
        boolean L = isTerrain(terrainMap, row,     col - 1, surroundingType);

        return T && R && B && L;
    }

    private static boolean terrainCellHasCardinalNeighbor(
            TerrainType[][] terrainMap,
            int row,
            int col,
            TerrainType terrainType) {

        if (!isTerrain(terrainMap, row, col, terrainType)) return false;

        return isTerrain(terrainMap, row - 1, col, terrainType)
            || isTerrain(terrainMap, row, col + 1, terrainType)
            || isTerrain(terrainMap, row + 1, col, terrainType)
            || isTerrain(terrainMap, row, col - 1, terrainType);
    }

    private static boolean isTerrain(TerrainType[][] terrainMap, int row, int col, TerrainType type) {
        if (row < 0 || row >= terrainMap.length || col < 0 || col >= terrainMap[0].length) {
            return false;
        }
        return terrainMap[row][col] == type;
    }

    private static int count(boolean... values) {
        int count = 0;
        for (boolean value : values) {
            if (value) count++;
        }
        return count;
    }

    private static String bit(boolean value) {
        return value ? "1" : "0";
    }

    private static int debugBoundary(
            int row,
            int col,
            boolean upBlob,
            boolean rightBlob,
            boolean downBlob,
            boolean leftBlob,
            boolean topLeftBlob,
            boolean topRightBlob,
            boolean bottomLeftBlob,
            boolean bottomRightBlob,
            int result) {

        if (DEBUG_AUTOTILE) {
            System.out.println(
                "[BOUNDARY] row=" + row +
                " col=" + col +
                " | U=" + bit(upBlob) +
                " R=" + bit(rightBlob) +
                " D=" + bit(downBlob) +
                " L=" + bit(leftBlob) +
                " | TL=" + bit(topLeftBlob) +
                " TR=" + bit(topRightBlob) +
                " BL=" + bit(bottomLeftBlob) +
                " BR=" + bit(bottomRightBlob) +
                " | -> " + result
            );
        }

        return result;
    }
}
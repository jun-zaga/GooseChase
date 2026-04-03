package util;

public final class GameConstants {

    private GameConstants() { }

    public static final int ORIGINAL_TILE_SIZE = 16;
    public static final int SCALE = 3;
    public static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;

    public static final int SCREEN_COLS = 16;
    public static final int SCREEN_ROWS = 12;
    public static final int SCREEN_WIDTH = TILE_SIZE * SCREEN_COLS;
    public static final int SCREEN_HEIGHT = TILE_SIZE * SCREEN_ROWS;

    public static final int WORLD_COLS = 35;
    public static final int WORLD_ROWS = 67;
    public static final int WORLD_WIDTH = TILE_SIZE * WORLD_COLS;
    public static final int WORLD_HEIGHT = TILE_SIZE * WORLD_ROWS;

    public static final int TARGET_FPS = 60;
}
package world;

public class TerrainManager {

    private final TerrainTileSet grassSet = new TerrainTileSet("grass_tiles.png");
    private final TerrainTileSet dirtSet = new TerrainTileSet("dirt_tiles.png");
    private final TerrainTileSet waterSet = new TerrainTileSet("water_tiles.png");
    private final TerrainTileSet snowSet = new TerrainTileSet("snow_tiles.png");
    private final TerrainTileSet stoneSet = new TerrainTileSet("stone_tiles.png");
    private final TerrainTileSet mudSet = new TerrainTileSet("mud_tiles.png");
    private final TerrainTileSet sandSet = new TerrainTileSet("sand_tiles.png");

    public TerrainTileSet getSet(TerrainType type) {
        return switch (type) {
            case GRASS -> grassSet;
            case DIRT -> dirtSet;
            case WATER -> waterSet;
            case SNOW -> snowSet;
            case STONE -> stoneSet;
            case MUD -> mudSet;
            case SAND -> sandSet;
        };
    }
}
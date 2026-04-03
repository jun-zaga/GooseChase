package world;

public enum TerrainType {
    DIRT(0, 0, true),
    GRASS(1, 1, true),
    SAND(2, 2, false),
    STONE(3, 3, false),
    WATER(4, 4, false),
    SNOW(5, 5, false),
    MUD(6, 6, false);

    private final int mapValue;
    private final int priority;
    private final boolean enabled;

    TerrainType(int mapValue, int priority, boolean enabled) {
        this.mapValue = mapValue;
        this.priority = priority;
        this.enabled = enabled;
    }

    public int getMapValue() {
        return mapValue;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public static TerrainType fromMapValue(int value) {
        for (TerrainType type : values()) {
            if (type.mapValue == value) {
                return type;
            }
        }
        return DIRT;
    }
}
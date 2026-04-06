package world;

public enum TerrainType {
    GRASS(0, 0, true),
    DIRT(1, 1, true),
    WATER(2, 2, true),
    SNOW(3, 3, true),
    STONE(4, 4, true),
    MUD(5, 5, true),
    SAND(6, 6, true);

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
        return GRASS;
    }
}
package world;

import entity.Entity;
import graphics.Camera;
import java.awt.Graphics2D;

public class World {

    private final int tileSize;
    private final TileMap tileMap;
    // Pixel dimensions calculated from the tile map
    private final int worldPixelWidth;
    private final int worldPixelHeight;

    public World(int tileSize) {
        this.tileSize = tileSize;
        this.tileMap = new TileMap();
        
        // Synchronize world size with the loaded CSV map
        this.worldPixelWidth = tileMap.getCols() * tileSize;
        this.worldPixelHeight = tileMap.getRows() * tileSize;
    }

    public void draw(Graphics2D g2, Camera camera) {
        tileMap.draw(g2, camera);
    }

    /**
     * Prevents entities from leaving the map boundaries.
     */
    public void clampEntityPosition(Entity entity, int entityWidth, int entityHeight) {
        // Use the calculated world pixel dimensions
        double clampedX = Math.max(0, Math.min(entity.getX(), worldPixelWidth - entityWidth));
        double clampedY = Math.max(0, Math.min(entity.getY(), worldPixelHeight - entityHeight));

        if (clampedX != entity.getX() || clampedY != entity.getY()) {
            entity.setPosition(clampedX, clampedY);
            // Optional: You might want to allow sliding instead of hard stopping velocity
            // entity.setVelocity(0, 0); 
        }
    }

    public boolean collides(double x, double y, int entityWidth, int entityHeight) {
        // First check if the requested position is outside the world entirely
        if (x < 0 || y < 0 || x + entityWidth > worldPixelWidth || y + entityHeight > worldPixelHeight) {
            return true;
        }
        return tileMap.collidesWithSolidTile(x, y, entityWidth, entityHeight);
    }

    public int getWidth() { return worldPixelWidth; }
    public int getHeight() { return worldPixelHeight; }
    public int getTileSize() { return tileSize; }
    public TileMap getTileMap() { return tileMap; }
}
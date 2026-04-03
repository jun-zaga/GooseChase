package world;

import entity.Entity;
import graphics.Camera;
import java.awt.Graphics2D;

public class World {

    private final int width;
    private final int height;
    private final int tileSize;
    private final TileMap tileMap;

    public World(int width, int height, int tileSize) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.tileMap = new TileMap();
    }

    public void draw(Graphics2D g2, Camera camera) {
        tileMap.draw(g2, camera);
    }

    public void clampEntityPosition(Entity entity, int entityWidth, int entityHeight) {
        double clampedX = Math.max(0, Math.min(entity.getX(), width - entityWidth));
        double clampedY = Math.max(0, Math.min(entity.getY(), height - entityHeight));

        if (clampedX != entity.getX() || clampedY != entity.getY()) {
            entity.setPosition(clampedX, clampedY);
            entity.setVelocity(0, 0);
        }
    }

    public boolean collides(double x, double y, int entityWidth, int entityHeight) {
        return tileMap.collidesWithSolidTile(x, y, entityWidth, entityHeight);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileSize() {
        return tileSize;
    }

    public TileMap getTileMap() {
        return tileMap;
    }
}
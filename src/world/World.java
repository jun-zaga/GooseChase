package world;

import entity.Entity;
import graphics.Camera;
import java.awt.Color;
import java.awt.Graphics2D;
import util.GameConstants;

public class World {

    private final int width;
    private final int height;
    private final int tileSize;

    public World(int width, int height, int tileSize) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
    }

    public void draw(Graphics2D g2, Camera camera) {
        g2.setColor(new Color(110, 160, 110));
        g2.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        int startCol = Math.max(0, camera.getX() / tileSize);
        int endCol = Math.min((width / tileSize), (camera.getX() + GameConstants.SCREEN_WIDTH) / tileSize + 2);

        int startRow = Math.max(0, camera.getY() / tileSize);
        int endRow = Math.min((height / tileSize), (camera.getY() + GameConstants.SCREEN_HEIGHT) / tileSize + 2);

        g2.setColor(new Color(95, 145, 95));

        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                int worldX = col * tileSize;
                int worldY = row * tileSize;
                int screenX = camera.worldToScreenX(worldX);
                int screenY = camera.worldToScreenY(worldY);

                g2.drawRect(screenX, screenY, tileSize, tileSize);
            }
        }
    }

    public void clampEntityPosition(Entity entity, int entityWidth, int entityHeight) {
        double clampedX = Math.max(0, Math.min(entity.getX(), width - entityWidth));
        double clampedY = Math.max(0, Math.min(entity.getY(), height - entityHeight));

        if (clampedX != entity.getX() || clampedY != entity.getY()) {
            entity.setPosition(clampedX, clampedY);
            entity.setVelocity(0, 0);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
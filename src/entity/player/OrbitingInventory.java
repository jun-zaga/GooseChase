package entity.player;

import graphics.Camera;
import item.Item;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import util.GameConstants;

public class OrbitingInventory {

    private static final int MAX_SLOTS = 6;
    private static final double ORBIT_RADIUS = 34.0;
    private static final double ORBIT_SPEED = 0.02;
    private static final int ITEM_DRAW_SIZE = 20;

    private final Item[] slots;
    private double orbitAngle;

    public OrbitingInventory() {
        this.slots = new Item[MAX_SLOTS];
        this.orbitAngle = 0;
    }

    public void update() {
        orbitAngle += ORBIT_SPEED;
        if (orbitAngle >= Math.PI * 2) {
            orbitAngle -= Math.PI * 2;
        }
    }

    public boolean addItem(Item item) {
        for (int i = 0; i < MAX_SLOTS; i++) {
            if (slots[i] == null) {
                slots[i] = item;
                return true;
            }
        }
        return false;
    }

    public Item removeItem(int index) {
        if (index < 0 || index >= MAX_SLOTS) return null;

        Item removed = slots[index];
        slots[index] = null;
        return removed;
    }

    public Item getItem(int index) {
        if (index < 0 || index >= MAX_SLOTS) return null;
        return slots[index];
    }

    public boolean isFull() {
        for (Item item : slots) {
            if (item == null) return false;
        }
        return true;
    }

    public int getMaxSlots() {
        return MAX_SLOTS;
    }

    public int getItemCount() {
        int count = 0;
        for (Item item : slots) {
            if (item != null) count++;
        }
        return count;
    }

    public void render(Graphics2D g2, Camera camera, double playerX, double playerY) {
        int itemCount = getItemCount();
        if (itemCount == 0) return;

        double playerCenterX = playerX + (GameConstants.TILE_SIZE / 2.0);
        double playerCenterY = playerY + (GameConstants.TILE_SIZE / 2.0);

        int drawIndex = 0;
        for (int i = 0; i < MAX_SLOTS; i++) {
            Item item = slots[i];
            if (item == null) continue;

            double angle = orbitAngle + ((Math.PI * 2 * drawIndex) / itemCount);

            double orbitX = playerCenterX + Math.cos(angle) * ORBIT_RADIUS;
            double orbitY = playerCenterY + Math.sin(angle) * ORBIT_RADIUS;

            int screenX = camera.worldToScreenX(orbitX) - (ITEM_DRAW_SIZE / 2);
            int screenY = camera.worldToScreenY(orbitY) - (ITEM_DRAW_SIZE / 2);

            BufferedImage image = item.getImage();
            if (image != null) {
                g2.drawImage(image, screenX, screenY, ITEM_DRAW_SIZE, ITEM_DRAW_SIZE, null);
            }

            drawIndex++;
        }
    }
}
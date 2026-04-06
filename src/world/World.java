package world;

import entity.Chick;
import entity.Entity;
import entity.Swan;
import entity.player.Player;
import graphics.Camera;
import item.Item;
import item.WorldItem;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;

public class World {

    private final int tileSize;
    private final TileMap tileMap;

    private final int worldPixelWidth;
    private final int worldPixelHeight;

    private final List<Chick> chicks = new ArrayList<>();
    private final Swan swan = new Swan(300, 220);
    private final List<WorldItem> worldItems = new ArrayList<>();

    public World(int tileSize) {
        this.tileSize = tileSize;
        this.tileMap = new TileMap();

        this.worldPixelWidth = tileMap.getCols() * tileSize;
        this.worldPixelHeight = tileMap.getRows() * tileSize;

        spawnTestChicks();
        spawnTestItems();
    }

    private void spawnTestChicks() {
        chicks.add(new Chick(96, 96));
        chicks.add(new Chick(160, 96));
    }

public List<WorldItem> getWorldItems() {
    return worldItems;
}

public void dropItem(Item item, double x, double y) {
    worldItems.add(new WorldItem(item, x, y));
}

public void tryPickupNearbyItems(Player player) {
    double playerCenterX = player.getX() + (tileSize / 2.0);
    double playerCenterY = player.getY() + (tileSize / 2.0);

    double pickupRadius = 28.0;

    Iterator<WorldItem> iterator = worldItems.iterator();
    while (iterator.hasNext()) {
        WorldItem worldItem = iterator.next();

        double itemCenterX = worldItem.getX() + (tileSize / 2.0);
        double itemCenterY = worldItem.getY() + (tileSize / 2.0);

        double dx = itemCenterX - playerCenterX;
        double dy = itemCenterY - playerCenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= pickupRadius) {
            boolean added = player.getInventory().addItem(worldItem.getItem());
            if (added) {
                iterator.remove();
            }
        }
    }
}

    public boolean collidesWithOtherChick(Chick self, double x, double y, int width, int height) {
        for (Chick other : chicks) {
            if (other == self) continue;

            double otherX = other.getHitboxWorldX();
            double otherY = other.getHitboxWorldY();
            int otherW = other.getHitboxWidth();
            int otherH = other.getHitboxHeight();

            boolean overlap =
                    x < otherX + otherW &&
                    x + width > otherX &&
                    y < otherY + otherH &&
                    y + height > otherY;

            if (overlap) {
                return true;
            }
        }
        return false;
    }

    public void update(Player player) {
        for (Chick chick : chicks) {
            chick.update(player, this);
        }

        swan.update(this);
    }

    public void draw(Graphics2D g2, Camera camera) {
        tileMap.draw(g2, camera);
        renderEntities(g2, camera);
        renderWorldItems(g2, camera);
    }

    private void spawnTestItems() {
        BufferedImage eggImage = loadItemImage("egg.png");
        if (eggImage == null) return;

        Item egg1 = new Item("Egg", eggImage);
        Item egg2 = new Item("Egg", eggImage);
        Item egg3 = new Item("Egg", eggImage);
        Item egg4 = new Item("Egg", eggImage);

        worldItems.add(new WorldItem(egg1, 220, 180));
        worldItems.add(new WorldItem(egg2, 300, 140));
        worldItems.add(new WorldItem(egg3, 380, 220));
        worldItems.add(new WorldItem(egg4, 460, 180));
    }

    private BufferedImage loadItemImage(String fileName) {
        try {
            return ImageIO.read(new File("src/assets/items/" + fileName));
        } catch (Exception e) {
            System.err.println("Failed to load item image: " + fileName);
            e.printStackTrace();
            return null;
        }
    }

    private void renderWorldItems(Graphics2D g2, Camera camera) {
        int drawSize = 20;

        for (WorldItem worldItem : worldItems) {
            if (worldItem.getItem().getImage() == null) continue;

            int screenX = camera.worldToScreenX(worldItem.getX()) + (tileSize - drawSize) / 2;
            int screenY = camera.worldToScreenY(worldItem.getY()) + (tileSize - drawSize) / 2;

            g2.drawImage(
                    worldItem.getItem().getImage(),
                    screenX,
                    screenY,
                    drawSize,
                    drawSize,
                    null
            );
        }
    }

    private void renderEntities(Graphics2D g2, Camera camera) {
        swan.render(g2, camera);

        for (Chick chick : chicks) {
            chick.render(g2, camera);
        }
    }

    public void interactWithNearbyChick(Player player) {
        for (Chick chick : chicks) {
            if (chick.canInteract(player)) {
                chick.interact(player);
                break;
            }
        }
    }

    public List<Chick> getChicks() {
        return chicks;
    }

    public Swan getSwan() {
        return swan;
    }

    public void clampEntityPosition(Entity entity, int entityWidth, int entityHeight) {
        double clampedX = Math.max(0, Math.min(entity.getX(), worldPixelWidth - entityWidth));
        double clampedY = Math.max(0, Math.min(entity.getY(), worldPixelHeight - entityHeight));

        if (clampedX != entity.getX() || clampedY != entity.getY()) {
            entity.setPosition(clampedX, clampedY);
        }
    }

    public boolean collides(double x, double y, int entityWidth, int entityHeight) {
        if (x < 0 || y < 0 || x + entityWidth > worldPixelWidth || y + entityHeight > worldPixelHeight) {
            return true;
        }

        return tileMap.collidesWithSolidTile(x, y, entityWidth, entityHeight);
    }

    public int getWidth() {
        return worldPixelWidth;
    }

    public int getHeight() {
        return worldPixelHeight;
    }

    public int getTileSize() {
        return tileSize;
    }

    public TileMap getTileMap() {
        return tileMap;
    }
}
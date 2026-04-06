package entity;

import graphics.Animation;
import graphics.Camera;
import graphics.SpriteLoader;
import graphics.SpriteSet;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;
import util.GameConstants;
import world.World;

public class Swan extends Entity {

    private static final int DRAW_SIZE = GameConstants.TILE_SIZE;

    private static final int HITBOX_X = 8;
    private static final int HITBOX_Y = 18;
    private static final int HITBOX_W = 32;
    private static final int HITBOX_H = 22;

    private static final double WANDER_SPEED = 1.2;

    private final SpriteSet sprites;
    private final Animation walkAnimation;
    private final Rectangle hitbox;
    private final Random random = new Random();

    private int aiTimer = 0;
    private int aiDuration = 0;
    private double aiX = 0;
    private double aiY = 0;

    public Swan(double startX, double startY) {
        this.x = startX;
        this.y = startY;

        this.accel = 0;
        this.speedCap = WANDER_SPEED;
        this.drag = 0.85;
        this.facing = EDirection.DOWN;

        this.sprites = SpriteLoader.loadWalkingSprites("swan");
        this.walkAnimation = new Animation(12);
        this.hitbox = new Rectangle(HITBOX_X, HITBOX_Y, HITBOX_W, HITBOX_H);

        pickNewDirection();
    }

    public void update(World world) {
        runAI();

        xVelocity = aiX * WANDER_SPEED;
        yVelocity = aiY * WANDER_SPEED;

        double nextX = x + xVelocity;
        if (!collidesAt(world, nextX, y)) {
            x = nextX;
        } else {
            xVelocity = 0;
            pickNewDirection();
        }

        double nextY = y + yVelocity;
        if (!collidesAt(world, x, nextY)) {
            y = nextY;
        } else {
            yVelocity = 0;
            pickNewDirection();
        }

        updateFacingFromVelocity();
        walkAnimation.update(isMoving());
    }

    private void runAI() {
        aiTimer++;
        if (aiTimer >= aiDuration) {
            pickNewDirection();
        }
    }

    private void pickNewDirection() {
        aiTimer = 0;
        aiDuration = 30 + random.nextInt(90);

        int choice = random.nextInt(9);
        switch (choice) {
            case 0 -> { aiX = 0;  aiY = 0; }
            case 1 -> { aiX = 1;  aiY = 0; }
            case 2 -> { aiX = -1; aiY = 0; }
            case 3 -> { aiX = 0;  aiY = 1; }
            case 4 -> { aiX = 0;  aiY = -1; }
            case 5 -> { aiX = 1;  aiY = 1; }
            case 6 -> { aiX = -1; aiY = 1; }
            case 7 -> { aiX = 1;  aiY = -1; }
            case 8 -> { aiX = -1; aiY = -1; }
        }

        double length = Math.sqrt(aiX * aiX + aiY * aiY);
        if (length > 0) {
            aiX /= length;
            aiY /= length;
        }
    }

    private boolean collidesAt(World world, double worldX, double worldY) {
        return world.collides(
                worldX + hitbox.x,
                worldY + hitbox.y,
                hitbox.width,
                hitbox.height
        );
    }

    private void updateFacingFromVelocity() {
        if (!isMoving()) return;

        if (Math.abs(xVelocity) > Math.abs(yVelocity)) {
            facing = (xVelocity > 0) ? EDirection.RIGHT : EDirection.LEFT;
        } else {
            facing = (yVelocity > 0) ? EDirection.DOWN : EDirection.UP;
        }
    }

    public int getHitboxWorldX() {
        return (int) Math.round(x + hitbox.x);
    }

    public int getHitboxWorldY() {
        return (int) Math.round(y + hitbox.y);
    }

    public int getHitboxWidth() {
        return hitbox.width;
    }

    public int getHitboxHeight() {
        return hitbox.height;
    }

    public void render(Graphics2D g2, Camera camera) {
        BufferedImage frame = getCurrentFrame();

        int screenX = camera.worldToScreenX(x);
        int screenY = camera.worldToScreenY(y);

        g2.drawImage(frame, screenX, screenY, DRAW_SIZE, DRAW_SIZE, null);

        // debug hitbox
        g2.drawRect(
                screenX + hitbox.x,
                screenY + hitbox.y,
                hitbox.width,
                hitbox.height
        );
    }

    private BufferedImage getCurrentFrame() {
        BufferedImage[] row = switch (facing) {
            case UP -> sprites.getUp();
            case RIGHT -> sprites.getRight();
            case DOWN -> sprites.getDown();
            case LEFT -> sprites.getLeft();
        };

        if (!isMoving()) {
            return row[0];
        }

        int walkFrame = walkAnimation.getCurrentFrame();
        return (walkFrame == 0) ? row[1] : row[2];
    }
}
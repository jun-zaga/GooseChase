package entity;

import entity.player.Player;
import graphics.Animation;
import graphics.Camera;
import graphics.SpriteLoader;
import graphics.SpriteSet;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import util.GameConstants;
import world.World;

public class Chick extends Entity {

    private static final int DRAW_SIZE = GameConstants.TILE_SIZE;

    private static final int HITBOX_X = 10;
    private static final int HITBOX_Y = 18;
    private static final int HITBOX_W = 28;
    private static final int HITBOX_H = 22;

    private static final double FOLLOW_SPEED = 1.8;
    private static final double STOP_DISTANCE = 30.0;
    private static final double INTERACT_DISTANCE = 42.0;

    private final SpriteSet sprites;
    private final Animation walkAnimation;
    private final Rectangle hitbox;

    private boolean following = false;

    public Chick(double startX, double startY) {
        this.x = startX;
        this.y = startY;

        this.accel = 0;
        this.speedCap = FOLLOW_SPEED;
        this.drag = 0.85;
        this.facing = EDirection.DOWN;

        this.sprites = SpriteLoader.loadWalkingSprites("chick");
        this.walkAnimation = new Animation(12);
        this.hitbox = new Rectangle(HITBOX_X, HITBOX_Y, HITBOX_W, HITBOX_H);
    }

    public void update(Player player, World world) {
        if (following) {
            followPlayer(player);
        } else {
            xVelocity *= drag;
            yVelocity *= drag;
        }

        double nextX = x + xVelocity;
        if (!collidesAt(world, nextX, y)
                && !collidesWithOtherChicksAt(world, nextX, y)) {
            x = nextX;
        } else {
            xVelocity = 0;
        }

        double nextY = y + yVelocity;
        if (!collidesAt(world, x, nextY)
                && !collidesWithOtherChicksAt(world, x, nextY)) {
            y = nextY;
        } else {
            yVelocity = 0;
        }

        xVelocity *= drag;
        yVelocity *= drag;

        if (Math.abs(xVelocity) < 0.05) xVelocity = 0;
        if (Math.abs(yVelocity) < 0.05) yVelocity = 0;

        updateFacingFromVelocity();
        walkAnimation.update(isMoving());
    }

    private void followPlayer(Player player) {
        double targetX = player.getX();
        double targetY = player.getY();

        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= STOP_DISTANCE) {
            xVelocity = 0;
            yVelocity = 0;
            return;
        }

        double nx = dx / distance;
        double ny = dy / distance;

        xVelocity = nx * FOLLOW_SPEED;
        yVelocity = ny * FOLLOW_SPEED;
    }

    private boolean collidesAt(World world, double worldX, double worldY) {
        return world.collides(
                worldX + hitbox.x,
                worldY + hitbox.y,
                hitbox.width,
                hitbox.height
        );
    }

    private boolean collidesWithOtherChicksAt(World world, double worldX, double worldY) {
        return world.collidesWithOtherChick(
                this,
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

    public boolean canInteract(Player player) {
        double chickCenterX = x + DRAW_SIZE / 2.0;
        double chickCenterY = y + DRAW_SIZE / 2.0;
        double playerCenterX = player.getX() + GameConstants.TILE_SIZE / 2.0;
        double playerCenterY = player.getY() + GameConstants.TILE_SIZE / 2.0;

        double dx = playerCenterX - chickCenterX;
        double dy = playerCenterY - chickCenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance <= INTERACT_DISTANCE;
    }

    public void interact(Player player) {
        if (canInteract(player)) {
            following = true;
        }
    }

    public boolean isFollowing() {
        return following;
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

        if (!following) {
            g2.drawString("E", screenX + 16, screenY - 4);
        }

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
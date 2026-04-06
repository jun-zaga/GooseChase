package entity.player;

import entity.EDirection;
import entity.Entity;
import graphics.Animation;
import graphics.Camera;
import graphics.SpriteLoader;
import graphics.SpriteSet;
import input.InputHandler;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import util.GameConstants;
import world.World;

public class Player extends Entity {

    private static final int DRAW_SIZE = GameConstants.TILE_SIZE;

    private static final int HITBOX_X = 8;
    private static final int HITBOX_Y = 12;
    private static final int HITBOX_W = 32;
    private static final int HITBOX_H = 28;

    private final SpriteSet sprites;
    private final Animation walkAnimation;
    private final Rectangle hitbox;
    private final OrbitingInventory inventory;

    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;

        this.accel = 0.7;
        this.speedCap = 4.0;
        this.drag = 0.82;
        this.facing = EDirection.DOWN;
        this.inventory = new OrbitingInventory();

        this.sprites = SpriteLoader.loadWalkingSprites("goose");
        this.walkAnimation = new Animation(12);
        this.hitbox = new Rectangle(HITBOX_X, HITBOX_Y, HITBOX_W, HITBOX_H);
    }

    public void update(InputHandler input, World world) {
        double xInput = 0;
        double yInput = 0;

        if (input.isHeld(EDirection.UP)) yInput -= 1;
        if (input.isHeld(EDirection.DOWN)) yInput += 1;
        if (input.isHeld(EDirection.LEFT)) xInput -= 1;
        if (input.isHeld(EDirection.RIGHT)) xInput += 1;

        if (xInput != 0 || yInput != 0) {
            double length = Math.sqrt((xInput * xInput) + (yInput * yInput));
            xInput /= length;
            yInput /= length;

            xVelocity += xInput * accel;
            yVelocity += yInput * accel;

            updateFacing(xInput, yInput);
        }

        clampVelocity();
        moveWithCollision(world);
        walkAnimation.update(isMoving());
    

        if (input.consumeInteractPressed()) {
            world.interactWithNearbyChick(this);
        }


        inventory.update();
        world.tryPickupNearbyItems(this);

    }

    private void moveWithCollision(World world) {
        double nextX = x + xVelocity;
        if (!collidesAt(world, nextX, y)) {
            x = nextX;
        } else {
            xVelocity = 0;
        }

        double nextY = y + yVelocity;
        if (!collidesAt(world, x, nextY)) {
            y = nextY;
        } else {
            yVelocity = 0;
        }

        xVelocity *= drag;
        yVelocity *= drag;

        if (Math.abs(xVelocity) < 0.05) xVelocity = 0;
        if (Math.abs(yVelocity) < 0.05) yVelocity = 0;
    }

    private boolean collidesAt(World world, double worldX, double worldY) {
        return world.collides(
                worldX + hitbox.x,
                worldY + hitbox.y,
                hitbox.width,
                hitbox.height
        );
    }

    private void updateFacing(double xInput, double yInput) {
        if (Math.abs(xInput) > Math.abs(yInput)) {
            facing = (xInput > 0) ? EDirection.RIGHT : EDirection.LEFT;
        } else {
            facing = (yInput > 0) ? EDirection.DOWN : EDirection.UP;
        }
    }

    public void draw(Graphics2D g2, Camera camera) {
        BufferedImage frame = getCurrentFrame();

        int screenX = camera.worldToScreenX(x);
        int screenY = camera.worldToScreenY(y);

        g2.drawImage(frame, screenX, screenY, DRAW_SIZE, DRAW_SIZE, null);

        g2.drawRect(
                screenX + hitbox.x,
                screenY + hitbox.y,
                hitbox.width,
                hitbox.height
        );

        inventory.render(g2, camera, x, y);
    }

    public OrbitingInventory getInventory() {
        return inventory;
    }

    private BufferedImage getCurrentFrame() {
        int frameIndex = walkAnimation.getCurrentFrame();

        return switch (facing) {
            case UP -> sprites.getUp()[frameIndex];
            case RIGHT -> sprites.getRight()[frameIndex];
            case DOWN -> sprites.getDown()[frameIndex];
            case LEFT -> sprites.getLeft()[frameIndex];
        };
    }
}
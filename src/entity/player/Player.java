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

    private static final int JUMP_DISTANCE_TILES = 2;
    private static final int JUMP_DURATION_TICKS = 20;
    private static final double JUMP_ARC_HEIGHT = 12.0;

    private final SpriteSet sprites;
    private final Animation walkAnimation;
    private final Rectangle hitbox;
    private final OrbitingInventory inventory;

    private boolean jumping = false;
    private int jumpTicks = 0;
    private double jumpStartX;
    private double jumpStartY;
    private double jumpTargetX;
    private double jumpTargetY;
    private double jumpVisualOffsetY = 0;

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
        if (jumping) {
            updateJump();
            walkAnimation.update(false);

            inventory.update();
            world.tryPickupNearbyItems(this);

            if (input.consumeInteractPressed()) {
                world.interactWithNearbyChick(this);
            }
            return;
        }

        if (input.consumeJumpPressed()) {
            tryStartJump(input, world);
        }

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

    private void tryStartJump(InputHandler input, World world) {
        int tileSize = world.getTileSize();
        double jumpDistance = tileSize * JUMP_DISTANCE_TILES;

        int dirX = 0;
        int dirY = 0;

        if (input.isHeld(EDirection.LEFT)) dirX -= 1;
        if (input.isHeld(EDirection.RIGHT)) dirX += 1;
        if (input.isHeld(EDirection.UP)) dirY -= 1;
        if (input.isHeld(EDirection.DOWN)) dirY += 1;

        // no input held: fall back to facing
        if (dirX == 0 && dirY == 0) {
            switch (facing) {
                case UP -> dirY = -1;
                case DOWN -> dirY = 1;
                case LEFT -> dirX = -1;
                case RIGHT -> dirX = 1;
            }
        }

        double dx = dirX * jumpDistance;
        double dy = dirY * jumpDistance;

        double targetX = x + dx;
        double targetY = y + dy;

        if (canLandAt(world, targetX, targetY)) {
            jumping = true;
            jumpTicks = 0;

            jumpStartX = x;
            jumpStartY = y;
            jumpTargetX = targetX;
            jumpTargetY = targetY;

            xVelocity = 0;
            yVelocity = 0;

            if (dirX != 0 || dirY != 0) {
                updateFacing(dirX, dirY);
            }
        }
    }

    private boolean canLandAt(World world, double targetX, double targetY) {
        return !world.collides(
                targetX + hitbox.x,
                targetY + hitbox.y,
                hitbox.width,
                hitbox.height
        );
    }

    private void updateJump() {
        jumpTicks++;

        double progress = (double) jumpTicks / JUMP_DURATION_TICKS;
        if (progress > 1.0) progress = 1.0;

        x = lerp(jumpStartX, jumpTargetX, progress);
        y = lerp(jumpStartY, jumpTargetY, progress);

        jumpVisualOffsetY = Math.sin(progress * Math.PI) * JUMP_ARC_HEIGHT;

        if (jumpTicks >= JUMP_DURATION_TICKS) {
            x = jumpTargetX;
            y = jumpTargetY;
            jumpVisualOffsetY = 0;
            jumping = false;
        }
    }

    private double lerp(double a, double b, double t) {
        return a + ((b - a) * t);
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
        int screenY = camera.worldToScreenY(y - jumpVisualOffsetY);

        g2.drawImage(frame, screenX, screenY, DRAW_SIZE, DRAW_SIZE, null);

        g2.drawRect(
                camera.worldToScreenX(x) + hitbox.x,
                camera.worldToScreenY(y) + hitbox.y,
                hitbox.width,
                hitbox.height
        );

        inventory.render(g2, camera, x, y);
    }

    public OrbitingInventory getInventory() {
        return inventory;
    }

    public boolean isJumping() {
        return jumping;
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
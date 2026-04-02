package entity.player;

import entity.EDirection;
import entity.Entity;
import graphics.Animation;
import graphics.Camera;
import graphics.SpriteLoader;
import graphics.SpriteSet;
import input.InputHandler;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import util.GameConstants;
import world.World;

public class Player extends Entity {

    private final SpriteSet sprites;
    private final Animation walkAnimation;

    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;

        this.accel = 0.7;
        this.speedCap = 4.0;
        this.drag = 0.82;
        this.facing = EDirection.DOWN;

        this.sprites = SpriteLoader.loadGooseSprites();
        this.walkAnimation = new Animation(12, 2);
    }

    public void update(InputHandler input, World world) {
        double xInput = 0;
        double yInput = 0;

        if (input.isHeld(EDirection.UP)) {
            yInput -= 1;
        }
        if (input.isHeld(EDirection.DOWN)) {
            yInput += 1;
        }
        if (input.isHeld(EDirection.LEFT)) {
            xInput -= 1;
        }
        if (input.isHeld(EDirection.RIGHT)) {
            xInput += 1;
        }

        if (xInput != 0 || yInput != 0) {
            double length = Math.sqrt((xInput * xInput) + (yInput * yInput));
            xInput /= length;
            yInput /= length;

            xVelocity += xInput * accel;
            yVelocity += yInput * accel;

            updateFacing(xInput, yInput);
        }

        clampVelocity();
        applyVelocityWithDrag();
        world.clampEntityPosition(this, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
        walkAnimation.update(isMoving());
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

        g2.drawImage(frame, screenX, screenY, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE, null);
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
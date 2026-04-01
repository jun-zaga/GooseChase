import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        xPos = gp.tileSize * 4;
        yPos = gp.tileSize * 4;
        xVelocity = 0;
        yVelocity = 0;
        accel = 1;
        speedCap = 4;
        drag = 0.9;
        direction = EDirection.LEFT;
        spriteCounter = 0;
        spriteNum = 1;
    }

    public void move(EDirection direction) {
        this.direction = direction;

        switch (direction) {
            case UP -> {
                if (yVelocity > -speedCap) {
                    yVelocity -= accel;
                } else {
                    yVelocity = -speedCap;
                }
            }
            case RIGHT -> {
                if (xVelocity < speedCap) {
                    xVelocity += accel;
                } else {
                    xVelocity = speedCap;
                }
            }
            case DOWN -> {
                if (yVelocity < speedCap) {
                    yVelocity += accel;
                } else {
                    yVelocity = speedCap;
                }
            }
            case LEFT -> {
                if (xVelocity > -speedCap) {
                    xVelocity -= accel;
                } else {
                    xVelocity = -speedCap;
                }
            }
        }

        xVelocity = clamp(xVelocity, -speedCap, speedCap);
        yVelocity = clamp(yVelocity, -speedCap, speedCap);
    }

    public void update() {
        if (keyH.upPressed) {
            move(EDirection.UP);
        }
        if (keyH.downPressed) {
            move(EDirection.DOWN);
        }
        if (keyH.leftPressed) {
            move(EDirection.LEFT);
        }
        if (keyH.rightPressed) {
            move(EDirection.RIGHT);
        }

        applyDragOnly();

        boolean actuallyMoving = Math.abs(xVelocity) > 0.1 || Math.abs(yVelocity) > 0.1;

        if (actuallyMoving) {
            spriteCounter++;

            if (spriteCounter >= 8) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 1;
            spriteCounter = 0;
        }
    }

    public void getPlayerImage() {
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("/entities/goose/goose_walk_up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/entities/goose/goose_walk_up_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/entities/goose/goose_walk_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/entities/goose/goose_walk_right_2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/entities/goose/goose_walk_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/entities/goose/goose_walk_down_2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/entities/goose/goose_walk_left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/entities/goose/goose_walk_left_2.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage currentFrame = null;

        switch (direction) {
            case UP -> currentFrame = (spriteNum == 1) ? up1 : up2;
            case RIGHT -> currentFrame = (spriteNum == 1) ? right1 : right2;
            case DOWN -> currentFrame = (spriteNum == 1) ? down1 : down2;
            case LEFT -> currentFrame = (spriteNum == 1) ? left1 : left2;
        }

        g2.drawImage(currentFrame, getXPos(), getYPos(), gp.tileSize, gp.tileSize, null);
    }

    public void applyDragOnly() {
        xVelocity *= drag;
        yVelocity *= drag;

        if (Math.abs(xVelocity) < 0.05) xVelocity = 0;
        if (Math.abs(yVelocity) < 0.05) yVelocity = 0;

        xPos += xVelocity;
        yPos += yVelocity;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public int getXPos() {
        return (int) xPos;
    }

    public int getYPos() {
        return (int) yPos;
    }

    public void setXPos(double xPos) {
        this.xPos = xPos;
    }

    public void setYPos(double yPos) {
        this.yPos = yPos;
    }

    public void setXVelocity(double xVelocity) {
        this.xVelocity = xVelocity;
    }

    public void setYVelocity(double yVelocity) {
        this.yVelocity = yVelocity;
    }
}
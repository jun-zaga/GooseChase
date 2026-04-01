
import java.awt.image.BufferedImage;

public class Entity {
    protected double xPos;
    protected double yPos;
    protected double xVelocity;
    protected double yVelocity;
    protected double accel;
    protected double speedCap;
    protected double drag;


    public BufferedImage up1, up2, right1, right2, down1, down2, left1, left2;
    public EDirection direction;
    public int spriteCounter = 0;
    public int spriteNum = 1;


    public double getxPos() { return xPos; }
    public void setxPos(double xPos) { this.xPos = xPos; }

    public double getyPos() { return yPos; }
    public void setyPos(double yPos) { this.yPos = yPos; }

    public double getxVelocity() { return xVelocity; }
    public void setxVelocity(double xVelocity) { this.xVelocity = xVelocity; }

    public double getyVelocity() { return yVelocity; }
    public void setyVelocity(double yVelocity) { this.yVelocity = yVelocity; }

    public void setAccel(double accel) { this.accel = accel; }
    public void setSpeedCap(double speedCap) { this.speedCap = speedCap; }
    public void setDrag(double drag) { this.drag = drag; }

    public double getAccel() { return accel; }
    public double getSpeedCap() { return speedCap; }
    public double getDrag() { return drag; }
}
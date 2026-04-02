package entity;

public abstract class Entity {

    protected double x;
    protected double y;
    protected double xVelocity;
    protected double yVelocity;

    protected double accel;
    protected double speedCap;
    protected double drag;

    protected EDirection facing = EDirection.DOWN;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getDrawX() {
        return (int) Math.round(x);
    }

    public int getDrawY() {
        return (int) Math.round(y);
    }

    public EDirection getFacing() {
        return facing;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setVelocity(double xVelocity, double yVelocity) {
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
    }

    protected void clampVelocity() {
        xVelocity = clamp(xVelocity, -speedCap, speedCap);
        yVelocity = clamp(yVelocity, -speedCap, speedCap);
    }

    protected void applyVelocityWithDrag() {
        x += xVelocity;
        y += yVelocity;

        xVelocity *= drag;
        yVelocity *= drag;

        if (Math.abs(xVelocity) < 0.05) {
            xVelocity = 0;
        }
        if (Math.abs(yVelocity) < 0.05) {
            yVelocity = 0;
        }
    }

    protected double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public boolean isMoving() {
        return Math.abs(xVelocity) > 0.05 || Math.abs(yVelocity) > 0.05;
    }
}
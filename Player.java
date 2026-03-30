class Player {
    private double xPos;
    private double yPos;
    private double xVelocity;
    private double yVelocity;

    private final double accel = 1.0;
    private final double speedCap = 4.0;
    private final double drag = 0.90;

    public Player(double xPos, double yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void move(EDirection direction) {
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

    public double getXPos() {
        return xPos;
    }

    public double getYPos() {
        return yPos;
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
package graphics;

public class Camera {

    private int x;
    private int y;

    private final int viewportWidth;
    private final int viewportHeight;
    private final int worldWidth;
    private final int worldHeight;

    public Camera(int viewportWidth, int viewportHeight, int worldWidth, int worldHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void centerOn(double targetX, double targetY, int targetWidth, int targetHeight) {
        x = (int) Math.round(targetX + targetWidth / 2.0 - viewportWidth / 2.0);
        y = (int) Math.round(targetY + targetHeight / 2.0 - viewportHeight / 2.0);

        x = clamp(x, 0, Math.max(0, worldWidth - viewportWidth));
        y = clamp(y, 0, Math.max(0, worldHeight - viewportHeight));
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public int worldToScreenX(double worldX) {
        return (int) Math.round(worldX) - x;
    }

    public int worldToScreenY(double worldY) {
        return (int) Math.round(worldY) - y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
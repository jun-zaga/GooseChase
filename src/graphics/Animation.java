package graphics;

public class Animation {

    private final int frameDuration;
    private final int frameCount;

    private int tickCounter = 0;
    private int currentFrame = 0;

    public Animation(int frameDuration, int frameCount) {
        this.frameDuration = frameDuration;
        this.frameCount = frameCount;
    }

    public void update(boolean animate) {
        if (!animate) {
            tickCounter = 0;
            currentFrame = 0;
            return;
        }

        tickCounter++;
        if (tickCounter >= frameDuration) {
            tickCounter = 0;
            currentFrame = (currentFrame + 1) % frameCount;
        }
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
}
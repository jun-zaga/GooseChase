package graphics;

public class Animation {

    private final int frameDuration;
    private final int[] animationFrames = {0, 1, 2, 1};

    private int tickCounter = 0;
    private int animationIndex = 0;
    private int currentFrame = 1;

    public Animation(int frameDuration) {
        this.frameDuration = frameDuration;
    }

    public void update(boolean animate) {
        if (!animate) {
            tickCounter = 0;
            animationIndex = 0;
            currentFrame = 1;
            return;
        }

        tickCounter++;

        if (tickCounter >= frameDuration) {
            tickCounter = 0;
            animationIndex = (animationIndex + 1) % animationFrames.length;
            currentFrame = animationFrames[animationIndex];
        }
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
}
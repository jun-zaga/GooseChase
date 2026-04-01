import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    final int originalTileSize = 16;
    final int scale = 3;

    final int tileSize = originalTileSize * scale;
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;

    Thread gameThread;
    KeyHandler keyH = new KeyHandler();
    private final Player player;

    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.GRAY);
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(keyH);

        player = new Player(this, this.keyH);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / 60.0; // 60 FPS
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        for (EDirection dir : keyH.keysHeld) {
            player.move(dir);
        }

        player.update();
        keepPlayerOnScreen();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        player.draw(g2);

        g2.dispose();
    }

    private void keepPlayerOnScreen() {
        if (player.getXPos() < 0) {
            player.setXPos(0);
            player.setXVelocity(0);
        }
        if (player.getYPos() < 0) {
            player.setYPos(0);
            player.setYVelocity(0);
        }
        if (player.getXPos() > getWidth() - tileSize) {
            player.setXPos(getWidth() - tileSize);
            player.setXVelocity(0);
        }
        if (player.getYPos() > getHeight() - tileSize) {
            player.setYPos(getHeight() - tileSize);
            player.setYVelocity(0);
        }
    }
}
package core;

import entity.player.Player;
import graphics.Camera;
import input.InputHandler;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import util.GameConstants;
import world.World;

public class GamePanel extends JPanel implements Runnable {

    private Thread gameThread;

    private final InputHandler inputHandler;
    private final World world;
    private final Player player;
    private final Camera camera;

    public GamePanel() {
        setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);

        inputHandler = new InputHandler();
        addKeyListener(inputHandler);

        world = new World(
                GameConstants.TILE_SIZE
        );

        player = new Player(
                GameConstants.WORLD_WIDTH / 2.0,
                GameConstants.WORLD_HEIGHT / 2.0
        );

        camera = new Camera(
                GameConstants.SCREEN_WIDTH,
                GameConstants.SCREEN_HEIGHT,
                GameConstants.WORLD_WIDTH,
                GameConstants.WORLD_HEIGHT
        );
    }

    public void startGameThread() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    @Override
    public void run() {
        final double drawInterval = 1_000_000_000.0 / GameConstants.TARGET_FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            while (delta >= 1) {
                updateGame();
                repaint();
                delta--;
            }
        }
    }

    private void updateGame() {
        player.update(inputHandler, world);
        camera.centerOn(player.getX(), player.getY(), GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        world.draw(g2, camera);
        player.draw(g2, camera);

        g2.dispose();
    }
}
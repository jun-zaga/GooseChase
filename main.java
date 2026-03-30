import java.awt.*;
import java.awt.event.*;
import java.util.EnumSet;
import javax.swing.*;

public class main extends JFrame {

    public main() {
        setTitle("Goose Chase");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        GamePanel panel = new GamePanel();
        add(panel);

        setVisible(true);
        panel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(main::new);
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {

    private final Timer timer;
    private final EnumSet<EDirection> keysHeld = EnumSet.noneOf(EDirection.class);

    private final Player player;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        player = new Player(100, 100);

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect((int) player.getXPos(), (int) player.getYPos(), 40, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (EDirection dir : keysHeld) {
            player.move(dir);
        }

        player.applyDragOnly();
        keepPlayerOnScreen();
        repaint();
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
        if (player.getXPos() > getWidth() - 40) {
            player.setXPos(getWidth() - 40);
            player.setXVelocity(0);
        }
        if (player.getYPos() > getHeight() - 40) {
            player.setYPos(getHeight() - 40);
            player.setYVelocity(0);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> keysHeld.add(EDirection.UP);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> keysHeld.add(EDirection.RIGHT);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> keysHeld.add(EDirection.DOWN);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> keysHeld.add(EDirection.LEFT);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> keysHeld.remove(EDirection.UP);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> keysHeld.remove(EDirection.RIGHT);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> keysHeld.remove(EDirection.DOWN);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> keysHeld.remove(EDirection.LEFT);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
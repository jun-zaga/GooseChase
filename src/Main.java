import core.GamePanel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main extends JFrame {

    private BufferedImage icon;

    public Main() {
        loadIcon();

        setTitle("Goose Chase");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        if (icon != null) {
            setIconImage(icon);
        }

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        gamePanel.requestFocusInWindow();
        gamePanel.startGameThread();
    }

    private void loadIcon() {
        try {
            icon = ImageIO.read(getClass().getResourceAsStream("/entities/swan/swan.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("[WARN] Could not load window icon.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
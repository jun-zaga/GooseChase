import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class main extends JFrame {

    BufferedImage icon;

    {
        try {
            icon = ImageIO.read(getClass().getResourceAsStream("/entities/swan/swan.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public main() {
        setTitle("Goose Chase");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setIconImage(icon);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        gamePanel.requestFocusInWindow();
        gamePanel.startGameThread();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(main::new);
    }
}
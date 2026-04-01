import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EnumSet;

public class KeyHandler implements KeyListener {

    final EnumSet<EDirection> keysHeld = EnumSet.noneOf(EDirection.class);
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    @Override
    public void keyTyped(KeyEvent e) {
        // leave empty
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
}
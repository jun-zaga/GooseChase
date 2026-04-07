package input;

import entity.EDirection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EnumSet;

public class InputHandler implements KeyListener {

    private final EnumSet<EDirection> keysHeld = EnumSet.noneOf(EDirection.class);

    private boolean interactPressed = false;
    private boolean jumpPressed = false;

    @Override
    public void keyTyped(KeyEvent e) {
        // unused
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> keysHeld.add(EDirection.UP);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> keysHeld.add(EDirection.RIGHT);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> keysHeld.add(EDirection.DOWN);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> keysHeld.add(EDirection.LEFT);
            case KeyEvent.VK_E -> interactPressed = true;
            case KeyEvent.VK_SPACE -> jumpPressed = true;
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

    public boolean isHeld(EDirection direction) {
        return keysHeld.contains(direction);
    }

    public boolean consumeInteractPressed() {
        if (interactPressed) {
            interactPressed = false;
            return true;
        }
        return false;
    }

    public boolean consumeJumpPressed() {
        if (jumpPressed) {
            jumpPressed = false;
            return true;
        }
        return false;
    }
}
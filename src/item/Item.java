package item;

import java.awt.image.BufferedImage;

public class Item {

    private final String name;
    private final BufferedImage image;

    public Item(String name, BufferedImage image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public BufferedImage getImage() {
        return image;
    }
}
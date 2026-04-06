package item;

public class WorldItem {

    private final Item item;
    private double x;
    private double y;

    public WorldItem(Item item, double x, double y) {
        this.item = item;
        this.x = x;
        this.y = y;
    }

    public Item getItem() {
        return item;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
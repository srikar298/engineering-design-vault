package snakeladder;

public class Player {
    private final String name;
    private int position = 0;
    public Player(String n) { this.name = n; }
    public String getName() { return name; }
    public int getPosition() { return position; }
    public void setPosition(int p) { this.position = p; }
}

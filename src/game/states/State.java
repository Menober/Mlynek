package game.states;

import java.awt.*;

public abstract class State {
    public abstract void update();

    public abstract void render(Graphics g);
}

import org.osbot.rs07.script.Script;

import java.awt.*;

public class Cursor {
    private final BasicStroke TWO_STROKE = new BasicStroke(0.51f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
    private final Color WHITE_COLOR = new Color(255, 255, 0, 255);
    private Script s;

    public Cursor(Script s) {
        this.s = s;
    }

    public void draw(final Graphics2D g) {
        final Point point = s.mouse.getPosition();
        final int x = point.x;
        final int y = point.y;
        g.setColor(WHITE_COLOR);
        g.setStroke(TWO_STROKE);
        g.drawLine(x - 3, y + 3, x + 3, y - 3);
        g.drawLine(x + 3, y + 3, x - 3, y - 3);
        g.setColor(Color.WHITE);
    }
}
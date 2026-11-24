package page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * The WC page body. The top bar, the footer, and the COG/SOG panel stay
 * outside this page so Eng and WC share them.
 * The left side is the note and the Depth Window.
 * The right side is five drop-downs. Gauges starts open.
 */
public class WcPage {

    private static final Color DARK = new Color(28, 28, 30);
    private static final Color GREEN = new Color(28, 148, 52);
    private static final Color RED = new Color(196, 32, 40);
    private static final Color CYAN = new Color(0, 206, 209);
    private static final Color INK = new Color(16, 18, 20);
    private static final Color PURPLE = new Color(198, 140, 226);
    private static final int ROW = 26;

    private final JPanel body;
    private final JPanel side;

    public WcPage() {
        body = body();
        side = side();
    }

    /** The dark area and the Depth Window. */
    public JPanel getBody() {
        return body;
    }

    /** The five drop-downs. COG and SOG are not in here. */
    public JPanel getSide() {
        return side;
    }

    private JPanel body() {
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(DARK);
        left.setOpaque(true);
        left.add(note(), BorderLayout.CENTER);
        left.add(depthWindow(), BorderLayout.SOUTH);
        return left;
    }

    private JPanel note() {
        JPanel area = new JPanel(new GridBagLayout());
        area.setBackground(DARK);
        area.setOpaque(true);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PURPLE);
        card.setOpaque(true);
        card.setPreferredSize(new Dimension(360, 190));
        JLabel text = new JLabel(
            "<html><center>"
                + "On Click F1 - Guid mode the bottom<br>navigation bar changes .<br><br>"
                + "And to get back to the original Navigation<br>bar , they must click again on the F1 Guid<br>Mode button.<br><br>"
                + "These F1 to F6 are automatic algorithms ,<br>they are applied automatically."
                + "</center></html>");
        text.setForeground(Color.BLACK);
        text.setFont(new Font("Dialog", Font.PLAIN, 13));
        text.setHorizontalAlignment(SwingConstants.CENTER);
        text.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.add(text, BorderLayout.CENTER);
        area.add(card);
        return area;
    }

    private JPanel depthWindow() {
        JPanel depth = new JPanel(new GridBagLayout());
        depth.setBackground(new Color(18, 18, 20));
        depth.setPreferredSize(new Dimension(100, 210));
        depth.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(70, 70, 74)));
        JLabel title = new JLabel("Depth Window");
        title.setForeground(new Color(220, 220, 220));
        title.setFont(new Font("Dialog", Font.PLAIN, 16));
        depth.add(title);
        return depth;
    }

    private JPanel side() {
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBackground(DARK);
        column.setOpaque(true);
        column.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));

        column.add(section("Gauges", gaugesBody(), true));
        column.add(gap());
        column.add(section("Torpedo Trajectory - Tube:06", blankBody(), false));
        column.add(gap());
        column.add(section("Torpedo Parameters", blankBody(), false));
        column.add(gap());
        column.add(section("Demon Spectrum", blankBody(), false));
        column.add(gap());
        column.add(section("LL Torpedo Distance", blankBody(), false));
        column.add(Box.createVerticalGlue());
        return column;
    }

    /** A cyan bar that opens and closes the rows under it. */
    private JPanel section(String title, final JPanel block, boolean open) {
        block.setVisible(open);
        JPanel header = band(CYAN, INK, title);
        header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MouseAdapter toggle = new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                block.setVisible(!block.isVisible());
                block.getParent().revalidate();
                block.getParent().repaint();
            }
        };
        listen(header, toggle);

        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(0f);
        group.add(header);
        group.add(block);
        return group;
    }

    private JPanel gaugesBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setAlignmentX(0f);
        body.add(gap());
        body.add(meter("OS WIRE (Nm)", GREEN));
        body.add(meter("Weapon Wire (Nm)", GREEN));
        body.add(meter("Battery Auto (KWh)", GREEN));
        body.add(ends("OS/WpnDp", "Wpn/TgtD"));
        body.add(bar(RED));
        body.add(gap());
        return body;
    }

    /** Closed drop-downs open onto an empty dark block. */
    private static JPanel blankBody() {
        JPanel block = new JPanel();
        block.setBackground(DARK);
        block.setOpaque(true);
        block.setPreferredSize(new Dimension(280, 36));
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        block.setAlignmentX(0f);
        return block;
    }

    private static JPanel meter(String name, Color color) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        block.setAlignmentX(0f);
        JLabel label = new JLabel(name);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Dialog", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createEmptyBorder(2, 8, 0, 8));
        label.setAlignmentX(0f);
        block.add(label);
        block.add(bar(color));
        return block;
    }

    private static JPanel bar(Color color) {
        JPanel line = new JPanel();
        line.setBackground(color);
        line.setOpaque(true);
        line.setPreferredSize(new Dimension(260, 8));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        line.setAlignmentX(0f);
        JPanel pad = new JPanel(new BorderLayout());
        pad.setOpaque(false);
        pad.setBorder(BorderFactory.createEmptyBorder(2, 8, 4, 8));
        pad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        pad.setAlignmentX(0f);
        pad.add(line, BorderLayout.CENTER);
        return pad;
    }

    private static JPanel band(Color background, Color ink, String text) {
        JPanel line = new JPanel(new BorderLayout());
        line.setBackground(background);
        line.setOpaque(true);
        line.setPreferredSize(new Dimension(280, ROW));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW));
        line.setAlignmentX(0f);
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(ink);
        label.setFont(new Font("Dialog", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        line.add(label, BorderLayout.CENTER);
        line.add(arrows(ink), BorderLayout.EAST);
        return line;
    }

    private static JPanel ends(String leftText, String rightText) {
        JPanel line = new JPanel(new BorderLayout());
        line.setOpaque(false);
        line.setPreferredSize(new Dimension(280, 22));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        line.setAlignmentX(0f);
        JLabel left = new JLabel(leftText);
        left.setForeground(Color.WHITE);
        left.setFont(new Font("Dialog", Font.PLAIN, 13));
        left.setBorder(BorderFactory.createEmptyBorder(2, 8, 0, 8));
        JLabel right = new JLabel(rightText);
        right.setForeground(Color.WHITE);
        right.setFont(new Font("Dialog", Font.PLAIN, 13));
        right.setBorder(BorderFactory.createEmptyBorder(2, 8, 0, 8));
        line.add(left, BorderLayout.WEST);
        line.add(right, BorderLayout.EAST);
        return line;
    }

    private static JPanel arrows(Color ink) {
        JPanel marks = new JPanel(new GridLayout(2, 1));
        marks.setOpaque(false);
        marks.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 6));
        JLabel up = new JLabel("^", SwingConstants.CENTER);
        JLabel down = new JLabel("v", SwingConstants.CENTER);
        up.setForeground(ink);
        down.setForeground(ink);
        up.setFont(new Font("Dialog", Font.BOLD, 9));
        down.setFont(new Font("Dialog", Font.BOLD, 9));
        marks.add(up);
        marks.add(down);
        return marks;
    }

    private static void listen(Component component, MouseAdapter toggle) {
        component.addMouseListener(toggle);
        if (component instanceof Container) {
            Component[] children = ((Container) component).getComponents();
            for (int i = 0; i < children.length; i++) {
                listen(children[i], toggle);
            }
        }
    }

    private static java.awt.Component gap() {
        return Box.createVerticalStrut(3);
    }
}

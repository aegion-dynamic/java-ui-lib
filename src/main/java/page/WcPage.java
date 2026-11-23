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
 * The WC page. The window's top drop-down shows it when WC is chosen.
 * COG, SOG, and DSM sit on the right. Depth Window fills the lower left.
 */
public class WcPage extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color DARK = new Color(28, 28, 30);
    private static final Color MAROON = new Color(112, 24, 40);
    private static final Color GREEN = new Color(28, 148, 52);
    private static final Color CYAN = new Color(0, 206, 209);
    private static final Color INK = new Color(16, 18, 20);
    private static final int ROW = 26;

    public WcPage() {
        super(new BorderLayout(2, 0));
        setBackground(DARK);
        setOpaque(true);

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        JPanel open = new JPanel();
        open.setBackground(DARK);
        left.add(open, BorderLayout.CENTER);
        left.add(depthWindow(), BorderLayout.SOUTH);

        add(left, BorderLayout.CENTER);
        add(readout(), BorderLayout.EAST);
    }

    private JPanel depthWindow() {
        JPanel depth = new JPanel(new GridBagLayout());
        depth.setBackground(new Color(18, 18, 20));
        depth.setPreferredSize(new Dimension(100, 210));
        depth.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(48, 48, 52)));
        JLabel title = new JLabel("Depth Window");
        title.setForeground(new Color(220, 220, 220));
        title.setFont(new Font("Dialog", Font.PLAIN, 16));
        depth.add(title);
        return depth;
    }

    private JPanel readout() {
        JPanel column = new JPanel(new BorderLayout());
        column.setBackground(DARK);
        column.setPreferredSize(new Dimension(300, 100));

        JPanel stack = new JPanel();
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setOpaque(false);
        stack.add(band(MAROON, Color.WHITE, "COG", false));
        stack.add(gap());
        stack.add(measure("SOG", "-----Kt"));
        stack.add(gap());
        stack.add(measure("DSM", "------m"));
        stack.add(gap());
        stack.add(gauges());
        stack.add(band(CYAN, INK, "Torpedo Trajectory - Tube:02", true));
        stack.add(gap());
        stack.add(band(CYAN, INK, "Torpedo Parameters", true));
        stack.add(gap());
        stack.add(band(CYAN, INK, "Demon Spectrum", true));

        column.add(stack, BorderLayout.NORTH);
        return column;
    }

    /**
     * Gauges is a drop-down. The cyan bar opens and closes the green rows.
     * It starts open, which is how the screen shows them.
     */
    private JPanel gauges() {
        final JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.add(gap());
        body.add(band(GREEN, Color.WHITE, "OS WIRE", false));
        body.add(gap());
        body.add(band(GREEN, Color.WHITE, "Weapon Wire", false));
        body.add(gap());
        body.add(band(GREEN, Color.WHITE, "Battery Automony", false));
        body.add(gap());
        body.add(ends("OS/WpnD", "Wpn/TgLD"));
        body.add(gap());
        body.add(band(GREEN, Color.WHITE, "", false));
        body.add(gap());

        JPanel header = band(CYAN, INK, "Gauges", true);
        header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MouseAdapter toggle = new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                body.setVisible(!body.isVisible());
                revalidate();
                repaint();
            }
        };
        listen(header, toggle);

        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        block.setAlignmentX(0f);
        block.add(header);
        block.add(body);
        return block;
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

    private static JPanel band(Color background, Color ink, String text, boolean arrow) {
        JPanel line = new JPanel(new BorderLayout());
        line.setBackground(background);
        line.setOpaque(true);
        line.setPreferredSize(new Dimension(280, ROW));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW));
        line.setAlignmentX(0f);

        JLabel label = new JLabel(text);
        label.setForeground(ink);
        label.setFont(new Font("Dialog", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        if (arrow) {
            label.setHorizontalAlignment(SwingConstants.CENTER);
            line.add(arrows(ink), BorderLayout.EAST);
        }
        line.add(label, BorderLayout.CENTER);
        return line;
    }

    /** SOG and DSM. The name is on the left and the dashed unit is on the right. */
    private static JPanel measure(String name, String unit) {
        JPanel line = new JPanel(new BorderLayout());
        line.setBackground(MAROON);
        line.setOpaque(true);
        line.setPreferredSize(new Dimension(280, ROW));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW));
        line.setAlignmentX(0f);
        JLabel label = new JLabel(name);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Dialog", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        JLabel value = new JLabel(unit);
        value.setForeground(Color.WHITE);
        value.setFont(new Font("Dialog", Font.PLAIN, 13));
        value.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        line.add(label, BorderLayout.WEST);
        line.add(value, BorderLayout.EAST);
        return line;
    }

    /** The two names that sit above the last green bar. */
    private static JPanel ends(String leftText, String rightText) {
        JPanel line = new JPanel(new BorderLayout());
        line.setOpaque(false);
        line.setPreferredSize(new Dimension(280, ROW));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW));
        line.setAlignmentX(0f);
        JLabel left = new JLabel(leftText);
        left.setForeground(Color.WHITE);
        left.setFont(new Font("Dialog", Font.PLAIN, 13));
        left.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        JLabel right = new JLabel(rightText);
        right.setForeground(Color.WHITE);
        right.setFont(new Font("Dialog", Font.PLAIN, 13));
        right.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
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

    private static java.awt.Component gap() {
        return Box.createVerticalStrut(3);
    }
}

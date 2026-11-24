package page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * COG, SOG, and DSM. One panel, shown on Eng and on WC.
 * SOG is knots. DSM is metres. An empty value shows the dashes.
 */
public class MotionReadout extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color MAROON = new Color(112, 24, 40);
    private static final String SOG_EMPTY = "-----Kt";
    private static final String DSM_EMPTY = "------m";
    private static final Font FONT = new Font("Dialog", Font.PLAIN, 13);
    private static final int ROW = 26;

    private final JLabel sogValue = valueLabel(SOG_EMPTY);
    private final JLabel dsmValue = valueLabel(DSM_EMPTY);

    public MotionReadout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(28, 28, 30));
        setOpaque(true);
        add(titleRow("COG"));
        add(Box.createVerticalStrut(3));
        add(measureRow("SOG", sogValue));
        add(Box.createVerticalStrut(3));
        add(measureRow("DSM", dsmValue));
    }

    /** Speed over ground. Pass "" to show the dashes again. */
    public void setSog(String value) {
        show(sogValue, value, SOG_EMPTY, "Kt");
    }

    /** Distance. Pass "" to show the dashes again. */
    public void setDsm(String value) {
        show(dsmValue, value, DSM_EMPTY, "m");
    }

    public String getSog() {
        return read(sogValue, SOG_EMPTY, "Kt");
    }

    public String getDsm() {
        return read(dsmValue, DSM_EMPTY, "m");
    }

    private static JPanel titleRow(String name) {
        JPanel row = row();
        JLabel label = new JLabel(name);
        label.setFont(FONT);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        row.add(label, BorderLayout.WEST);
        return row;
    }

    private static JPanel measureRow(String name, JLabel value) {
        JPanel row = row();
        JLabel label = new JLabel(name);
        label.setFont(FONT);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private static JPanel row() {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(MAROON);
        row.setOpaque(true);
        row.setPreferredSize(new Dimension(280, ROW));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW));
        row.setAlignmentX(0f);
        return row;
    }

    private static JLabel valueLabel(String text) {
        JLabel value = new JLabel(text);
        value.setFont(FONT);
        value.setForeground(Color.WHITE);
        value.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        return value;
    }

    private static void show(JLabel label, String value, String empty, String suffix) {
        if (value == null || value.length() == 0) {
            label.setText(empty);
        } else {
            label.setText(value + suffix);
        }
    }

    private static String read(JLabel label, String empty, String suffix) {
        String text = label.getText();
        if (empty.equals(text)) {
            return "";
        }
        if (text.endsWith(suffix)) {
            return text.substring(0, text.length() - suffix.length());
        }
        return text;
    }
}

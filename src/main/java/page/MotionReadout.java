package page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The small panel above the form slot.
 * COG is a title strip. SOG and DSM are empty dashed values until set.
 * Units are Kt for SOG and m for DSM.
 */
public class MotionReadout extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color STRIP = new Color(166, 212, 245);
    private static final Color INK = new Color(20, 24, 28);
    private static final Color DASH = new Color(120, 128, 136);
    private static final String EMPTY = "------------------------";
    private static final Font LABEL_FONT = new Font("Dialog", Font.BOLD, 13);
    private static final Font VALUE_FONT = new Font("Dialog", Font.PLAIN, 13);

    private final JLabel sogValue = valueLabel();
    private final JLabel dsmValue = valueLabel();

    public MotionReadout() {
        setLayout(new GridLayout(3, 1, 0, 2));
        setBackground(new Color(28, 28, 30));
        setBorder(BorderFactory.createLineBorder(new Color(70, 74, 80)));
        setPreferredSize(new Dimension(640, 118));
        add(titleRow("COG"));
        add(measureRow("SOG", sogValue, "Kt"));
        add(measureRow("DSM", dsmValue, "m"));
    }

    /** Speed over ground. Pass "" to show the dashes again. */
    public void setSog(String value) {
        show(sogValue, value);
    }

    /** Distance. Pass "" to show the dashes again. */
    public void setDsm(String value) {
        show(dsmValue, value);
    }

    public String getSog() {
        return read(sogValue);
    }

    public String getDsm() {
        return read(dsmValue);
    }

    private static JPanel titleRow(String name) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(STRIP);
        row.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        JLabel label = new JLabel(name);
        label.setFont(LABEL_FONT);
        label.setForeground(INK);
        row.add(label, BorderLayout.WEST);
        return row;
    }

    private static JPanel measureRow(String name, JLabel value, String unit) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setBackground(STRIP);
        row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 8));

        JLabel label = new JLabel(name);
        label.setFont(LABEL_FONT);
        label.setForeground(INK);
        label.setOpaque(true);
        label.setBackground(STRIP);
        label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 4));
        label.setPreferredSize(new Dimension(52, 22));

        JLabel unitLabel = new JLabel(unit);
        unitLabel.setFont(LABEL_FONT);
        unitLabel.setForeground(INK);
        unitLabel.setPreferredSize(new Dimension(24, 22));

        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.CENTER);
        row.add(unitLabel, BorderLayout.EAST);
        return row;
    }

    private static JLabel valueLabel() {
        JLabel value = new JLabel(EMPTY);
        value.setFont(VALUE_FONT);
        value.setForeground(DASH);
        value.setOpaque(true);
        value.setBackground(Color.WHITE);
        value.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        return value;
    }

    private static void show(JLabel label, String value) {
        if (value == null || value.length() == 0) {
            label.setText(EMPTY);
            label.setForeground(DASH);
        } else {
            label.setText(value);
            label.setForeground(INK);
        }
    }

    private static String read(JLabel label) {
        String text = label.getText();
        if (EMPTY.equals(text)) {
            return "";
        }
        return text;
    }
}

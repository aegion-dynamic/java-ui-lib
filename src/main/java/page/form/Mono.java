package page.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 * Shared colours for the dark forms.
 * A new screen calls these methods so it matches this one
 * without copying colour and border lines into every class.
 *
 * JComboBox is not generic here. Generics on JComboBox arrived in Java 7.
 */
public final class Mono {

    public static final Color BACKGROUND = new Color(11, 22, 34);
    public static final Color FOREGROUND = Color.WHITE;
    public static final Color ACCENT = new Color(0, 214, 214);
    public static final Color FIELD = Color.WHITE;
    public static final Color FIELD_TEXT = new Color(20, 28, 38);
    public static final Color MUTED = new Color(130, 140, 150);
    public static final Color RULE = new Color(70, 90, 110);
    public static final Font FONT = new Font("Dialog", Font.PLAIN, 13);
    public static final Font TITLE_FONT = new Font("Dialog", Font.BOLD, 18);
    public static final Font HEADING_FONT = new Font("Dialog", Font.BOLD, 15);

    private static final Border LINE = BorderFactory.createLineBorder(RULE);
    private static final Border FIELD_LINE = BorderFactory.createLineBorder(new Color(210, 214, 220));
    /** Digits, and at most one decimal point. Empty matches, so a box can be cleared. */
    private static final Pattern NUMBER = Pattern.compile("[0-9]*\\.?[0-9]*");

    private Mono() {
    }

    public static void paint(JComponent component) {
        component.setOpaque(true);
        component.setBackground(BACKGROUND);
        component.setForeground(FOREGROUND);
        component.setFont(FONT);
    }

    /** White input. The text is dark so it can be read on the white field. */
    public static JTextField field(int columns) {
        JTextField field = new JTextField(columns);
        styleField(field);
        field.setForeground(FIELD_TEXT);
        return field;
    }

    /**
     * A number box. Digits and one decimal point are kept.
     * Letters and a second dot are refused. Empty is allowed.
     */
    public static JTextField number(int columns) {
        JTextField field = field(columns);
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new NumberFilter());
        return field;
    }

    public static JTextArea area(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        styleField(area);
        area.setForeground(FIELD_TEXT);
        area.setCaretColor(FIELD_TEXT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    public static void styleField(JComponent component) {
        component.setOpaque(true);
        component.setBackground(FIELD);
        component.setFont(FONT);
        component.setBorder(FIELD_LINE);
        if (component instanceof JTextField) {
            ((JTextField) component).setCaretColor(FIELD_TEXT);
        }
    }

    /** Java 6: use the raw JComboBox, not JComboBox&lt;String&gt;. Items may be strings or enums. */
    public static JComboBox combo(Object[] items) {
        JComboBox box = new JComboBox(items);
        box.setBackground(FIELD);
        box.setForeground(FIELD_TEXT);
        box.setFont(FONT);
        box.setBorder(FIELD_LINE);
        box.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(
                    JList list, Object value, int index, boolean selected, boolean focus) {
                Component row = super.getListCellRendererComponent(list, value, index, selected, focus);
                if (row instanceof JComponent) {
                    JComponent cell = (JComponent) row;
                    cell.setOpaque(true);
                    cell.setBackground(FIELD);
                    cell.setForeground(FIELD_TEXT);
                    cell.setFont(FONT);
                }
                return row;
            }
        });
        return box;
    }

    /** Dark button with a light border. Cancel, and the small OK by a field. */
    public static JButton button(String text) {
        JButton button = new JButton(text);
        paint(button);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                LINE,
                BorderFactory.createEmptyBorder(4, 16, 4, 16)));
        return button;
    }

    /** The filled cyan OK at the bottom of a form. */
    public static JButton accentButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT);
        button.setForeground(new Color(8, 24, 34));
        button.setFont(new Font("Dialog", Font.BOLD, 13));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 22, 6, 22));
        return button;
    }

    public static JCheckBox check(String text) {
        JCheckBox box = new JCheckBox(text);
        paint(box);
        box.setFocusPainted(false);
        box.setIcon(new MarkIcon(false, false));
        box.setSelectedIcon(new MarkIcon(false, true));
        return box;
    }

    public static JRadioButton radio(String text) {
        JRadioButton radio = new JRadioButton(text);
        paint(radio);
        radio.setFocusPainted(false);
        radio.setIcon(new MarkIcon(true, false));
        radio.setSelectedIcon(new MarkIcon(true, true));
        return radio;
    }

    /**
     * White circle for a radio, white square for a check box.
     * Drawn here so the mark stays white on Ubuntu's own look and feel.
     */
    public static final class MarkIcon implements Icon {

        private final boolean radio;
        private final boolean selected;
        private final int size = 14;

        public MarkIcon(boolean radio, boolean selected) {
            this.radio = radio;
            this.selected = selected;
        }

        public int getIconWidth() {
            return size;
        }

        public int getIconHeight() {
            return size;
        }

        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(selected ? ACCENT : new Color(190, 198, 206));
            if (radio) {
                g.drawOval(x, y, size - 1, size - 1);
                if (selected) {
                    g.fillOval(x + 3, y + 3, size - 7, size - 7);
                }
            } else {
                g.drawRect(x, y, size - 1, size - 1);
                if (selected) {
                    g.fillRect(x + 3, y + 3, size - 7, size - 7);
                }
            }
            g.dispose();
        }
    }

    /** Small white triangle used by a section header. Pointing down means open. */
    public static final class TriangleIcon implements Icon {

        private final boolean open;
        private final int size = 10;

        public TriangleIcon(boolean open) {
            this.open = open;
        }

        public int getIconWidth() {
            return size;
        }

        public int getIconHeight() {
            return size;
        }

        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(FOREGROUND);
            int[] xs;
            int[] ys;
            if (open) {
                xs = new int[] {x, x + size, x + (size / 2)};
                ys = new int[] {y + 2, y + 2, y + size - 1};
            } else {
                xs = new int[] {x + 2, x + 2, x + size - 1};
                ys = new int[] {y, y + size, y + (size / 2)};
            }
            g.fillPolygon(xs, ys, 3);
            g.dispose();
        }
    }

    /** Keeps a text field on the number pattern. A change that would break it is dropped. */
    private static final class NumberFilter extends DocumentFilter {

        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                throws BadLocationException {
            if (fits(fb.getDocument(), offset, 0, text)) {
                super.insertString(fb, offset, text, attr);
            }
        }

        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
                throws BadLocationException {
            if (fits(fb.getDocument(), offset, length, text)) {
                super.replace(fb, offset, length, text, attr);
            }
        }

        private static boolean fits(Document document, int offset, int length, String text)
                throws BadLocationException {
            String current = document.getText(0, document.getLength());
            String incoming = text == null ? "" : text;
            String next = current.substring(0, offset) + incoming + current.substring(offset + length);
            return NUMBER.matcher(next).matches();
        }
    }
}

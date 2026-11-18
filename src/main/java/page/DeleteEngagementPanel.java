package page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

import page.form.FormRow;
import page.form.Mono;

/**
 * Delete Engagement. F3 shows it.
 * Eng No is the list of engagement numbers. Eng Name is the name for that number.
 * The list starts as 1, 2, 3. Replace it when real engagements are available.
 */
public class DeleteEngagementPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JComboBox engNo;
    private final JTextField engName;
    private final JButton cancelButton;
    private final JButton okButton;

    public DeleteEngagementPanel() {
        super(new BorderLayout());
        LookAndFeel previous = UIManager.getLookAndFeel();
        try {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception ex) {
            /* Keep the look that is already installed. */
        }
        engNo = Mono.combo(new String[] {"1", "2", "3"});
        engName = Mono.field(18);
        cancelButton = colored("Cancel", new Color(214, 64, 64));
        okButton = colored("OK", new Color(30, 150, 78));
        try {
            build();
        } finally {
            try {
                UIManager.setLookAndFeel(previous);
            } catch (Exception ex) {
                /* Leave the look that build() just used. */
            }
        }
    }

    private void build() {
        Mono.paint(this);

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(Mono.ACCENT);
        banner.setOpaque(true);
        JLabel title = new JLabel("DELETE ENGAGEMENT", SwingConstants.CENTER);
        title.setForeground(Color.BLACK);
        title.setFont(new Font("Dialog", Font.BOLD, 14));
        title.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        banner.add(title, BorderLayout.CENTER);

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setOpaque(false);
        fields.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        fields.add(new FormRow("Eng No", engNo));
        fields.add(Box.createVerticalStrut(8));
        fields.add(new FormRow("Eng Name", engName));
        fields.add(Box.createVerticalStrut(8));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(cancelButton);
        buttons.add(okButton);
        fields.add(buttons);

        JPanel body = new JPanel(new BorderLayout());
        Mono.paint(body);
        body.add(fields, BorderLayout.NORTH);

        add(banner, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
    }

    /** The number currently shown in Eng No. */
    public String getEngNo() {
        Object item = engNo.getSelectedItem();
        if (item == null) {
            return "";
        }
        return item.toString();
    }

    public String getEngName() {
        return engName.getText();
    }

    /** Put a name into the Eng Name field. Pass "" to clear it. */
    public void setEngName(String name) {
        engName.setText(name == null ? "" : name);
    }

    /**
     * Replace the Eng No list. numbers is the engagement numbers you already have.
     * The first number is selected. An empty array leaves the list empty.
     */
    public void setEngNumbers(String[] numbers) {
        engNo.removeAllItems();
        if (numbers == null) {
            return;
        }
        for (int i = 0; i < numbers.length; i++) {
            engNo.addItem(numbers[i]);
        }
        if (numbers.length > 0) {
            engNo.setSelectedIndex(0);
        }
    }

    public JButton getOkButton() {
        return okButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    private static JButton colored(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Dialog", Font.BOLD, 13));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 22, 6, 22));
        return button;
    }
}

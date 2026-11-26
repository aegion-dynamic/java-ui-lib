package page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

import page.form.FormRow;
import page.form.Mono;
import page.form.OptionGroup;
import page.model.WeaponConfigState;

/**
 * Delete Weapon Config.
 * EHWT WAR / EHWT EXE ask the backend for that type's names.
 * Config name is the drop-down of those names. OK deletes the chosen one.
 */
public class DeleteWeaponConfigPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final OptionGroup weapon;
    private final JComboBox configName;
    private boolean filling;
    private final JButton cancelButton;
    private final JButton okButton;

    public DeleteWeaponConfigPanel() {
        super(new BorderLayout());
        LookAndFeel previous = UIManager.getLookAndFeel();
        try {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception ex) {
            /* Keep the look that is already installed. */
        }
        weapon = new OptionGroup(new String[] {"EHWT WAR", "EHWT EXE"}, -1, 2);
        configName = Mono.combo(new String[0]);
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
        JLabel title = new JLabel("DELETE WEAPON CONFIG", SwingConstants.CENTER);
        title.setForeground(Color.BLACK);
        title.setFont(new Font("Dialog", Font.BOLD, 14));
        title.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        banner.add(title, BorderLayout.CENTER);

        JPanel radios = new JPanel(new FlowLayout(FlowLayout.CENTER, 36, 0));
        radios.setOpaque(false);
        radios.add(weapon);

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setOpaque(false);
        fields.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        fields.add(radios);
        fields.add(Box.createVerticalStrut(8));
        fields.add(new FormRow("Config name", configName));
        fields.add(Box.createVerticalStrut(10));
        fields.add(rule());
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

    /** EHWT WAR, EHWT EXE, or null when neither radio is selected. */
    public WeaponConfigState getWeaponState() {
        String label = weapon.getSelected();
        if ("EHWT WAR".equals(label)) {
            return WeaponConfigState.EHWT_WAR;
        }
        if ("EHWT EXE".equals(label)) {
            return WeaponConfigState.EHWT_EXE;
        }
        return null;
    }

    /** Runs when the user clicks EHWT WAR or EHWT EXE. */
    public void addWeaponListener(ActionListener listener) {
        weapon.addListener(listener);
    }

    /** The name currently shown in the drop-down, or "" when the list is empty. */
    public String getConfigName() {
        Object item = configName.getSelectedItem();
        if (item == null) {
            return "";
        }
        return item.toString();
    }

    /** Replace the names in the drop-down. The first name is selected. */
    public void setNames(String[] names) {
        filling = true;
        configName.removeAllItems();
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                configName.addItem(names[i]);
            }
        }
        filling = false;
        if (names != null && names.length > 0) {
            configName.setSelectedIndex(0);
        }
    }

    public void addNameListener(ActionListener listener) {
        configName.addActionListener(listener);
    }

    public boolean isFilling() {
        return filling;
    }

    /** Clear the radios and the name list. */
    public void reset() {
        filling = true;
        weapon.setSelected("");
        configName.removeAllItems();
        filling = false;
    }

    public JButton getOkButton() {
        return okButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    private static JPanel rule() {
        JPanel line = new JPanel();
        line.setBackground(new Color(176, 184, 192));
        line.setOpaque(true);
        line.setPreferredSize(new Dimension(10, 1));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return line;
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

package page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

import page.form.Block;
import page.form.FormRow;
import page.form.FormStack;
import page.form.GroupLabel;
import page.form.Mono;
import page.form.OptionGroup;
import page.model.WeaponConfig;

/**
 * Create Weapon Configuration.
 * Shown by the first function key after F4 has swapped the footer.
 * OK reads the fields. The page then shows the saved stage 6 screen.
 */
public class CreateWeaponConfigPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String[] SPEEDS = {"24", "30", "35", "40", "45", "50"};
    private static final String[] ACOUSTIC = {"Simultaneous", "Wake"};
    private static final String[] ATTACK = {"Sub Surface", "Surface"};
    private static final String[] ON_OFF = {"ON", "OFF"};
    private static final String[] GUIDANCE = {"Bg Rider Out", "CCM", "TCCM", "Self Guided"};
    private static final String[] HOMING = {"Pro Navigation", "Intercept"};

    private final OptionGroup weapon;
    private final JTextField configName;
    private final JComboBox initialSpeed;
    private final JTextField orderedSpeed;
    private final JTextField orderedDepth;
    private final JTextField searchSpeed;
    private final JTextField enbDist;
    private final JTextField safetySonarRange;
    private final JTextField udl;
    private final JTextField ldl;
    private final JComboBox ash;
    private final JTextField maxRd;
    private final JComboBox acousticHeadMode;
    private final JTextField attackSonarRange;
    private final JComboBox attackMode;
    private final JComboBox wakeGuidance;
    private final JComboBox guidanceMode;
    private final JComboBox homingMode;
    private final JTextArea comments;
    private final JButton cancelButton;
    private final JButton okButton;
    private JScrollPane formScroll;

    public CreateWeaponConfigPanel() {
        super(new BorderLayout());
        LookAndFeel previous = UIManager.getLookAndFeel();
        try {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception ex) {
            /* Keep the look that is already installed. */
        }
        weapon = new OptionGroup(new String[] {"EHWT WAR", "EHWT EXE"}, -1, 2);
        configName = Mono.field(18);
        initialSpeed = blankCombo(SPEEDS);
        orderedSpeed = Mono.field(12);
        orderedDepth = Mono.field(12);
        searchSpeed = Mono.field(12);
        enbDist = Mono.field(8);
        safetySonarRange = Mono.field(12);
        udl = Mono.field(8);
        ldl = Mono.field(8);
        ash = blankCombo(ON_OFF);
        maxRd = Mono.field(8);
        acousticHeadMode = blankCombo(ACOUSTIC);
        attackSonarRange = Mono.field(12);
        attackMode = blankCombo(ATTACK);
        wakeGuidance = blankCombo(ON_OFF);
        guidanceMode = blankCombo(GUIDANCE);
        homingMode = blankCombo(HOMING);
        comments = Mono.area(4, 20);
        cancelButton = colored("Cancel", new Color(214, 64, 64));
        okButton = colored("OK", new Color(30, 150, 78));
        try {
            build();
            reset();
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
        add(titleBar(), BorderLayout.NORTH);
        formScroll = scroller(form());
        add(formScroll, BorderLayout.CENTER);
        add(buttonBar(), BorderLayout.SOUTH);
    }

    private FormStack form() {
        FormStack form = new FormStack();
        form.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        form.add(banner("WEAPON CONFIGURATION"));
        form.add(pad(weapon));
        form.add(pad(new FormRow("Config name", configName)));
        form.add(banner("PARAMETER CONFIGURATION"));
        form.add(new GroupLabel("TORPEDO TRAJECTORY"));
        form.add(pad(new FormRow("Initial Speed", initialSpeed)));
        form.add(pad(new FormRow("Ordered Speed", orderedSpeed)));
        form.add(pad(new FormRow("Ordered Depth", orderedDepth)));
        form.add(pad(new FormRow("Search Speed", searchSpeed)));
        form.add(new GroupLabel("SAFETY PARAMETERS"));
        form.add(pad(new FormRow("Enb Dist", enbDist)));
        form.add(pad(new FormRow("Sonar Range", safetySonarRange)));
        form.add(pad(pair(labeled("UDL", udl), labeled("LDL", ldl))));
        form.add(pad(pair(labeled("ASH", ash), labeled("Max RD", maxRd))));
        form.add(new GroupLabel("ATTACK PARAMETERS"));
        form.add(pad(new FormRow("Acoustic Head Mode", acousticHeadMode)));
        form.add(pad(new FormRow("Sonar Range", attackSonarRange)));
        form.add(pad(new FormRow("Attack Mode", attackMode)));
        form.add(pad(new FormRow("Wake Guidance", wakeGuidance)));
        form.add(pad(new FormRow("Guidance Mode", guidanceMode)));
        form.add(pad(new FormRow("Homing Mode", homingMode)));
        form.add(banner("WC COMMENTS"));
        form.add(pad(comments));
        return form;
    }

    /** Blank create form. Enb Dist, Max RD, and the attack sonar range keep their starting numbers. */
    public void reset() {
        weapon.setSelected("");
        configName.setText("");
        clear(initialSpeed);
        orderedSpeed.setText("");
        orderedDepth.setText("");
        searchSpeed.setText("");
        enbDist.setText("500");
        safetySonarRange.setText("");
        udl.setText("");
        ldl.setText("");
        clear(ash);
        maxRd.setText("26.99");
        clear(acousticHeadMode);
        attackSonarRange.setText("2000.00");
        clear(attackMode);
        clear(wakeGuidance);
        clear(guidanceMode);
        clear(homingMode);
        comments.setText("");
        scrollToTop();
    }

    public WeaponConfig readForm() {
        WeaponConfig config = new WeaponConfig();
        config.setWeapon(weapon.getSelected());
        config.setConfigName(configName.getText());
        config.setInitialSpeed(selected(initialSpeed));
        config.setOrderedSpeed(orderedSpeed.getText());
        config.setOrderedDepth(orderedDepth.getText());
        config.setSearchSpeed(searchSpeed.getText());
        config.setEnbDist(enbDist.getText());
        config.setSafetySonarRange(safetySonarRange.getText());
        config.setUdl(udl.getText());
        config.setLdl(ldl.getText());
        config.setAsh(selected(ash));
        config.setMaxRd(maxRd.getText());
        config.setAcousticHeadMode(selected(acousticHeadMode));
        config.setAttackSonarRange(attackSonarRange.getText());
        config.setAttackMode(selected(attackMode));
        config.setWakeGuidance(selected(wakeGuidance));
        config.setGuidanceMode(selected(guidanceMode));
        config.setHomingMode(selected(homingMode));
        config.setComments(comments.getText());
        return config;
    }

    public JButton getOkButton() {
        return okButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    private JPanel titleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Mono.ACCENT);
        bar.setOpaque(true);
        JLabel title = new JLabel("CREATE WEAPON CONFIGURATION", SwingConstants.CENTER);
        title.setForeground(Color.BLACK);
        title.setFont(new Font("Dialog", Font.BOLD, 14));
        title.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JButton close = new JButton("\u00D7");
        close.setFont(Mono.TITLE_FONT);
        close.setForeground(Color.BLACK);
        close.setContentAreaFilled(false);
        close.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        close.setFocusPainted(false);
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setVisible(false);
            }
        });
        bar.add(title, BorderLayout.CENTER);
        bar.add(close, BorderLayout.EAST);
        return bar;
    }

    private JPanel buttonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        Mono.paint(bar);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Mono.RULE));
        bar.add(cancelButton);
        bar.add(okButton);
        return bar;
    }

    private void scrollToTop() {
        if (formScroll != null) {
            formScroll.getVerticalScrollBar().setValue(0);
        }
    }

    private static JComboBox blankCombo(String[] items) {
        JComboBox box = Mono.combo(items);
        box.setSelectedIndex(-1);
        return box;
    }

    private static void clear(JComboBox box) {
        box.setSelectedIndex(-1);
    }

    private static String selected(JComboBox box) {
        Object item = box.getSelectedItem();
        if (item == null) {
            return "";
        }
        return item.toString();
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

    private static JScrollPane scroller(FormStack form) {
        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Mono.BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private static Block banner(String title) {
        Block bar = new Block();
        bar.setLayout(new BorderLayout());
        bar.setBackground(Mono.ACCENT);
        bar.setOpaque(true);
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Dialog", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        bar.add(label, BorderLayout.CENTER);
        return bar;
    }

    private static Block pad(JComponent child) {
        Block line = new Block();
        line.setLayout(new BorderLayout());
        line.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));
        line.add(child, BorderLayout.CENTER);
        return line;
    }

    private static Block pair(JPanel left, JPanel right) {
        Block line = new Block();
        line.setLayout(new java.awt.GridLayout(1, 2, 28, 0));
        line.add(left);
        line.add(right);
        return line;
    }

    private static JPanel labeled(String name, JComponent field) {
        JPanel line = new JPanel(new BorderLayout(8, 0));
        Mono.paint(line);
        JLabel label = new JLabel(name);
        Mono.paint(label);
        label.setPreferredSize(new Dimension(110, 24));
        line.add(label, BorderLayout.WEST);
        line.add(field, BorderLayout.CENTER);
        return line;
    }
}

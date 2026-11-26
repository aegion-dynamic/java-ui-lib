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
import page.model.AcousticHeadMode;
import page.model.Depth;
import page.model.Distance;
import page.model.GuidanceMode;
import page.model.InternalGuidanceMode;
import page.model.OnOff;
import page.model.Speed;
import page.model.TargetType;
import page.model.WeaponConfig;
import page.model.WeaponConfigState;

/**
 * Create Weapon Configuration.
 * Shown by F1 on the Eng page.
 * OK reads the fields. The page then shows the saved stage 6 screen.
 */
public class CreateWeaponConfigPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Speed[] SPEEDS = {
        new Speed(24), new Speed(30), new Speed(35), new Speed(40), new Speed(45), new Speed(50)
    };

    private final OptionGroup weapon;
    private final JTextField configName;
    private final JComboBox nameList;
    private final boolean nameIsList;
    private final String heading;
    private boolean filling;
    private final JComboBox initialSpeed;
    private final JTextField orderedSpeed;
    private final JTextField orderedDepth;
    private final JTextField searchSpeed;
    private final JTextField enbDist;
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
        this("CREATE WEAPON CONFIGURATION", false, false);
    }

    /**
     * heading is the cyan title.
     * nameIsList uses a drop-down of saved config names instead of a typed name.
     */
    protected CreateWeaponConfigPanel(String heading, boolean nameIsList) {
        this(heading, nameIsList, false);
    }

    /**
     * displayOnly locks every field except the weapon radios and the config-name drop-down.
     * OK is hidden. The name drop-down still loads a stored configuration.
     */
    protected CreateWeaponConfigPanel(String heading, boolean nameIsList, boolean displayOnly) {
        super(new BorderLayout());
        this.heading = heading;
        this.nameIsList = nameIsList;
        LookAndFeel previous = UIManager.getLookAndFeel();
        try {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception ex) {
            /* Keep the look that is already installed. */
        }
        weapon = new OptionGroup(new String[] {"EHWT WAR", "EHWT EXE"}, -1, 2);
        configName = Mono.field(18);
        nameList = Mono.combo(new String[0]);
        initialSpeed = blankCombo(SPEEDS);
        orderedSpeed = Mono.number(12);
        orderedDepth = Mono.number(12);
        searchSpeed = Mono.number(12);
        enbDist = Mono.number(8);
        udl = Mono.number(8);
        ldl = Mono.number(8);
        ash = blankCombo(OnOff.values());
        maxRd = Mono.number(8);
        acousticHeadMode = blankCombo(AcousticHeadMode.values());
        attackSonarRange = Mono.number(12);
        attackMode = blankCombo(TargetType.values());
        wakeGuidance = blankCombo(OnOff.values());
        guidanceMode = blankCombo(GuidanceMode.values());
        homingMode = blankCombo(InternalGuidanceMode.values());
        comments = Mono.area(4, 20);
        cancelButton = colored("Cancel", new Color(214, 64, 64));
        okButton = colored("OK", new Color(30, 150, 78));
        try {
            build();
            reset();
            if (displayOnly) {
                lockForDisplay();
            }
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
        if (nameIsList) {
            form.add(pad(new FormRow("Config name", nameList)));
        } else {
            form.add(pad(new FormRow("Config name", configName)));
        }
        form.add(banner("PARAMETER CONFIGURATION"));
        form.add(new GroupLabel("TORPEDO TRAJECTORY"));
        form.add(pad(new FormRow("Initial Speed", initialSpeed)));
        form.add(pad(new FormRow("Ordered Speed", orderedSpeed)));
        form.add(pad(new FormRow("Ordered Depth", orderedDepth)));
        form.add(pad(new FormRow("Search Speed", searchSpeed)));
        form.add(new GroupLabel("SAFETY PARAMETERS"));
        form.add(pad(new FormRow("Enb Dist", enbDist)));
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

    /**
     * Clear the loaded configuration and leave the weapon radios as they are.
     * Used when the backend list for the chosen type is empty.
     */
    public void clearDetails() {
        filling = true;
        if (!nameIsList) {
            configName.setText("");
        }
        clear(initialSpeed);
        orderedSpeed.setText("");
        orderedDepth.setText("");
        searchSpeed.setText("");
        enbDist.setText("500");
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
        filling = false;
    }

    /** Blank create form. Enb Dist, Max RD, and the attack sonar range keep their starting numbers. */
    public void reset() {
        weapon.setSelected("");
        if (!nameIsList) {
            configName.setText("");
        }
        clear(initialSpeed);
        orderedSpeed.setText("");
        orderedDepth.setText("");
        searchSpeed.setText("");
        enbDist.setText("500");
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
        config.setWeapon(weaponState(weapon.getSelected()));
        config.setConfigName(getConfigName().toCharArray());
        config.setInitialSpeed(knotsOf(chosen(initialSpeed)));
        config.setOrderedSpeed(knots(orderedSpeed.getText()));
        config.setOrderedDepth(metresDeep(orderedDepth.getText()));
        config.setSearchSpeed(knots(searchSpeed.getText()));
        config.setEnbDist(metres(enbDist.getText()));
        config.setUdl(metres(udl.getText()));
        config.setLdl(metres(ldl.getText()));
        config.setAsh((OnOff) chosen(ash));
        config.setMaxRd(metres(maxRd.getText()));
        config.setAcousticHeadMode((AcousticHeadMode) chosen(acousticHeadMode));
        config.setAttackSonarRange(metres(attackSonarRange.getText()));
        config.setAttackMode((TargetType) chosen(attackMode));
        config.setWakeGuidance((OnOff) chosen(wakeGuidance));
        config.setGuidanceMode((GuidanceMode) chosen(guidanceMode));
        config.setHomingMode((InternalGuidanceMode) chosen(homingMode));
        config.setComments(comments.getText());
        return config;
    }

    /** Their WeaponConfigState has no fromLabel. Match the radio text to the constant. */
    private static WeaponConfigState weaponState(String label) {
        if ("EHWT WAR".equals(label)) {
            return WeaponConfigState.EHWT_WAR;
        }
        if ("EHWT EXE".equals(label)) {
            return WeaponConfigState.EHWT_EXE;
        }
        return null;
    }

    /** EHWT WAR, EHWT EXE, or null when neither radio is selected. */
    public WeaponConfigState getWeaponState() {
        return weaponState(weapon.getSelected());
    }

    /** Runs when the user clicks EHWT WAR or EHWT EXE. Not used by Create. */
    public void addWeaponListener(ActionListener listener) {
        weapon.addListener(listener);
    }

    public String getConfigName() {
        if (nameIsList) {
            return selected(nameList);
        }
        return configName.getText();
    }

    /** Saved config names. The first name is selected. */
    public void setNames(String[] names) {
        filling = true;
        nameList.removeAllItems();
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                nameList.addItem(names[i]);
            }
        }
        filling = false;
        if (names != null && names.length > 0) {
            nameList.setSelectedIndex(0);
        }
    }

    public String getSelectedName() {
        return selected(nameList);
    }

    public void addNameListener(ActionListener listener) {
        nameList.addActionListener(listener);
    }

    public boolean isFilling() {
        return filling;
    }

    /** Fill every control from a saved weapon configuration. */
    public void apply(WeaponConfig config) {
        filling = true;
        weapon.setSelected(written(config.getWeapon()));
        if (nameIsList) {
            nameList.setSelectedItem(config.configNameText());
        } else {
            configName.setText(config.configNameText());
        }
        showChoice(initialSpeed, config.toInitialSpeed());
        orderedSpeed.setText(written(config.toOrderedSpeed()));
        orderedDepth.setText(written(config.toOrderedDepth()));
        searchSpeed.setText(written(config.toSearchSpeed()));
        enbDist.setText(written(config.toEnbDist()));
        udl.setText(written(config.toUdl()));
        ldl.setText(written(config.toLdl()));
        showChoice(ash, config.getAsh());
        maxRd.setText(written(config.toMaxRd()));
        showChoice(acousticHeadMode, config.getAcousticHeadMode());
        attackSonarRange.setText(written(config.toSonarRange()));
        showChoice(attackMode, config.getAttackMode());
        showChoice(wakeGuidance, config.getWakeGuidance());
        showChoice(guidanceMode, config.getGuidanceMode());
        showChoice(homingMode, config.getHomingMode());
        comments.setText(config.getComments());
        filling = false;
        scrollToTop();
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
        JLabel title = new JLabel(heading, SwingConstants.CENTER);
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

    private void lockForDisplay() {
        lockField(orderedSpeed);
        lockField(orderedDepth);
        lockField(searchSpeed);
        lockField(enbDist);
        lockField(udl);
        lockField(ldl);
        lockField(maxRd);
        lockField(attackSonarRange);
        comments.setEditable(false);
        lockCombo(initialSpeed);
        lockCombo(ash);
        lockCombo(acousticHeadMode);
        lockCombo(attackMode);
        lockCombo(wakeGuidance);
        lockCombo(guidanceMode);
        lockCombo(homingMode);
        okButton.setVisible(false);
    }

    private static void lockField(JTextField field) {
        field.setEditable(false);
    }

    private static void lockCombo(JComboBox box) {
        box.setEnabled(false);
    }

    private static JComboBox blankCombo(Object[] items) {
        JComboBox box = Mono.combo(items);
        box.setSelectedIndex(-1);
        return box;
    }

    private static void clear(JComboBox box) {
        box.setSelectedIndex(-1);
    }

    private static void showChoice(JComboBox box, Object value) {
        if (value == null || "".equals(value)) {
            box.setSelectedIndex(-1);
            return;
        }
        for (int i = 0; i < box.getItemCount(); i++) {
            Object item = box.getItemAt(i);
            if (item != null && (value.equals(item)
                    || words(value.toString()).equalsIgnoreCase(words(item.toString())))) {
                box.setSelectedIndex(i);
                return;
            }
        }
        box.setSelectedIndex(-1);
    }

    private static String words(String text) {
        if (text.indexOf('_') < 0 && !constantName(text)) {
            return text;
        }
        String[] parts = text.split("_");
        StringBuffer joined = new StringBuffer();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                joined.append(' ');
            }
            if (parts[i].length() == 0) {
                continue;
            }
            String lower = parts[i].toLowerCase();
            joined.append(Character.toUpperCase(lower.charAt(0)));
            joined.append(lower.substring(1));
        }
        return joined.toString();
    }

    private static boolean constantName(String text) {
        if (text.length() == 0) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'a' && c <= 'z') {
                return false;
            }
        }
        return true;
    }

    private static Object chosen(JComboBox box) {
        return box.getSelectedItem();
    }

    private static Double knotsOf(Object item) {
        if (!(item instanceof Speed)) {
            return null;
        }
        return new Double(((Speed) item).getKnots());
    }

    private static Double knots(String text) {
        Speed speed = Speed.parse(text);
        return speed == null ? null : new Double(speed.getKnots());
    }

    private static Double metresDeep(String text) {
        Depth depth = Depth.parse(text);
        return depth == null ? null : new Double(depth.getMeters());
    }

    private static Float metres(String text) {
        Distance distance = Distance.parse(text);
        return distance == null ? null : new Float(distance.getMeters());
    }

    private static String written(Object value) {
        return value == null ? "" : value.toString();
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

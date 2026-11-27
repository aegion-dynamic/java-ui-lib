package page;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
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
import page.model.GuidanceMode;
import page.model.InternalGuidanceMode;
import page.model.OnOff;
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

    private static final String[] SPEEDS = {"24", "30", "35", "40", "45", "50"};

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
        config.setInitialSpeed(decimal(selected(initialSpeed)));
        config.setOrderedSpeed(decimal(orderedSpeed.getText()));
        config.setOrderedDepth(decimal(orderedDepth.getText()));
        config.setSearchSpeed(decimal(searchSpeed.getText()));
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
        if (config == null) {
            return;
        }
        filling = true;
        weapon.setSelected(choiceText(config.getWeapon()));
        String savedName = nameText(config.getConfigName());
        if (nameIsList) {
            nameList.setSelectedItem(savedName);
        } else {
            configName.setText(savedName);
        }
        showChoice(initialSpeed, knotsOf(config.getInitialSpeed()));
        orderedSpeed.setText(numberText(config.getOrderedSpeed()));
        orderedDepth.setText(numberText(config.getOrderedDepth()));
        searchSpeed.setText(numberText(config.getSearchSpeed()));
        enbDist.setText(numberText(config.getEnbDist()));
        udl.setText(numberText(config.getUdl()));
        ldl.setText(numberText(config.getLdl()));
        showChoice(ash, config.getAsh());
        maxRd.setText(numberText(config.getMaxRd()));
        showChoice(acousticHeadMode, config.getAcousticHeadMode());
        attackSonarRange.setText(numberText(config.getAttackSonarRange()));
        showChoice(attackMode, config.getAttackMode());
        showChoice(wakeGuidance, config.getWakeGuidance());
        showChoice(guidanceMode, config.getGuidanceMode());
        showChoice(homingMode, config.getHomingMode());
        String note = config.getComments();
        comments.setText(note == null ? "" : note);
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

    /** char[] from getConfigName. Their struct has no configNameText. */
    private static String nameText(char[] name) {
        return name == null ? "" : new String(name);
    }

    /**
     * A number for a text box.
     * A Double or Float is written as it is.
     * A Speed, Depth, or Distance is written from the number in its text.
     * Null, or text with no number, leaves the box blank. It does not throw.
     */
    private static String numberText(Object number) {
        Double value = knotsOf(number);
        if (value == null) {
            return "";
        }
        DecimalFormat format = new DecimalFormat("0.##");
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        return format.format(value.doubleValue());
    }

    /** Enum.name(), so EHWT_WAR selects the EHWT WAR radio. */
    private static String choiceText(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Enum) {
            return ((Enum) value).name();
        }
        return String.valueOf(value);
    }

    private static void showChoice(JComboBox box, Object value) {
        if (value == null || "".equals(value)) {
            box.setSelectedIndex(-1);
            return;
        }
        for (int i = 0; i < box.getItemCount(); i++) {
            Object item = box.getItemAt(i);
            if (item != null && sameChoice(value, item)) {
                box.setSelectedIndex(i);
                return;
            }
        }
        box.setSelectedIndex(-1);
    }

    private static boolean sameChoice(Object value, Object item) {
        if (value.equals(item)) {
            return true;
        }
        if (sameNumber(value, item)) {
            return true;
        }
        String[] left = keys(value);
        String[] right = keys(item);
        for (int a = 0; a < left.length; a++) {
            for (int b = 0; b < right.length; b++) {
                if (left[a].length() > 0 && left[a].equals(right[b])) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 24 from getInitialSpeed matches the Speed row whose text is 24. */
    private static boolean sameNumber(Object value, Object item) {
        Double left = asNumber(value);
        Double right = asNumber(item);
        if (left == null || right == null) {
            return false;
        }
        return Double.doubleToLongBits(left.doubleValue()) == Double.doubleToLongBits(right.doubleValue());
    }

    private static Double asNumber(Object value) {
        if (value instanceof Number) {
            return new Double(((Number) value).doubleValue());
        }
        if (value == null) {
            return null;
        }
        return decimal(String.valueOf(value));
    }

    private static String[] keys(Object value) {
        String shown = letters(String.valueOf(value));
        if (value instanceof Enum) {
            String constant = letters(((Enum) value).name());
            if (!constant.equals(shown)) {
                return new String[] {constant, shown};
            }
            return new String[] {constant};
        }
        return new String[] {shown};
    }

    private static String letters(String text) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ' || c == '_' || c == '-') {
                continue;
            }
            out.append(Character.toLowerCase(c));
        }
        return out.toString();
    }

    private static Object chosen(JComboBox box) {
        return box.getSelectedItem();
    }

    /** The first number in the text. "24", "24.5", and "24 kn" all become a decimal. */
    private static Double decimal(String text) {
        if (text == null) {
            return null;
        }
        String trimmed = text.trim();
        int start = -1;
        int end = trimmed.length();
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if ((c >= '0' && c <= '9') || c == '.') {
                if (start < 0) {
                    start = i;
                }
            } else if (start >= 0) {
                end = i;
                break;
            }
        }
        if (start < 0) {
            return null;
        }
        if (start > 0 && trimmed.charAt(start - 1) == '-') {
            start = start - 1;
        }
        try {
            return new Double(trimmed.substring(start, end));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Knots from their getter.
     * A number is kept. A Speed object is read from its text, so getKnots is not required.
     */
    private static Double knotsOf(Object speed) {
        if (speed instanceof Number) {
            return new Double(((Number) speed).doubleValue());
        }
        if (speed == null) {
            return null;
        }
        return decimal(String.valueOf(speed));
    }

    private static Float metres(String text) {
        Double value = decimal(text);
        return value == null ? null : new Float(value.floatValue());
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

package page;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import page.form.Block;
import page.form.FormRow;
import page.form.FormStack;
import page.form.GroupLabel;
import page.form.Mono;
import page.form.OptionGroup;
import page.model.AcousticHeadMode;
import page.model.GuidanceMode;
import page.model.OnOff;
import page.model.TargetType;
import page.model.Engagement;
import page.model.WeaponConfig;
import page.model.WeaponConfigState;

/**
 * Engagement Parameters. This is the screen shown when the window opens.
 * ASH is ON or OFF. Acoustic head mode is Passive, Simultaneous, or Wake.
 * Attack mode and target Type are TargetType. Wake Sensor is GuidanceMode.
 * Guidance Mode is InternalGuidanceMode.
 */
public class EngagementParametersPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JComboBox engm;
    private final JTextField tube;
    private final JTextField engmType;
    private final OptionGroup weapon;
    private final JTextField configName;
    private final JTextField initialSpeed;
    private final JTextField orderedSpeed;
    private final JTextField orderedDepth;
    private final JTextField searchSpeed;
    private final JTextField enbDist;
    private final JTextField safDist;
    private final JTextField udl;
    private final JTextField ldl;
    private final JTextField safetySonarRange;
    private final JComboBox ash;
    private final JTextField maxRd;
    private final JComboBox acousticHeadMode;
    private final JComboBox attackMode;
    private final JComboBox wakeSensor;
    private final JComboBox guidanceMode;
    private final JComboBox type;
    private final JTextField tb;
    private final JTextField rb;
    private final JTextField dp;
    private final JTextField lat;
    private final JTextField lng;
    private final JButton cancelButton;
    private boolean filling;
    private JScrollPane formScroll;

    public EngagementParametersPanel() {
        this(false);
    }

    /**
     * displayOnly is the list of engagements.
     * ENGM stays a drop-down of stored names. Every other field only displays.
     */
    protected EngagementParametersPanel(boolean displayOnly) {
        super(new BorderLayout());
        Mono.paint(this);

        engm = Mono.combo(new String[] {""});
        tube = Mono.number(8);
        engmType = Mono.field(16);
        weapon = new OptionGroup(new String[] {"EHWT WAR", "EHWT EXE"}, 0, 2);
        configName = Mono.field(16);
        initialSpeed = Mono.number(12);
        orderedSpeed = Mono.number(12);
        orderedDepth = Mono.number(12);
        searchSpeed = Mono.number(12);
        enbDist = Mono.number(8);
        safDist = Mono.number(8);
        udl = Mono.number(8);
        ldl = Mono.number(8);
        safetySonarRange = Mono.number(8);
        ash = Mono.combo(OnOff.values());
        ash.setSelectedIndex(-1);
        maxRd = Mono.number(12);
        acousticHeadMode = Mono.combo(AcousticHeadMode.values());
        attackMode = Mono.combo(TargetType.values());
        wakeSensor = Mono.combo(OnOff.values());
        guidanceMode = Mono.combo(new GuidanceMode[] {
            GuidanceMode.BG_RIDER_OUT,
            GuidanceMode.CCM,
            GuidanceMode.TCCM
        });
        type = Mono.combo(TargetType.values());
        type.setSelectedIndex(-1);
        tb = Mono.number(8);
        rb = Mono.number(8);
        dp = Mono.number(8);
        lat = Mono.number(8);
        lng = Mono.number(8);
        cancelButton = Mono.button("Cancel");

        formScroll = scroller(form());
        add(formScroll, BorderLayout.CENTER);
        if (displayOnly) {
            lockForDisplay(formScroll);
            JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
            Mono.paint(bar);
            bar.add(cancelButton);
            add(bar, BorderLayout.SOUTH);
        }
    }

    private FormStack form() {
        FormStack form = new FormStack();
        form.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        form.add(banner("ENGAGEMENT PARAMETERS"));
        form.add(pad(pair(labeled("ENGM", engm), labeled("Tube", tube))));
        form.add(pad(new FormRow("ENGM Type", engmType)));

        form.add(banner("WEAPON CONFIGURATION"));
        form.add(pad(weapon));
        form.add(pad(new FormRow("CONFIG NAME", configName)));

        form.add(banner("PARAMETER CONFIGURATION"));
        form.add(new GroupLabel("TORPEDO TRAJECTORY"));
        form.add(pad(new FormRow("Initial Speed", initialSpeed)));
        form.add(pad(new FormRow("Ordered Speed", orderedSpeed)));
        form.add(pad(new FormRow("Ordered Depth", orderedDepth)));
        form.add(pad(new FormRow("Search Speed", searchSpeed)));

        form.add(new GroupLabel("SAFETY PARAMETERS"));
        form.add(pad(pair(labeled("Enab dist (m)", enbDist), labeled("Saf dist (m)", safDist))));
        form.add(pad(pair(labeled("UDL (M)", udl), labeled("LDL (m)", ldl))));
        form.add(pad(pair(labeled("Sonar Range", safetySonarRange), labeled("ASH", ash))));
        form.add(pad(new FormRow("MAX DIST", maxRd)));

        form.add(pad(pair(labeled("WCse", Mono.number(8)), labeled("Prog CSe", Mono.number(8)))));
        form.add(pad(pair(labeled("ITA", Mono.number(8)), labeled("Sonar Range", Mono.number(8)))));
        form.add(pad(pair(labeled("Wake EAl", Mono.number(8)), labeled("WAKE EAL", Mono.number(8)))));
        form.add(pad(pair(labeled("RT", Mono.number(8)), labeled("MAX. RT", Mono.number(8)))));
        form.add(pad(pair(labeled("RT", Mono.field(8)), labeled("MAX. RT", Mono.field(8)))));

        form.add(new GroupLabel("ATTACK PARAMETERS"));
        form.add(pad(new FormRow("Acoustic head mode", acousticHeadMode)));
        form.add(pad(new FormRow("Attack mode", attackMode)));
        form.add(pad(new FormRow("Wake Sensor", wakeSensor)));
        form.add(pad(new FormRow("Guidance Mode", guidanceMode)));

        form.add(pad(pair(labeled("TRACK", Mono.number(8)), labeled("RB", rb))));
        form.add(pad(pair(labeled("TB", tb), labeled("Dp", dp))));
        form.add(pad(pair(labeled("TR", Mono.number(8)), labeled("TCo", Mono.number(8)))));
        form.add(pad(pair(labeled("TSp", Mono.number(8)), labeled("Type", type))));
        form.add(pad(pair(labeled("ATB", Mono.number(8)), labeled("Lat", lat))));
        form.add(pad(pair(labeled("Lng", lng), empty())));
        form.add(pad(pair(labeled("Lng", Mono.field(8)), empty())));
        return form;
    }

    /**
     * Copy a weapon configuration onto this screen.
     * Uses only their getters. The struct has no to-methods and no configNameText.
     * Numbers are written as text. Enums are matched to the words already in each box.
     */
    public void applyWeapon(WeaponConfig config) {
        if (config == null) {
            return;
        }
        weapon.setSelected(choiceText(config.getWeapon()));
        configName.setText(nameText(config.getConfigName()));
        text(initialSpeed, numberText(config.getInitialSpeed()));
        text(orderedSpeed, numberText(config.getOrderedSpeed()));
        text(orderedDepth, numberText(config.getOrderedDepth()));
        text(searchSpeed, numberText(config.getSearchSpeed()));
        text(enbDist, numberText(config.getEnbDist()));
        text(udl, numberText(config.getUdl()));
        text(ldl, numberText(config.getLdl()));
        text(safetySonarRange, numberText(config.getAttackSonarRange()));
        choose(ash, config.getAsh());
        text(maxRd, numberText(config.getMaxRd()));
        choose(acousticHeadMode, config.getAcousticHeadMode());
        choose(attackMode, config.getAttackMode());
        choose(wakeSensor, config.getWakeGuidance());
        choose(guidanceMode, config.getGuidanceMode());
    }
    /** Names already stored. ENGM lists them. The first name is selected. */
    public void setNames(String[] names) {
        filling = true;
        engm.removeAllItems();
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                engm.addItem(names[i]);
            }
        }
        filling = false;
        if (names != null && names.length > 0) {
            engm.setSelectedIndex(0);
        }
    }

    public String getSelectedName() {
        Object item = engm.getSelectedItem();
        return item == null ? "" : item.toString();
    }

    public void addNameListener(ActionListener listener) {
        engm.addActionListener(listener);
    }

    public boolean isFilling() {
        return filling;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    /** Fill this screen from a stored engagement. The layout stays Engagement Parameters. */
    public void apply(Engagement engagement) {
        filling = true;
        engm.setSelectedItem(engagement.getField2());
        engmType.setText(textOf(engagement.getGroup1()));
        text(tube, numeric(engagement.getField3()));
        configName.setText(textOf(engagement.getGroup5()));
        text(tb, numeric(engagement.getP1()));
        text(rb, numeric(engagement.getP2()));
        text(dp, numeric(engagement.getP4()));
        text(lat, numeric(engagement.getP9()));
        text(lng, numeric(engagement.getP8()));
        filling = false;
        if (formScroll != null) {
            formScroll.getVerticalScrollBar().setValue(0);
        }
    }

    private void lockForDisplay(Container parent) {
        java.awt.Component[] kids = parent.getComponents();
        for (int i = 0; i < kids.length; i++) {
            java.awt.Component kid = kids[i];
            if (kid == engm) {
                continue;
            }
            if (kid instanceof JTextField) {
                ((JTextField) kid).setEditable(false);
            } else if (kid instanceof JComboBox) {
                kid.setEnabled(false);
            } else if (kid instanceof JRadioButton) {
                kid.setEnabled(false);
            }
            if (kid instanceof Container) {
                lockForDisplay((Container) kid);
            }
        }
    }

    private static String numeric(String value) {
        if (value == null || !value.matches("[0-9]*\\.?[0-9]*")) {
            return "";
        }
        return value;
    }

    public WeaponConfigState getWeapon() {
        String label = weapon.getSelected();
        if ("EHWT WAR".equals(label)) {
            return WeaponConfigState.EHWT_WAR;
        }
        if ("EHWT EXE".equals(label)) {
            return WeaponConfigState.EHWT_EXE;
        }
        return null;
    }
    /** The number in Initial Speed. Their Speed is built from this decimal, not from Speed.parse. */
    public Double getInitialSpeed() {
        return decimal(initialSpeed.getText());
    }

    /** The number in Ordered Speed. */
    public Double getOrderedSpeed() {
        return decimal(orderedSpeed.getText());
    }

    public Double getOrderedDepth() {
        return decimal(orderedDepth.getText());
    }

    public Double getSearchSpeed() {
        return decimal(searchSpeed.getText());
    }

    public Float getEnbDist() {
        return metres(enbDist.getText());
    }

    public Float getSafDist() {
        return metres(safDist.getText());
    }

    public Float getUdl() {
        return metres(udl.getText());
    }

    public Float getLdl() {
        return metres(ldl.getText());
    }

    public Float getSonarRange() {
        return metres(safetySonarRange.getText());
    }

    public Float getMaxRd() {
        return metres(maxRd.getText());
    }

    public OnOff getAsh() {
        return (OnOff) chosen(ash);
    }

    public AcousticHeadMode getAcousticHeadMode() {
        return (AcousticHeadMode) chosen(acousticHeadMode);
    }

    public TargetType getAttackMode() {
        return (TargetType) chosen(attackMode);
    }

    public OnOff getWakeSensor() {
        return (OnOff) chosen(wakeSensor);
    }

    public GuidanceMode getGuidanceMode() {
        return (GuidanceMode) chosen(guidanceMode);
    }

    public TargetType getTargetType() {
        return (TargetType) chosen(type);
    }
    /**
     * The first number in the text. "24", "24.5", and "24 kn" all become 24 or 24.5.
     * A blank box, or text with no number, is null.
     */
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
     * A Double is used as it is. A Speed object is read from its text, so getKnots is not required.
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


    private static void text(JTextField field, String value) {
        field.setText(textOf(value));
    }

    private static String textOf(String value) {
        return value == null ? "" : value;
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

    /** Enum.name() when they have one, so EHWT_WAR can select the EHWT WAR radio. */
    private static String choiceText(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Enum) {
            return ((Enum) value).name();
        }
        return String.valueOf(value);
    }

    /**
     * Select the row for a getter value.
     * Their boxes hold words ("Passive", "Sub Surface", "ON", "Bg Rider Out").
     * The getter returns the enum. setSelectedItem leaves the box blank.
     * PASSIVE matches Passive. BG_RIDER_OUT matches Bg Rider Out. ON matches ON.
     */
    private static void choose(JComboBox box, Object value) {
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

    /** The constant name and toString, with spaces and underscores removed. */
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

    private static JScrollPane scroller(FormStack form) {
        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Mono.BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    /** Cyan bar with the section name centred in black. */
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
        line.setLayout(new GridLayout(1, 2, 28, 0));
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

    private static JPanel empty() {
        JPanel blank = new JPanel();
        Mono.paint(blank);
        return blank;
    }
}

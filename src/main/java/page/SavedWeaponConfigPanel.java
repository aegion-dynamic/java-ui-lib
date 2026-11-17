package page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import page.form.Block;
import page.form.FormRow;
import page.form.FormStack;
import page.form.GroupLabel;
import page.form.Mono;
import page.form.OptionGroup;
import page.model.WeaponConfig;

/**
 * Stage 6. The saved weapon configuration on the engagement screen.
 * The weapon fields are filled from Create Weapon Configuration.
 * Engm, the CAL block, and the target block belong to this screen.
 */
public class SavedWeaponConfigPanel extends JPanel {

    private static final long serialVersionUID = 1L;

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

    public SavedWeaponConfigPanel() {
        super(new BorderLayout());
        Mono.paint(this);

        weapon = new OptionGroup(new String[] {"EHWT WAR", "EHWT EXE"}, -1, 2);
        configName = Mono.field(16);
        initialSpeed = Mono.combo(new String[] {"24", "30", "35", "40", "45", "50"});
        orderedSpeed = Mono.field(8);
        orderedDepth = Mono.field(8);
        searchSpeed = Mono.field(8);
        enbDist = Mono.field(8);
        safetySonarRange = Mono.field(8);
        udl = Mono.field(8);
        ldl = Mono.field(8);
        ash = Mono.combo(new String[] {"ON", "OFF"});
        maxRd = Mono.field(8);
        acousticHeadMode = Mono.combo(new String[] {"Simultaneous", "Wake"});
        attackSonarRange = Mono.field(8);
        attackMode = Mono.combo(new String[] {"Sub Surface", "Surface"});
        wakeGuidance = Mono.combo(new String[] {"ON", "OFF"});
        guidanceMode = Mono.combo(new String[] {"Bg Rider Out", "CCM", "TCCM", "Self Guided"});
        homingMode = Mono.combo(new String[] {"Pro Navigation", "Intercept"});
        clear(initialSpeed);
        clear(ash);
        clear(acousticHeadMode);
        clear(attackMode);
        clear(wakeGuidance);
        clear(guidanceMode);
        clear(homingMode);

        add(scroller(form()), BorderLayout.CENTER);
    }

    /** Copy the weapon configuration onto this screen. The other sections stay. */
    public void apply(WeaponConfig config) {
        weapon.setSelected(config.getWeapon());
        configName.setText(config.getConfigName());
        choose(initialSpeed, config.getInitialSpeed());
        orderedSpeed.setText(config.getOrderedSpeed());
        orderedDepth.setText(config.getOrderedDepth());
        searchSpeed.setText(config.getSearchSpeed());
        enbDist.setText(config.getEnbDist());
        safetySonarRange.setText(config.getSafetySonarRange());
        udl.setText(config.getUdl());
        ldl.setText(config.getLdl());
        choose(ash, config.getAsh());
        maxRd.setText(config.getMaxRd());
        choose(acousticHeadMode, config.getAcousticHeadMode());
        attackSonarRange.setText(config.getAttackSonarRange());
        choose(attackMode, config.getAttackMode());
        choose(wakeGuidance, config.getWakeGuidance());
        choose(guidanceMode, config.getGuidanceMode());
        choose(homingMode, config.getHomingMode());
    }

    private FormStack form() {
        FormStack form = new FormStack();
        form.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JComboBox engm = Mono.combo(new String[] {"1", "2", "3"});
        JTextField tube = Mono.field(8);
        tube.setText("1");
        JComboBox engmType = Mono.combo(new String[] {"Prosp Eng", "Real Engine"});

        form.add(banner("ENGAGEMENT PARAMETERS"));
        form.add(pad(pair(labeled("Engm", engm), labeled("Tube", tube))));
        form.add(pad(new FormRow("Engm Type", engmType)));

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
        form.add(pad(pair(labeled("Enb Dist", enbDist), labeled("Sonar Range", safetySonarRange))));
        form.add(pad(pair(labeled("UDL", udl), labeled("LDL", ldl))));
        form.add(pad(pair(labeled("ASH", ash), labeled("Max RD", maxRd))));

        form.add(new GroupLabel("CAL ENGM PARAMETERS"));
        form.add(pad(pair(labeled("WCre", text("0.00")), labeled("Prog Cre", text("0.00")))));
        form.add(pad(pair(labeled("ETA", text("-98.21")), labeled("Hit Range", text("0.53")))));
        form.add(pad(pair(labeled("Wake EAl", text("0.00")), labeled("Wake EAL", text("0.00")))));
        form.add(pad(pair(labeled("RT", text("+0+0+0")), labeled("Max RT", text("01:00:00")))));

        form.add(new GroupLabel("ATTACK PARAMETERS"));
        form.add(pad(new FormRow("Acoustic Head Mode", acousticHeadMode)));
        form.add(pad(new FormRow("Sonar Range", attackSonarRange)));
        form.add(pad(new FormRow("Attack Mode", attackMode)));
        form.add(pad(new FormRow("Wake Guidance", wakeGuidance)));
        form.add(pad(new FormRow("Guidance Mode", guidanceMode)));
        form.add(pad(new FormRow("Homing Mode", homingMode)));

        JComboBox type = Mono.combo(new String[] {"Surface", "Sub Surface"});
        JTextField lat = Mono.field(12);
        lat.setText("00\u00B0 01' 46\" N");

        form.add(banner("TARGET PARAMETERS"));
        form.add(pad(pair(labeled("Track", Mono.field(8)), labeled("RB", Mono.field(8)))));
        form.add(pad(pair(labeled("TB", Mono.field(8)), labeled("Dp", Mono.field(8)))));
        form.add(pad(pair(labeled("TR", Mono.field(8)), labeled("TCa", Mono.field(8)))));
        form.add(pad(pair(labeled("TSp", Mono.field(8)), labeled("Type", type))));
        form.add(pad(pair(labeled("ATB", Mono.field(8)), labeled("Lat", lat))));
        form.add(pad(pair(labeled("Lng", Mono.field(8)), empty())));
        return form;
    }

    private static JTextField text(String value) {
        JTextField field = Mono.field(8);
        field.setText(value);
        return field;
    }

    private static void clear(JComboBox box) {
        box.setSelectedIndex(-1);
    }

    private static void choose(JComboBox box, String value) {
        if (value == null || value.length() == 0) {
            box.setSelectedIndex(-1);
            return;
        }
        box.setSelectedItem(value);
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

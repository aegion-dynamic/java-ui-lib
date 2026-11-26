package page;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import page.form.Mono;

/**
 * Wire guidance view. One panel, so the window can show this view or replace it.
 * The note, the Depth Window, and each drop-down body can be replaced on their own.
 * COG/SOG is not built here. The window places that shared strip in {@link #placeReadout}
 * while this view is showing.
 */
public class WcPage extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color DARK = new Color(28, 28, 30);
    private static final Color GREEN = new Color(28, 148, 52);
    private static final Color RED = new Color(196, 32, 40);
    private static final Color CYAN = new Color(0, 206, 209);
    private static final Color INK = new Color(16, 18, 20);
    private static final Color PURPLE = new Color(198, 140, 226);
    private static final Color LABEL_BG = new Color(58, 58, 62);
    private static final Color OUTPUT_BG = new Color(176, 176, 176);
    private static final Color GRID = new Color(96, 96, 100);
    private static final Color BOX_LINE = new Color(120, 120, 124);
    private static final Color SEND = new Color(32, 148, 232);
    private static final int ROW = 26;
    private static final int PARAM_ROW = 22;

    private JComponent leftPanel;
    private JComponent notePanel;
    private JComponent depthPanel;
    private JComponent sections;
    private JComponent gaugesBody;
    private JComponent torpedoParametersBody;
    private JComponent demonBody;
    private JComponent distanceBody;
    private final JPanel readoutSlot = new JPanel(new BorderLayout());
    private final JPanel rightColumn = new JPanel(new BorderLayout(0, 4));
    private DepthGraph depthGraph;
    private JLabel trajectoryTitle;
    private JComponent trajectoryBlock;
    private CardLayout trajectoryCards;
    private JPanel trajectoryDeck;
    private JComboBox trajectoryTubes;
    private boolean choosingTube;

    public WcPage() {
        super(new BorderLayout(4, 0));
        setBackground(DARK);
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        readoutSlot.setOpaque(false);
        rightColumn.setOpaque(false);
        rightColumn.setPreferredSize(new Dimension(300, 100));
        rightColumn.add(readoutSlot, BorderLayout.NORTH);
        sections = side();
        rightColumn.add(sections, BorderLayout.CENTER);
        leftPanel = body();
        add(leftPanel, BorderLayout.CENTER);
        add(rightColumn, BorderLayout.EAST);
    }

    /** The note and the Depth Window. */
    public JComponent getBody() {
        return leftPanel;
    }

    /** The five drop-downs. COG and SOG are the strip above this, when the view is showing. */
    public JComponent getSide() {
        return sections;
    }

    /** Put the shared COG/SOG strip at the top of this view's right column. */
    public void placeReadout(MotionReadout readout) {
        readoutSlot.removeAll();
        if (readout != null) {
            readoutSlot.add(readout, BorderLayout.CENTER);
        }
        readoutSlot.revalidate();
        readoutSlot.repaint();
    }

    /** Take the shared COG/SOG strip back. The slot is left empty. */
    public MotionReadout takeReadout() {
        if (readoutSlot.getComponentCount() == 0) {
            return null;
        }
        MotionReadout readout = (MotionReadout) readoutSlot.getComponent(0);
        readoutSlot.remove(readout);
        readoutSlot.revalidate();
        return readout;
    }

    /** Replace the left half. The right column stays. */
    public void setLeft(JComponent next) {
        remove(leftPanel);
        leftPanel = next;
        add(leftPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /** Replace the five drop-downs. The COG/SOG slot stays. */
    public void setSide(JComponent next) {
        rightColumn.remove(sections);
        sections = next;
        rightColumn.add(sections, BorderLayout.CENTER);
        rightColumn.revalidate();
        rightColumn.repaint();
    }

    /** Replace the purple note. */
    public void setNote(JComponent next) {
        ((Container) leftPanel).remove(notePanel);
        notePanel = next;
        leftPanel.add(notePanel, BorderLayout.CENTER);
        leftPanel.revalidate();
        leftPanel.repaint();
    }

    /** Replace the Depth Window. Later depth samples go to the graph this view built. */
    public void setDepthWindow(JComponent next) {
        ((Container) leftPanel).remove(depthPanel);
        depthPanel = next;
        leftPanel.add(depthPanel, BorderLayout.SOUTH);
        leftPanel.revalidate();
        leftPanel.repaint();
    }

    /** Replace the open body under Gauges. The cyan bar stays. */
    public void setGauges(JComponent next) {
        gaugesBody = swap(gaugesBody, next);
    }

    /** Replace the body under Torpedo Trajectory. The cyan bar stays. */
    public void setTrajectory(JComponent next) {
        trajectoryBlock = swap(trajectoryBlock, next);
    }

    /** Replace the body under Torpedo Parameters. The cyan bar stays. */
    public void setTorpedoParameters(JComponent next) {
        torpedoParametersBody = swap(torpedoParametersBody, next);
    }

    /** Replace the body under Demon Spectrum. The cyan bar stays. */
    public void setDemonSpectrum(JComponent next) {
        demonBody = swap(demonBody, next);
    }

    /** Replace the body under LL Torpedo Distance. The cyan bar stays. */
    public void setLlTorpedoDistance(JComponent next) {
        distanceBody = swap(distanceBody, next);
    }

    private JComponent swap(JComponent current, JComponent next) {
        Container parent = current.getParent();
        int index = 0;
        for (int i = 0; i < parent.getComponentCount(); i++) {
            if (parent.getComponent(i) == current) {
                index = i;
                break;
            }
        }
        boolean open = current.isVisible();
        parent.remove(index);
        next.setVisible(open);
        next.setAlignmentX(0f);
        parent.add(next, index);
        parent.revalidate();
        parent.repaint();
        return next;
    }

    private JPanel body() {
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(DARK);
        left.setOpaque(true);
        notePanel = note();
        depthPanel = depthWindow();
        left.add(notePanel, BorderLayout.CENTER);
        left.add(depthPanel, BorderLayout.SOUTH);
        return left;
    }

    private JPanel note() {
        JPanel area = new JPanel(new GridBagLayout());
        area.setBackground(DARK);
        area.setOpaque(true);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PURPLE);
        card.setOpaque(true);
        card.setPreferredSize(new Dimension(360, 190));
        JLabel text = new JLabel(
            "<html><center>"
                + "On Click F1 - Guid mode the bottom<br>navigation bar changes .<br><br>"
                + "And to get back to the original Navigation<br>bar , they must click again on the F1 Guid<br>Mode button.<br><br>"
                + "These F1 to F6 are automatic algorithms ,<br>they are applied automatically."
                + "</center></html>");
        text.setForeground(Color.BLACK);
        text.setFont(new Font("Dialog", Font.PLAIN, 13));
        text.setHorizontalAlignment(SwingConstants.CENTER);
        text.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.add(text, BorderLayout.CENTER);
        area.add(card);
        return area;
    }

    private JPanel depthWindow() {
        JPanel depth = new JPanel(new BorderLayout());
        depth.setBackground(new Color(18, 18, 20));
        depth.setPreferredSize(new Dimension(100, 300));
        depth.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(70, 70, 74)));
        JLabel title = new JLabel("Depth Window");
        title.setForeground(new Color(220, 220, 220));
        title.setFont(new Font("Dialog", Font.PLAIN, 16));
        title.setBorder(BorderFactory.createEmptyBorder(6, 12, 0, 12));
        depth.add(title, BorderLayout.NORTH);
        depthGraph = new DepthGraph();
        depth.add(depthGraph, BorderLayout.CENTER);
        loadDummyDepth();
        return depth;
    }

    /** Temporary line so the mouse cross has a curve to sit on. Delete when real samples are wired. */
    private void loadDummyDepth() {
        int count = 70;
        double[] x = new double[count];
        double[] y = new double[count];
        for (int i = 0; i < count; i++) {
            double at = i * 40.0 / (count - 1);
            x[i] = at;
            double rise = at < 2.0 ? 0.0 : (1.0 - Math.exp(-(at - 2.0) / 3.5));
            y[i] = 8.7 * rise;
        }
        depthGraph.setBulk(x, y);
    }

    /** Replace the Depth Window line. The mouse still owns the cross. */
    public void setDepthBulk(double[] x, double[] y) {
        depthGraph.setBulk(x, y);
    }

    /** Append one Depth Window sample. The mouse still owns the cross. */
    public void addDepthPoint(double x, double y) {
        depthGraph.addPoint(x, y);
    }

    /** Clear the Depth Window line. The axes stay. */
    public void clearDepth() {
        depthGraph.clear();
    }

    private JPanel side() {
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBackground(DARK);
        column.setOpaque(true);
        column.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));

        gaugesBody = gaugesBody();
        column.add(section("Gauges", gaugesBody, true));
        column.add(gap());
        column.add(trajectorySection());
        column.add(gap());
        torpedoParametersBody = torpedoParameters();
        column.add(section("Torpedo Parameters", torpedoParametersBody, false));
        column.add(gap());
        demonBody = blankBody();
        column.add(section("Demon Spectrum", demonBody, false));
        column.add(gap());
        distanceBody = blankBody();
        column.add(section("LL Torpedo Distance", distanceBody, false));
        column.add(Box.createVerticalGlue());
        return column;
    }

    /** A cyan bar that opens and closes the rows under it. */
    private JPanel section(String title, final JComponent block, boolean open) {
        block.setVisible(open);
        JPanel header = band(CYAN, INK, title);
        header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MouseAdapter toggle = new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                block.setVisible(!block.isVisible());
                block.getParent().revalidate();
                block.getParent().repaint();
            }
        };
        listen(header, toggle);

        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(0f);
        group.add(header);
        group.add(block);
        return group;
    }

    private JPanel gaugesBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setAlignmentX(0f);
        body.add(gap());
        body.add(meter("OS WIRE (Nm)", GREEN));
        body.add(meter("Weapon Wire (Nm)", GREEN));
        body.add(meter("Battery Auto (KWh)", GREEN));
        body.add(ends("OS/WpnDp", "Wpn/TgtD"));
        body.add(bar(RED));
        body.add(gap());
        return body;
    }

    /**
     * Torpedo Trajectory. Tubes are 01 through 06. Each tube keeps its own fields.
     * The cyan title shows the tube that is selected.
     */
    private JPanel trajectorySection() {
        trajectoryTitle = new JLabel("Torpedo Trajectory - Tube:06", SwingConstants.CENTER);
        trajectoryTitle.setForeground(INK);
        trajectoryTitle.setFont(new Font("Dialog", Font.PLAIN, 13));
        trajectoryTitle.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CYAN);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(280, ROW));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW));
        header.setAlignmentX(0f);
        header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        header.add(trajectoryTitle, BorderLayout.CENTER);
        header.add(arrows(INK), BorderLayout.EAST);

        final JPanel block = trajectoryBody();
        trajectoryBlock = block;
        block.setVisible(false);
        MouseAdapter toggle = new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                block.setVisible(!block.isVisible());
                block.getParent().revalidate();
                block.getParent().repaint();
            }
        };
        listen(header, toggle);

        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(0f);
        group.add(header);
        group.add(block);
        return group;
    }

    /** Show one tube, 01 through 06, and open the trajectory section. */
    public void showTube(String tube) {
        if (choosingTube || tube == null) {
            return;
        }
        choosingTube = true;
        trajectoryTubes.setSelectedItem(tube);
        trajectoryCards.show(trajectoryDeck, tube);
        trajectoryTitle.setText("Torpedo Trajectory - Tube:" + tube);
        trajectoryBlock.setVisible(true);
        trajectoryBlock.getParent().revalidate();
        trajectoryBlock.getParent().repaint();
        choosingTube = false;
    }

    private JPanel trajectoryBody() {
        final String[] tubes = {"01", "02", "03", "04", "05", "06"};
        trajectoryCards = new CardLayout();
        trajectoryDeck = new JPanel(trajectoryCards);
        trajectoryDeck.setOpaque(false);
        for (int i = 0; i < tubes.length; i++) {
            trajectoryDeck.add(trajectoryForm(), tubes[i]);
        }
        trajectoryCards.show(trajectoryDeck, "06");

        trajectoryTubes = new JComboBox(tubes);
        trajectoryTubes.setSelectedItem("06");
        trajectoryTubes.setFont(new Font("Dialog", Font.PLAIN, 11));
        trajectoryTubes.setBackground(Color.WHITE);
        trajectoryTubes.setForeground(INK);
        trajectoryTubes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                showTube((String) trajectoryTubes.getSelectedItem());
            }
        });

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(Color.WHITE);
        shell.setOpaque(true);
        shell.setBorder(BorderFactory.createLineBorder(BOX_LINE));
        shell.add(trajectoryTubes, BorderLayout.CENTER);

        JPanel stack = new JPanel();
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setOpaque(false);
        stack.setAlignmentX(0f);
        stack.add(gap());
        stack.add(trajRow(null, sideField("Tube", shell)));
        stack.add(gap());
        stack.add(trajectoryDeck);
        return stack;
    }

    /** One tube. Left bars are output. White boxes on the right are input. */
    private JPanel trajectoryForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setAlignmentX(0f);
        form.add(trajRow(greyBar("RT", readout("0:0:0")), sideField("Max RT", whiteBox())));
        form.add(gap());
        form.add(trajRow(greyBar("WTB", null), null));
        form.add(gap());
        form.add(trajRow(greyBar("TTHB", null), sideField("TTVB", whiteBox())));
        form.add(gap());
        form.add(trajRow(greyBar("WR", null), sideField("ApfDtn", whiteBox())));
        form.add(gap());
        form.add(trajRow(greyBar("Achm", null), null));
        form.add(gap());
        form.add(trajRow(greyBar("NDB", null), null));
        form.add(gap());
        form.add(trajRow(greyBar("Lat", null), sideField("Lon", whiteBox())));
        form.add(gap());
        form.add(trajRow(greyBar("TTR", null), sideField("WTR", whiteBox())));
        form.add(gap());
        form.add(trajRow(greyBar("WCo", null), null));
        form.add(gap());
        form.add(trajRow(greyBar("WSp", null), null));
        form.add(gap());
        form.add(trajRow(greyBar("WDp", null), null));
        form.add(gap());
        form.add(trajRow(greyBar("TGT ID", tinyChoice()), sendBox()));
        return form;
    }

    private static JPanel trajRow(JComponent left, JComponent right) {
        JPanel line = new JPanel(new GridLayout(1, 2, 6, 0));
        line.setOpaque(false);
        line.setPreferredSize(new Dimension(280, PARAM_ROW));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, PARAM_ROW));
        line.setAlignmentX(0f);
        line.add(left == null ? spacer() : left);
        line.add(right == null ? spacer() : right);
        return line;
    }

    private static JPanel spacer() {
        JPanel blank = new JPanel();
        blank.setOpaque(false);
        return blank;
    }

    /** Grey output bar. value sits at the right when the row shows one. */
    private static JPanel greyBar(String name, JComponent value) {
        JLabel label = new JLabel(name);
        label.setForeground(INK);
        label.setFont(new Font("Dialog", Font.PLAIN, 11));
        label.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 4));
        JPanel bar = new JPanel(new BorderLayout(4, 0));
        bar.setBackground(OUTPUT_BG);
        bar.setOpaque(true);
        bar.setBorder(BorderFactory.createLineBorder(BOX_LINE));
        bar.add(label, BorderLayout.WEST);
        if (value != null) {
            bar.add(value, BorderLayout.EAST);
        }
        return bar;
    }

    private static JTextField readout(String text) {
        JTextField field = new JTextField(text);
        field.setEditable(false);
        field.setFont(new Font("Dialog", Font.PLAIN, 11));
        field.setBackground(new Color(210, 210, 210));
        field.setForeground(INK);
        field.setBorder(BorderFactory.createLineBorder(BOX_LINE));
        field.setPreferredSize(new Dimension(52, PARAM_ROW - 6));
        return field;
    }

    private static JPanel sideField(String name, JComponent input) {
        JLabel label = new JLabel(name);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Dialog", Font.PLAIN, 11));
        label.setPreferredSize(new Dimension(52, PARAM_ROW));
        JPanel line = new JPanel(new BorderLayout(4, 0));
        line.setOpaque(false);
        line.add(label, BorderLayout.WEST);
        line.add(input, BorderLayout.CENTER);
        return line;
    }

    private static JTextField whiteBox() {
        JTextField field = new JTextField();
        field.setFont(new Font("Dialog", Font.PLAIN, 11));
        field.setBackground(Color.WHITE);
        field.setForeground(INK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BOX_LINE),
            BorderFactory.createEmptyBorder(0, 4, 0, 4)));
        return field;
    }

    private static JComboBox tinyChoice() {
        JComboBox box = new JComboBox(new String[] {""});
        box.setFont(new Font("Dialog", Font.PLAIN, 11));
        box.setBackground(Color.WHITE);
        box.setForeground(INK);
        box.setPreferredSize(new Dimension(56, PARAM_ROW - 6));
        return box;
    }

    private static JPanel sendBox() {
        JLabel send = new JLabel("Send", SwingConstants.CENTER);
        send.setForeground(Color.WHITE);
        send.setFont(new Font("Dialog", Font.PLAIN, 12));
        JPanel hold = new JPanel(new BorderLayout());
        hold.setBackground(SEND);
        hold.setOpaque(true);
        hold.setBorder(BorderFactory.createLineBorder(BOX_LINE));
        hold.setPreferredSize(new Dimension(72, PARAM_ROW));
        hold.add(send, BorderLayout.CENTER);
        JPanel align = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        align.setOpaque(false);
        align.add(hold);
        return align;
    }

    /**
     * Torpedo Parameters. Grey cells are the output and stay disabled.
     * White cells are the input. A choice cell uses the command list.
     */
    private JPanel torpedoParameters() {
        JPanel table = new JPanel();
        table.setLayout(new BoxLayout(table, BoxLayout.Y_AXIS));
        table.setBackground(GRID);
        table.setOpaque(true);
        table.setAlignmentX(0f);
        table.add(paramLine("Enable Dist", outputCell(), inputField()));
        table.add(paramLine("SA Pattern", outputCell(), inputChoice(
            new String[] {"Auto", "SP1", "SP2", "SP3", "SP4", "SP5", "SP6"}, "Auto")));
        table.add(paramLine("SA Width", outputCell(), inputField()));
        table.add(paramLine("SA Length", outputCell(), inputField()));
        table.add(paramLine("ASH", outputCell(), inputField()));
        table.add(paramLine("UDL", outputCell(), inputField()));
        table.add(paramLine("LDL", outputCell(), inputField()));
        table.add(paramLine("ACHM", outputCell(), inputChoice(
            new String[] {"Acoustic", "wake"}, "Acoustic")));
        table.add(paramLine("Acoustic Mode", outputCell(), inputChoice(
            new String[] {"Simultaneous", "Passive"}, "Simultaneous")));
        table.add(paramLine("Attack Mode", outputCell(), inputChoice(
            new String[] {"Sub Surface", "Surface"}, "Sub Surface")));
        table.add(paramLine("Wake Sens", outputCell(), inputChoice(
            new String[] {"ON", "OFF"}, "OFF")));
        table.add(paramLine("Fuse", outputCell(), inputChoice(
            new String[] {"Auto", "ON", "OFF"}, "Auto")));
        table.add(paramLine("Home Enab", outputCell(), inputChoice(
            new String[] {"ON", "OFF"}, "ON")));
        table.add(paramLine("Int Guide Mode", outputCell(), inputChoice(
            new String[] {"Auto", "Pursuit", "PN"}, "Auto")));
        table.add(paramLine("Torpedo Stop", outputCell(), inputChoice(
            new String[] {"Running", "E Stop", "Stop"}, "Stop")));
        table.add(paramLine("Homing Contact", outputBadge("P Mod A"), outputCell()));
        table.add(paramLine("Battery Voltage", outputBadge("P Mod B"), outputCell()));
        table.add(paramLine("Motor Current", outputCell(), inputField()));
        table.add(paramLine("FOC Recap", outputCell(), inputField()));
        table.add(paramLine("Bin Pressure", outputCell(), inputField()));
        table.add(paramLine("ACCHM Echo", outputCell(), inputField()));
        table.add(sendLine());
        return table;
    }

    /** Label, grey output, white input. */
    private static JPanel paramLine(String name, JComponent output, JComponent input) {
        JPanel values = new JPanel(new GridLayout(1, 2, 2, 0));
        values.setOpaque(false);
        values.add(output);
        values.add(input);
        return paramRow(name, values);
    }

    private static JPanel sendLine() {
        JLabel send = new JLabel("Send", SwingConstants.CENTER);
        send.setForeground(Color.WHITE);
        send.setFont(new Font("Dialog", Font.PLAIN, 12));
        JPanel hold = new JPanel(new BorderLayout());
        hold.setBackground(SEND);
        hold.setOpaque(true);
        hold.setBorder(BorderFactory.createLineBorder(BOX_LINE));
        hold.add(send, BorderLayout.CENTER);
        return paramRow("Wire Guidance Mode", hold);
    }

    private static JPanel paramRow(String name, JComponent values) {
        JPanel line = new JPanel(new BorderLayout(1, 0));
        line.setOpaque(false);
        line.setPreferredSize(new Dimension(280, PARAM_ROW));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, PARAM_ROW));
        line.setAlignmentX(0f);
        line.add(nameCell(name), BorderLayout.WEST);
        line.add(values, BorderLayout.CENTER);
        return line;
    }

    private static JPanel nameCell(String name) {
        JLabel label = new JLabel(name);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Dialog", Font.PLAIN, 11));
        label.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 4));
        JPanel cell = new JPanel(new BorderLayout());
        cell.setBackground(LABEL_BG);
        cell.setOpaque(true);
        cell.setPreferredSize(new Dimension(118, PARAM_ROW));
        cell.add(label, BorderLayout.CENTER);
        return cell;
    }

    /** Grey output. It stays disabled until a launch fills it. */
    private static JPanel outputCell() {
        JPanel cell = new JPanel();
        cell.setBackground(OUTPUT_BG);
        cell.setOpaque(true);
        cell.setBorder(BorderFactory.createLineBorder(BOX_LINE));
        return cell;
    }

    private static JPanel outputBadge(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Dialog", Font.PLAIN, 11));
        JPanel cell = new JPanel(new BorderLayout());
        cell.setBackground(INK);
        cell.setOpaque(true);
        cell.setBorder(BorderFactory.createLineBorder(BOX_LINE));
        cell.add(label, BorderLayout.CENTER);
        return cell;
    }

    /** White input. Digits and one decimal point. */
    private static JTextField inputField() {
        JTextField field = Mono.number(4);
        field.setFont(new Font("Dialog", Font.PLAIN, 11));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BOX_LINE),
            BorderFactory.createEmptyBorder(0, 4, 0, 4)));
        return field;
    }

    private static JComponent inputChoice(String[] items, String selected) {
        JComboBox box = new JComboBox(items);
        box.setSelectedItem(selected);
        box.setFont(new Font("Dialog", Font.PLAIN, 11));
        box.setBackground(Color.WHITE);
        box.setForeground(INK);
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(Color.WHITE);
        shell.setOpaque(true);
        shell.setBorder(BorderFactory.createLineBorder(BOX_LINE));
        shell.add(box, BorderLayout.CENTER);
        return shell;
    }

    /** Closed drop-downs open onto an empty dark block. */
    private static JPanel blankBody() {
        JPanel block = new JPanel();
        block.setBackground(DARK);
        block.setOpaque(true);
        block.setPreferredSize(new Dimension(280, 36));
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        block.setAlignmentX(0f);
        return block;
    }

    private static JPanel meter(String name, Color color) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        block.setAlignmentX(0f);
        JLabel label = new JLabel(name);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Dialog", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createEmptyBorder(2, 8, 0, 8));
        label.setAlignmentX(0f);
        block.add(label);
        block.add(bar(color));
        return block;
    }

    private static JPanel bar(Color color) {
        JPanel line = new JPanel();
        line.setBackground(color);
        line.setOpaque(true);
        line.setPreferredSize(new Dimension(260, 8));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        line.setAlignmentX(0f);
        JPanel pad = new JPanel(new BorderLayout());
        pad.setOpaque(false);
        pad.setBorder(BorderFactory.createEmptyBorder(2, 8, 4, 8));
        pad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        pad.setAlignmentX(0f);
        pad.add(line, BorderLayout.CENTER);
        return pad;
    }

    private static JPanel band(Color background, Color ink, String text) {
        JPanel line = new JPanel(new BorderLayout());
        line.setBackground(background);
        line.setOpaque(true);
        line.setPreferredSize(new Dimension(280, ROW));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW));
        line.setAlignmentX(0f);
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(ink);
        label.setFont(new Font("Dialog", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        line.add(label, BorderLayout.CENTER);
        line.add(arrows(ink), BorderLayout.EAST);
        return line;
    }

    private static JPanel ends(String leftText, String rightText) {
        JPanel line = new JPanel(new BorderLayout());
        line.setOpaque(false);
        line.setPreferredSize(new Dimension(280, 22));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        line.setAlignmentX(0f);
        JLabel left = new JLabel(leftText);
        left.setForeground(Color.WHITE);
        left.setFont(new Font("Dialog", Font.PLAIN, 13));
        left.setBorder(BorderFactory.createEmptyBorder(2, 8, 0, 8));
        JLabel right = new JLabel(rightText);
        right.setForeground(Color.WHITE);
        right.setFont(new Font("Dialog", Font.PLAIN, 13));
        right.setBorder(BorderFactory.createEmptyBorder(2, 8, 0, 8));
        line.add(left, BorderLayout.WEST);
        line.add(right, BorderLayout.EAST);
        return line;
    }

    private static JPanel arrows(Color ink) {
        JPanel marks = new JPanel(new GridLayout(2, 1));
        marks.setOpaque(false);
        marks.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 6));
        JLabel up = new JLabel("^", SwingConstants.CENTER);
        JLabel down = new JLabel("v", SwingConstants.CENTER);
        up.setForeground(ink);
        down.setForeground(ink);
        up.setFont(new Font("Dialog", Font.BOLD, 9));
        down.setFont(new Font("Dialog", Font.BOLD, 9));
        marks.add(up);
        marks.add(down);
        return marks;
    }

    private static void listen(Component component, MouseAdapter toggle) {
        component.addMouseListener(toggle);
        if (component instanceof Container) {
            Component[] children = ((Container) component).getComponents();
            for (int i = 0; i < children.length; i++) {
                listen(children[i], toggle);
            }
        }
    }

    private static java.awt.Component gap() {
        return Box.createVerticalStrut(3);
    }
}

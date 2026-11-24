package page;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

import page.form.Block;
import page.form.FormCard;
import page.form.FormRow;
import page.form.FormStack;
import page.form.GroupLabel;
import page.form.Heading;
import page.form.HintArea;
import page.form.HintField;
import page.form.Mono;
import page.form.OptionGroup;
import page.model.Engagement;

/**
 * Create Engagement form, built from the pieces in page.form.
 * Copy this class and change build() when you need a similar screen.
 */
public class CreateEngagementPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int PANEL_WIDTH = 620;

    private final HintField field1;
    private final HintField field2;
    private final JComboBox nameList;
    private final boolean nameIsList;
    private final String heading;
    private boolean filling;
    private final OptionGroup group1;
    private final OptionGroup group2;
    private final OptionGroup group3;
    private final JTextField field3;
    private final OptionGroup group4;
    private final JComboBox group5;
    private final JComboBox field4;
    private final JComboBox field5;
    private final HintArea field6;
    private final JButton cancelButton;
    private final JButton okButton;
    private final JButton closeButton;
    private JButton distanceOk;
    private JScrollPane formScroll;
    private final Map<String, JTextField> parameters = new LinkedHashMap<String, JTextField>();

    public CreateEngagementPanel() {
        this("CREATE ENGAGEMENT", false, false);
    }

    /**
     * heading is the title on the form.
     * nameIsList uses a drop-down of engagement names instead of a typed name.
     */
    protected CreateEngagementPanel(String heading, boolean nameIsList) {
        this(heading, nameIsList, false);
    }

    /**
     * displayOnly locks every field except the engagement-name drop-down.
     * OK is hidden. The name drop-down still loads a stored engagement.
     */
    protected CreateEngagementPanel(String heading, boolean nameIsList, boolean displayOnly) {
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
        field1 = new HintField("Enter Engagement No", 18);
        field2 = new HintField("Enter Engagement Name", 18);
        nameList = Mono.combo(new String[0]);
        group1 = new OptionGroup(new String[] {"Prosp Eng", "Real Engine"}, 0, 2);
        group2 = new OptionGroup(new String[] {"ETH WVR", "ETH WEXE"}, 0, 2);
        group3 = new OptionGroup(new String[] {"Yes", "No"}, 1, 2);
        field3 = Mono.number(10);
        field3.setText("0");
        group4 = new OptionGroup(
                new String[] {"Tube 6", "Tube 5", "Tube 4", "Tube 3", "Tube 2", "Tube 1"},
                5,
                2);
        group5 = Mono.combo(new String[] {"Default Sub Surface War", "Default Surface War"});
        field4 = Mono.combo(new String[] {"Surface", "Sub Surface"});
        field5 = Mono.combo(new String[] {"Select Track", "Automatic", "Manual"});
        field6 = new HintArea("Enter comments...", 4, 20);
        cancelButton = Mono.button("Cancel");
        okButton = Mono.accentButton("OK");
        closeButton = closeControl();
        try {
            build();
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

    private JPanel titleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        Mono.paint(bar);
        bar.setBorder(BorderFactory.createEmptyBorder(8, 12, 4, 8));

        JLabel title = new JLabel(heading);
        title.setForeground(Mono.FOREGROUND);
        title.setFont(Mono.TITLE_FONT);
        bar.add(title, BorderLayout.WEST);
        bar.add(closeButton, BorderLayout.EAST);

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setVisible(false);
            }
        });
        return bar;
    }

    private FormStack form() {
        FormStack form = new FormStack();
        form.setBorder(BorderFactory.createEmptyBorder(4, 12, 12, 12));

        form.add(new FormRow("Eng No", field1));
        form.add(Box.createVerticalStrut(8));
        if (nameIsList) {
            form.add(new FormRow("Engagement Name", nameList));
        } else {
            form.add(new FormRow("Engagement Name", field2));
        }
        form.add(new Heading("ENGAGEMENT CONFIGURATION"));
        form.add(section1Card());
        form.add(new Heading("TARGET PARAMETERS"));
        form.add(section2Card());
        form.add(new Heading("TARGET SELECTION"));
        form.add(section3Card());
        form.add(new Heading("COMMENTS"));
        form.add(section4Card());
        return form;
    }

    private FormCard section1Card() {
        FormCard card = new FormCard();
        card.addRow(new GroupLabel("Engagement State Selection"));
        card.addRow(group1);
        card.addRow(new GroupLabel("Weapon Select"));
        card.addRow(group2);
        card.addRow(new GroupLabel("Linked Launch"));
        card.addRow(group3);
        card.addRow(field3Row());
        card.addRow(new GroupLabel("Tube Select"));
        card.addRow(group4);
        card.addRow(new GroupLabel("Weapon Configuration Selection"));
        card.addRow(row(group5));
        return card;
    }

    private Block field3Row() {
        JPanel line = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        Mono.paint(line);
        JLabel label = new JLabel("Torpedo Distance");
        Mono.paint(label);
        distanceOk = Mono.button("OK");
        line.add(label);
        line.add(field3);
        line.add(distanceOk);
        return row(line);
    }

    private FormCard section2Card() {
        FormCard card = new FormCard();
        card.addRow(parameterNumber("TB", "305.00", "RB", "305.00"));
        card.addRow(parameterNumber("TP", "2.87", "Dp", "0.00"));
        card.addRow(parameterNumber("Tpe", "5.00", "TCa", "125.00"));
        card.addRow(parameterNumber("ATg", "0.00", field4Row()));
        card.addRow(parameterPair("Long", "80\u00B0 07' 48\"", "Lat", "00\u00B0 01' 46\" N"));
        return card;
    }

    private FormCard section3Card() {
        FormCard card = new FormCard();
        card.addRow(new FormRow("Track", field5));
        return card;
    }

    private FormCard section4Card() {
        FormCard card = new FormCard();
        card.addRow(row(field6));
        return card;
    }

    private JScrollPane scroller(FormStack form) {
        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Mono.BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private JPanel buttonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        Mono.paint(bar);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Mono.RULE));
        bar.add(cancelButton);
        bar.add(okButton);
        return bar;
    }

    private JButton closeControl() {
        JButton close = new JButton("\u00D7");
        Mono.paint(close);
        close.setFont(Mono.TITLE_FONT);
        close.setFocusPainted(false);
        close.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        close.setContentAreaFilled(false);
        return close;
    }

    private Block row(java.awt.Component child) {
        Block line = new Block();
        line.setLayout(new BorderLayout());
        line.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        line.add(child, BorderLayout.CENTER);
        return line;
    }

    private Block parameterNumber(String leftName, String leftValue, String rightName, String rightValue) {
        return pair(namedNumber(leftName, leftValue), namedNumber(rightName, rightValue));
    }

    private Block parameterNumber(String leftName, String leftValue, JPanel right) {
        return pair(namedNumber(leftName, leftValue), right);
    }

    private Block parameterPair(String leftName, String leftValue, String rightName, String rightValue) {
        return pair(namedField(leftName, leftValue), namedField(rightName, rightValue));
    }

    private Block parameterPair(String leftName, String leftValue, JPanel right) {
        return pair(namedField(leftName, leftValue), right);
    }

    private Block pair(JPanel left, JPanel right) {
        Block line = new Block();
        line.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
        line.setLayout(new java.awt.GridLayout(1, 2, 16, 0));
        line.add(left);
        line.add(right);
        return line;
    }

    private JPanel namedNumber(String name, String value) {
        return named(name, value, true);
    }

    private JPanel namedField(String name, String value) {
        return named(name, value, false);
    }

    private JPanel named(String name, String value, boolean number) {
        JPanel line = new JPanel(new BorderLayout(8, 0));
        Mono.paint(line);
        JLabel label = new JLabel(name);
        Mono.paint(label);
        label.setPreferredSize(new Dimension(48, 24));
        JTextField field = number ? Mono.number(8) : Mono.field(8);
        field.setText(value);
        parameters.put(name, field);
        line.add(label, BorderLayout.WEST);
        line.add(field, BorderLayout.CENTER);
        return line;
    }

    private JPanel field4Row() {
        JPanel line = new JPanel(new BorderLayout(8, 0));
        Mono.paint(line);
        JLabel label = new JLabel("Type");
        Mono.paint(label);
        label.setPreferredSize(new Dimension(48, 24));
        line.add(label, BorderLayout.WEST);
        line.add(field4, BorderLayout.CENTER);
        return line;
    }

    public Dimension getPreferredSize() {
        return new Dimension(PANEL_WIDTH, 700);
    }

    public Dimension getMinimumSize() {
        return new Dimension(PANEL_WIDTH, 240);
    }

    public Dimension getMaximumSize() {
        Dimension size = getPreferredSize();
        size.height = Integer.MAX_VALUE;
        return size;
    }

    public JButton getOkButton() {
        return okButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public String getField1() {
        return field1.getValue();
    }

    public String getField2() {
        if (nameIsList) {
            return selected(nameList);
        }
        return field2.getValue();
    }

    /** Names already stored. The first name is selected, which loads that engagement. */
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

    public String getGroup1() {
        return group1.getSelected();
    }

    public String getGroup2() {
        return group2.getSelected();
    }

    public String getGroup3() {
        return group3.getSelected();
    }

    public String getGroup4() {
        return group4.getSelected();
    }

    public String getField6() {
        return field6.getValue();
    }

    public String getParameter(String name) {
        JTextField field = parameters.get(name);
        if (field == null) {
            return "";
        }
        return field.getText();
    }

    private void setParameter(String name, String value) {
        JTextField field = parameters.get(name);
        if (field != null) {
            field.setText(value == null ? "" : value);
        }
    }

    private void selectItem(JComboBox box, String value) {
        if (value == null) {
            return;
        }
        box.setSelectedItem(value);
    }

    /** Put the form back to the Create Engagement starting values. */
    public void resetForCreate() {
        field1.setValue("");
        if (!nameIsList) {
            field2.setValue("");
        }
        group1.setSelected("Prosp Eng");
        group2.setSelected("ETH WVR");
        group3.setSelected("No");
        field3.setText("0");
        group4.setSelected("Tube 1");
        selectItem(group5, "Default Sub Surface War");
        setParameter("TB", "305.00");
        setParameter("RB", "305.00");
        setParameter("TP", "2.87");
        setParameter("Dp", "0.00");
        setParameter("Tpe", "5.00");
        setParameter("TCa", "125.00");
        setParameter("ATg", "0.00");
        selectItem(field4, "Surface");
        setParameter("Long", "80\u00B0 07' 48\"");
        setParameter("Lat", "00\u00B0 01' 46\" N");
        selectItem(field5, "Select Track");
        field6.setValue("");
        scrollToTop();
    }

    /** Fill every control from a saved engagement. The layout stays the create form. */
    public void apply(Engagement engagement) {
        filling = true;
        field1.setValue(engagement.getField1());
        if (nameIsList) {
            nameList.setSelectedItem(engagement.getField2());
        } else {
            field2.setValue(engagement.getField2());
        }
        group1.setSelected(engagement.getGroup1());
        group2.setSelected(engagement.getGroup2());
        group3.setSelected(engagement.getGroup3());
        field3.setText(engagement.getField3());
        group4.setSelected(engagement.getGroup4());
        selectItem(group5, engagement.getGroup5());
        setParameter("TB", engagement.getP1());
        setParameter("RB", engagement.getP2());
        setParameter("TP", engagement.getP3());
        setParameter("Dp", engagement.getP4());
        setParameter("Tpe", engagement.getP5());
        setParameter("TCa", engagement.getP6());
        setParameter("ATg", engagement.getP7());
        selectItem(field4, engagement.getField4());
        setParameter("Long", engagement.getP8());
        setParameter("Lat", engagement.getP9());
        selectItem(field5, engagement.getField5());
        field6.setValue(engagement.getField6());
        filling = false;
        scrollToTop();
    }

    private void lockForDisplay() {
        field1.setEditable(false);
        field1.setFocusable(false);
        field2.setEditable(false);
        field2.setFocusable(false);
        field3.setEditable(false);
        field6.setEditable(false);
        field6.setFocusable(false);
        group1.setLocked(true);
        group2.setLocked(true);
        group3.setLocked(true);
        group4.setLocked(true);
        group5.setEnabled(false);
        field4.setEnabled(false);
        field5.setEnabled(false);
        java.util.Iterator values = parameters.values().iterator();
        while (values.hasNext()) {
            ((JTextField) values.next()).setEditable(false);
        }
        if (distanceOk != null) {
            distanceOk.setEnabled(false);
        }
        okButton.setVisible(false);
    }

    public void scrollToTop() {
        if (formScroll != null) {
            formScroll.getVerticalScrollBar().setValue(0);
        }
    }

    /**
     * Copy every control into one plain object.
     * Call this from the OK listener, then pass the object to the backend.
     */
    public Engagement readForm() {
        Engagement engagement = new Engagement();
        engagement.setField1(field1.getValue());
        engagement.setField2(getField2());
        engagement.setGroup1(group1.getSelected());
        engagement.setGroup2(group2.getSelected());
        engagement.setGroup3(group3.getSelected());
        engagement.setField3(field3.getText());
        engagement.setGroup4(group4.getSelected());
        engagement.setGroup5(selected(group5));
        engagement.setP1(getParameter("TB"));
        engagement.setP2(getParameter("RB"));
        engagement.setP3(getParameter("TP"));
        engagement.setP4(getParameter("Dp"));
        engagement.setP5(getParameter("Tpe"));
        engagement.setP6(getParameter("TCa"));
        engagement.setP7(getParameter("ATg"));
        engagement.setField4(selected(field4));
        engagement.setP8(getParameter("Long"));
        engagement.setP9(getParameter("Lat"));
        engagement.setField5(selected(field5));
        engagement.setField6(field6.getValue());
        return engagement;
    }

    private String selected(JComboBox box) {
        Object item = box.getSelectedItem();
        if (item == null) {
            return "";
        }
        return item.toString();
    }
}

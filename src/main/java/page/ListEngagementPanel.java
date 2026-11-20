package page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import page.form.Mono;

/**
 * List of engagements. Same fields as Create Engagement.
 * The fields only display. Engagement Name, and the Name list, choose which one to show.
 */
public class ListEngagementPanel extends CreateEngagementPanel {

    private static final long serialVersionUID = 1L;

    private final DefaultListModel items = new DefaultListModel();
    private final JList choices = new JList(items);
    private boolean selecting;

    public ListEngagementPanel() {
        super("LIST OF ENGAGEMENTS", true, true);
        choices.setBackground(Color.WHITE);
        choices.setForeground(Mono.FIELD_TEXT);
        choices.setFont(Mono.FONT);
        choices.setSelectionBackground(new Color(180, 210, 240));
        choices.setSelectionForeground(Mono.FIELD_TEXT);
        choices.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() || selecting || isFilling()) {
                    return;
                }
                Object value = choices.getSelectedValue();
                if (value == null) {
                    return;
                }
                setSelectedName(value.toString());
            }
        });
        addNameListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (isFilling() || selecting) {
                    return;
                }
                selecting = true;
                choices.setSelectedValue(getSelectedName(), true);
                selecting = false;
            }
        });

        JLabel caption = new JLabel("Name");
        Mono.paint(caption);

        JScrollPane listScroll = new JScrollPane(choices);
        listScroll.setPreferredSize(new Dimension(72, 96));

        JPanel stack = new JPanel(new BorderLayout(0, 4));
        Mono.paint(stack);
        stack.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 8));
        stack.add(caption, BorderLayout.NORTH);
        stack.add(listScroll, BorderLayout.CENTER);

        JPanel east = new JPanel(new BorderLayout());
        Mono.paint(east);
        east.add(stack, BorderLayout.NORTH);
        add(east, BorderLayout.EAST);
    }

    protected void namesChanged(String[] names) {
        selecting = true;
        items.clear();
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                items.addElement(names[i]);
            }
            if (names.length > 0) {
                choices.setSelectedIndex(0);
            }
        }
        selecting = false;
    }
}

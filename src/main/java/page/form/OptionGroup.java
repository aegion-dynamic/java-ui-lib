package page.form;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

/**
 * Radio buttons that belong to one choice.
 * columns is how many choices sit side by side.
 * selectedIndex is the choice that starts selected. Pass -1 for none.
 */
public class OptionGroup extends Block {

    private static final long serialVersionUID = 1L;

    private final JRadioButton[] buttons;
    private final ButtonGroup group;

    public OptionGroup(String[] choices, int selectedIndex, int columns) {
        super();
        int rows = 1;
        if (columns < 1) {
            columns = 1;
        }
        if (choices.length > 0) {
            rows = ((choices.length - 1) / columns) + 1;
        }
        setLayout(new GridLayout(rows, columns, 12, 4));

        group = new ButtonGroup();
        buttons = new JRadioButton[choices.length];
        for (int i = 0; i < choices.length; i++) {
            JRadioButton button = Mono.radio(choices[i]);
            group.add(button);
            if (i == selectedIndex) {
                button.setSelected(true);
            }
            buttons[i] = button;
            add(button);
        }
    }

    /** Select the choice whose label matches text. No match clears the group. */
    public void setSelected(String text) {
        for (int i = 0; i < buttons.length; i++) {
            if (text != null && text.equals(buttons[i].getText())) {
                buttons[i].setSelected(true);
                return;
            }
        }
        group.clearSelection();
    }

    /** Stop clicks from changing the choice. The selected mark stays visible. */
    public void setLocked(boolean locked) {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setEnabled(!locked);
        }
    }

    /** Runs when the user clicks a choice. The event text is not the label. Read getSelected. */
    public void addListener(ActionListener listener) {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].addActionListener(listener);
        }
    }

    /** The text of the selected choice, or an empty string when none is selected. */
    public String getSelected() {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].isSelected()) {
                return buttons[i].getText();
            }
        }
        return "";
    }
}

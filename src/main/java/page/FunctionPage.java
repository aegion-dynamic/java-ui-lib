package page;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

import page.backend.EngagementService;
import page.backend.WeaponConfigService;
import page.model.Engagement;
import page.model.WeaponConfig;


/**
 * The screen. One listener handles every footer button.
 * The window opens with Engagement Parameters in the lower-right form slot.
 * F1 calls create() and puts a blank Create Engagement form in that slot.
 * OK on that form stores it and returns to Engagement Parameters.
 * F2 calls onF2() and opens ModifyEngagementPanel.
 * The name drop-down lists engagements already stored. The chosen name fills the form.
 * F3 calls onF3() and shows Delete Engagement.
 * After F4, the first key is command B1. It opens Create Weapon Configuration.
 * The second key opens Modify Weapon Configuration. The third opens Delete Weapon Config.
 * F7 opens List of Engagements. The chosen name fills the Create Engagement fields, and the fields stay read-only.
 * F8 opens List of Weapon Configurations. The chosen name fills the fields, and the fields stay read-only.
 * The drop-down in the top bar chooses Eng or WC. Eng is this screen. WC is the depth-window page.
 * F4 swaps the footer to a second set of buttons.
 * A second F4 restores the first set. The other methods are empty
 * on purpose: fill those in with the functions from the real project.
 *
 * To drop this into an existing window, copy the listener and the
 * create() method, then add the bar with BorderLayout.SOUTH.
 */
public class FunctionPage extends JFrame {

    private static final long serialVersionUID = 1L;

    /** Commands for the second set. Slot 4 stays "F4" so the same key can switch back. */
    private static final String[] SECOND_KEYS = {
        "B1", "B2", "B3", "F4", "B5", "B6", "B7", "B8", "B9", "B10", "B11", "B12"
    };

    /** Words beside the blue F1–F12 squares on the first set. */
    private static final String[] FIRST_TEXT = {
        "Create", "Modify", "Delete", "Swap", "Op 5", "Op 6",
        "Engs", "List", "Op 9", "Op 10", "Op 11", "Op 12"
    };

    /** Words beside the blue squares on the WC page. */
    private static final String[] WC_TEXT = {
        "CFM", "BG RIDER IN", "BG RIDER OUT", "OFFSET", "MANU", "--------",
        "TDISP", "--------", "--------", "Send WDATA", "TCCM", "WAKE"
    };

    /** Words beside the blue squares after F4. Slot 4 stays "Swap". */
    private static final String[] SECOND_TEXT = {
        "WConfig", "Modify", "Delete", "Swap", "Alt 5", "Alt 6",
        "Alt 7", "Alt 8", "Alt 9", "Alt 10", "Alt 11", "Alt 12"
    };

    private static final Color BLUE = new Color(46, 134, 222);
    private static final Color DARK = new Color(28, 28, 30);
    private static final int FORM_WIDTH = 640;

    private final JLabel resultLabel = new JLabel(" ");
    private final CardLayout formCards = new CardLayout();
    private final JPanel formSlot = new JPanel(formCards);
    private EngagementParametersPanel parameters;
    private CreateEngagementPanel form;
    private ModifyEngagementPanel modifyForm;
    private DeleteEngagementPanel deleteForm;
    private CreateWeaponConfigPanel weaponForm;
    private ModifyWeaponConfigPanel modifyWeaponForm;
    private DeleteWeaponConfigPanel deleteWeaponForm;
    private ListWeaponConfigPanel listWeaponForm;
    private ListEngagementPanel listEngagementForm;
    private EngagementService backend;
    private WeaponConfigService weaponBackend;
    private FunctionKeyBar footer;
    private boolean secondSet;
    private boolean wcPage;
    private final CardLayout screenCards = new CardLayout();
    private final JPanel screenSlot = new JPanel(screenCards);
    private final JComboBox pageChoice = new JComboBox(new String[] {"Eng", "WC"});

    /**
     * One listener for every function button.
     * The button's action command says which key it was.
     */
    private final ActionListener functionListener = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            String key = event.getActionCommand();
            if (wcPage) {
                return;
            }

            if ("F4".equals(key)) {
                onF4();
            } else if ("F1".equals(key)) {
                create();
            } else if ("F2".equals(key)) {
                onF2();
            } else if ("F3".equals(key)) {
                onF3();
            } else if ("F5".equals(key)) {
                onF5();
            } else if ("F6".equals(key)) {
                onF6();
            } else if ("F7".equals(key)) {
                onF7();
            } else if ("F8".equals(key)) {
                onF8();
            } else if ("F9".equals(key)) {
                onF9();
            } else if ("F10".equals(key)) {
                onF10();
            } else if ("F11".equals(key)) {
                onF11();
            } else if ("F12".equals(key)) {
                onF12();
            } else if ("B1".equals(key)) {
                onB1();
            } else if ("B2".equals(key)) {
                onB2();
            } else if ("B3".equals(key)) {
                onB3();
            } else if ("B5".equals(key)) {
                onB5();
            } else if ("B6".equals(key)) {
                onB6();
            } else if ("B7".equals(key)) {
                onB7();
            } else if ("B8".equals(key)) {
                onB8();
            } else if ("B9".equals(key)) {
                onB9();
            } else if ("B10".equals(key)) {
                onB10();
            } else if ("B11".equals(key)) {
                onB11();
            } else if ("B12".equals(key)) {
                onB12();
            }
        }
    };

    public FunctionPage() {
        super("Java UI");

        JPanel page = new JPanel(new BorderLayout(0, 4));
        page.setBackground(DARK);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        topBar.setBackground(BLUE);
        topBar.setPreferredSize(new Dimension(100, 40));
        JLabel pageLabel = new JLabel("Page");
        pageLabel.setForeground(Color.WHITE);
        pageLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        pageChoice.setFont(new Font("Dialog", Font.PLAIN, 14));
        topBar.add(pageLabel);
        topBar.add(pageChoice);

        JPanel blue = new JPanel(new BorderLayout());
        blue.setBackground(BLUE);
        blue.setPreferredSize(new Dimension(100, 190));
        resultLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        blue.add(resultLabel, BorderLayout.NORTH);

        JPanel dark = new JPanel();
        dark.setBackground(DARK);

        JPanel left = new JPanel(new BorderLayout(0, 4));
        left.setOpaque(false);
        left.add(dark, BorderLayout.CENTER);
        left.add(blue, BorderLayout.SOUTH);

        LookAndFeel previous = UIManager.getLookAndFeel();
        try {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception ex) {
            /* Keep the look that is already installed. */
        }
        parameters = new EngagementParametersPanel();
        form = new CreateEngagementPanel();
        modifyForm = new ModifyEngagementPanel();
        deleteForm = new DeleteEngagementPanel();
        weaponForm = new CreateWeaponConfigPanel();
        modifyWeaponForm = new ModifyWeaponConfigPanel();
        deleteWeaponForm = new DeleteWeaponConfigPanel();
        listWeaponForm = new ListWeaponConfigPanel();
        listEngagementForm = new ListEngagementPanel();
        try {
            UIManager.setLookAndFeel(previous);
        } catch (Exception ex) {
            /* Leave the look the forms were built with. */
        }
        backend = new EngagementService();
        weaponBackend = new WeaponConfigService();
        form.getOkButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Engagement engagement = form.readForm();
                backend.create(engagement);
                resultLabel.setText("Saved " + engagement.getField1()
                        + " / " + engagement.getGroup1());
                showParameters();
            }
        });
        modifyForm.addNameListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (modifyForm.isFilling()) {
                    return;
                }
                Engagement selected = backend.findByName(modifyForm.getSelectedName());
                if (selected != null) {
                    modifyForm.apply(selected);
                }
            }
        });
        modifyForm.getOkButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Engagement engagement = modifyForm.readForm();
                backend.update(engagement);
                resultLabel.setText("Modified " + engagement.getField1()
                        + " / " + engagement.getGroup1());
                showParameters();
            }
        });
        modifyForm.getCancelButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                resultLabel.setText("Cancel");
                showParameters();
            }
        });
        form.getCancelButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                resultLabel.setText("Cancel");
                showParameters();
            }
        });
        deleteForm.getOkButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String name = deleteForm.getEngName();
                backend.deleteByName(name);
                resultLabel.setText("Delete " + name);
                showParameters();
            }
        });
        deleteForm.getCancelButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                resultLabel.setText("Cancel");
                showParameters();
            }
        });
        weaponForm.getOkButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                WeaponConfig config = weaponForm.readForm();
                weaponBackend.create(config);
                parameters.applyWeapon(config);
                resultLabel.setText("Saved weapon configuration");
                showParameters();
            }
        });
        modifyWeaponForm.addNameListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (modifyWeaponForm.isFilling()) {
                    return;
                }
                WeaponConfig selected = weaponBackend.findByName(modifyWeaponForm.getSelectedName());
                if (selected != null) {
                    modifyWeaponForm.apply(selected);
                }
            }
        });
        modifyWeaponForm.getOkButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                WeaponConfig config = modifyWeaponForm.readForm();
                weaponBackend.update(config);
                parameters.applyWeapon(config);
                resultLabel.setText("Modified weapon configuration");
                showParameters();
            }
        });
        modifyWeaponForm.getCancelButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                resultLabel.setText("Cancel");
                showParameters();
            }
        });
        weaponForm.getCancelButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                resultLabel.setText("Cancel");
                showParameters();
            }
        });
        deleteWeaponForm.getOkButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String name = deleteWeaponForm.getConfigName();
                weaponBackend.deleteByName(name);
                resultLabel.setText("Delete " + name);
                showParameters();
            }
        });
        deleteWeaponForm.getCancelButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                resultLabel.setText("Cancel");
                showParameters();
            }
        });
        listWeaponForm.addNameListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (listWeaponForm.isFilling()) {
                    return;
                }
                WeaponConfig selected = weaponBackend.findByName(listWeaponForm.getSelectedName());
                if (selected != null) {
                    listWeaponForm.apply(selected);
                }
            }
        });
        listWeaponForm.getCancelButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                resultLabel.setText("Cancel");
                showParameters();
            }
        });
        listEngagementForm.addNameListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (listEngagementForm.isFilling()) {
                    return;
                }
                Engagement selected = backend.findByName(listEngagementForm.getSelectedName());
                if (selected != null) {
                    listEngagementForm.apply(selected);
                }
            }
        });
        listEngagementForm.getCancelButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                resultLabel.setText("Cancel");
                showParameters();
            }
        });

        formSlot.setBackground(DARK);
        formSlot.setPreferredSize(new Dimension(FORM_WIDTH, 400));
        formSlot.add(parameters, "parameters");
        formSlot.add(form, "create");
        formSlot.add(modifyForm, "modify");
        formSlot.add(deleteForm, "delete");
        formSlot.add(weaponForm, "wconfig");
        formSlot.add(modifyWeaponForm, "modifyWeapon");
        formSlot.add(deleteWeaponForm, "deleteWeapon");
        formSlot.add(listWeaponForm, "listWeapon");
        formSlot.add(listEngagementForm, "listEngagement");
        formCards.show(formSlot, "parameters");

        JPanel right = new JPanel(new BorderLayout(0, 4));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(FORM_WIDTH, 100));
        right.add(new MotionReadout(), BorderLayout.NORTH);
        right.add(formSlot, BorderLayout.CENTER);

        JPanel stage = new JPanel(new BorderLayout(4, 0));
        stage.setBackground(DARK);
        stage.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        stage.add(left, BorderLayout.CENTER);
        stage.add(right, BorderLayout.EAST);

        screenSlot.setOpaque(false);
        screenSlot.add(stage, "eng");
        screenSlot.add(new WcPage(), "wc");
        screenCards.show(screenSlot, "eng");

        page.add(topBar, BorderLayout.NORTH);
        page.add(screenSlot, BorderLayout.CENTER);

        /* bar height, button width, button height — change these to fit the other screen */
        footer = new FunctionKeyBar(64, 96, 40, functionListener);
        footer.setLabels(FunctionKeyBar.KEYS, FIRST_TEXT);
        page.add(footer, BorderLayout.SOUTH);
        pageChoice.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                showChosenPage();
            }
        });

        setContentPane(page);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setLocationRelativeTo(null);
    }

    /** F1. Puts a fresh Create Engagement form in the lower-right slot. */
    private void create() {
        form.resetForCreate();
        formCards.show(formSlot, "create");
        resultLabel.setText("Create engagement");
    }

    /** F2. Modify Engagement. The name drop-down picks which stored engagement to open. */
    private void onF2() {
        String[] names = backend.names();
        if (names.length == 0) {
            resultLabel.setText("Create an engagement first");
            return;
        }
        modifyForm.setNames(names);
        Engagement selected = backend.findByName(modifyForm.getSelectedName());
        if (selected != null) {
            modifyForm.apply(selected);
        }
        formCards.show(formSlot, "modify");
        resultLabel.setText("Modify engagement");
    }

    /** F3. Delete Engagement. Eng No lists 1, 2, and 3 until real numbers are supplied. */
    private void onF3() {
        formCards.show(formSlot, "delete");
        resultLabel.setText("Delete engagement");
    }

    /** The top drop-down. Eng is the form screen. WC is the depth-window screen. */
    private void showChosenPage() {
        Object item = pageChoice.getSelectedItem();
        if ("WC".equals(item)) {
            wcPage = true;
            screenCards.show(screenSlot, "wc");
            footer.setLabels(FunctionKeyBar.KEYS, WC_TEXT);
        } else {
            wcPage = false;
            screenCards.show(screenSlot, "eng");
            if (secondSet) {
                footer.setLabels(SECOND_KEYS, SECOND_TEXT);
            } else {
                footer.setLabels(FunctionKeyBar.KEYS, FIRST_TEXT);
            }
        }
    }

    /** The page that is showing when the window opens, and after OK or Cancel. */
    private void showParameters() {
        formCards.show(formSlot, "parameters");
    }

    /**
     * F4 swaps the footer.
     * The blue squares stay F1–F12. The words beside them change.
     * The first press shows Alt 1 and the rest. The next press restores Create and the rest.
     */
    private void onF4() {
        secondSet = !secondSet;
        if (secondSet) {
            footer.setLabels(SECOND_KEYS, SECOND_TEXT);
            resultLabel.setText("F4 second set");
        } else {
            footer.setLabels(FunctionKeyBar.KEYS, FIRST_TEXT);
            resultLabel.setText("F4 first set");
        }
    }

    /** F5. */
    private void onF5() {
        resultLabel.setText("F5");
    }

    /** F6. */
    private void onF6() {
        resultLabel.setText("F6");
    }

    /** F7. List of engagements. The name drop-down loads one engagement for display. */
    private void onF7() {
        String[] names = backend.names();
        if (names.length == 0) {
            resultLabel.setText("Create an engagement first");
            return;
        }
        listEngagementForm.setNames(names);
        Engagement selected = backend.findByName(listEngagementForm.getSelectedName());
        if (selected != null) {
            listEngagementForm.apply(selected);
        }
        formCards.show(formSlot, "listEngagement");
        resultLabel.setText("List engagement");
    }

    /** F8. List of weapon configurations. The name drop-down loads one configuration for display. */
    private void onF8() {
        String[] names = weaponBackend.names();
        if (names.length == 0) {
            resultLabel.setText("Create a weapon configuration first");
            return;
        }
        listWeaponForm.setNames(names);
        WeaponConfig selected = weaponBackend.findByName(listWeaponForm.getSelectedName());
        if (selected != null) {
            listWeaponForm.apply(selected);
        }
        formCards.show(formSlot, "listWeapon");
        resultLabel.setText("List weapon configuration");
    }

    /** F9. */
    private void onF9() {
        resultLabel.setText("F9");
    }

    /** F10. */
    private void onF10() {
        resultLabel.setText("F10");
    }

    /** F11. */
    private void onF11() {
        resultLabel.setText("F11");
    }

    /** F12. */
    private void onF12() {
        resultLabel.setText("F12");
    }

    /** F1 after the key swap. The blue square still says F1. The command is B1. */
    private void onB1() {
        weaponForm.reset();
        formCards.show(formSlot, "wconfig");
        resultLabel.setText("Create weapon configuration");
    }

    /** F2 after the key swap. Modify Weapon Configuration for the chosen config name. */
    private void onB2() {
        String[] names = weaponBackend.names();
        if (names.length == 0) {
            resultLabel.setText("Create a weapon configuration first");
            return;
        }
        modifyWeaponForm.setNames(names);
        WeaponConfig selected = weaponBackend.findByName(modifyWeaponForm.getSelectedName());
        if (selected != null) {
            modifyWeaponForm.apply(selected);
        }
        formCards.show(formSlot, "modifyWeapon");
        resultLabel.setText("Modify weapon configuration");
    }

    /** F3 after the key swap. Delete Weapon Config. */
    private void onB3() {
        deleteWeaponForm.reset();
        formCards.show(formSlot, "deleteWeapon");
        resultLabel.setText("Delete weapon config");
    }

    /** B5. Slot 4 is F4, the swap, in both sets. */
    private void onB5() {
        resultLabel.setText("B5");
    }

    /** B6. */
    private void onB6() {
        resultLabel.setText("B6");
    }

    /** B7. */
    private void onB7() {
        resultLabel.setText("B7");
    }

    /** B8. */
    private void onB8() {
        resultLabel.setText("B8");
    }

    /** B9. */
    private void onB9() {
        resultLabel.setText("B9");
    }

    /** B10. */
    private void onB10() {
        resultLabel.setText("B10");
    }

    /** B11. */
    private void onB11() {
        resultLabel.setText("B11");
    }

    /** B12. */
    private void onB12() {
        resultLabel.setText("B12");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FunctionPage().setVisible(true);
            }
        });
    }
}

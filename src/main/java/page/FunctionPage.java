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
 * F1 opens Create Weapon Configuration. F2 opens Modify Weapon Configuration.
 * F3 opens Delete Weapon Config. F4 opens List of Weapon Configurations.
 * F5 opens List of Engagements. That list is the Engagement Parameters form,
 * read-only, and ENGM chooses which stored engagement to show.
 * The drop-down in the top bar chooses Eng or WC.
 * The top bar, the footer, and the COG/SOG panel stay on both pages.
 * WC replaces the left side and the area under COG/SOG.
 * F7 on WC swaps that page's footer. Eng has one footer and does not swap.
 */
public class FunctionPage extends JFrame {

    private static final long serialVersionUID = 1L;

    /** Words beside the blue F1–F12 squares on the Eng page. */
    private static final String[] FIRST_TEXT = {
        "Create WC", "Modify", "Delete", "List WC", "List Eng", "Op 6",
        "Op 7", "Op 8", "Op 9", "Op 10", "Op 11", "Op 12"
    };

    /** Words beside the blue squares on the WC page. */
    private static final String[] WC_TEXT = {
        "Guid Mode", "LL BR Out", "BR In", "CCM", "TCCM", "Self Guidance",
        "Manu", "WG End", "Avoid Target", "LLCHM", "--------", "Stop"
    };

    /** WC footer after F7. F7 swaps this set back. */
    private static final String[] WC_TUBE_TEXT = {
        "Tube 1", "LL Tube 2", "Tube 3", "Tube 4", "Tube 5", "Tube 6",
        "TORPS", "WG End", "Avoid Target", "LLCHM", "--------", "Stop"
    };

    private static final Color BLUE = new Color(46, 134, 222);
    private static final Color DARK = new Color(28, 28, 30);
    private static final int FORM_WIDTH = 640;

    private final JLabel resultLabel = new JLabel(" ");
    private final CardLayout formCards = new CardLayout();
    private final JPanel formSlot = new JPanel(formCards);
    private EngagementParametersPanel parameters;
    private CreateWeaponConfigPanel weaponForm;
    private ModifyWeaponConfigPanel modifyWeaponForm;
    private DeleteWeaponConfigPanel deleteWeaponForm;
    private ListWeaponConfigPanel listWeaponForm;
    private ListEngagementPanel listEngagementForm;
    private EngagementService backend;
    private WeaponConfigService weaponBackend;
    private FunctionKeyBar footer;
    private boolean wcPage;
    private boolean wcTubeSet;
    private WcPage wcPagePanel;
    private final CardLayout leftCards = new CardLayout();
    private final JPanel leftSlot = new JPanel(leftCards);
    private final CardLayout rightCards = new CardLayout();
    private final JPanel rightBody = new JPanel(rightCards);
    private final JPanel rightColumn = new JPanel(new BorderLayout(0, 4));
    private final MotionReadout readout = new MotionReadout();
    private final JComboBox pageChoice = new JComboBox(new String[] {"Eng", "WC"});

    /**
     * One listener for every function button.
     * The button's action command says which key it was.
     */
    private final ActionListener functionListener = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            String key = event.getActionCommand();
            if (wcPage) {
                if ("F7".equals(key)) {
                    onWcF7();
                } else if (wcTubeSet) {
                    onWcTube(key);
                }
                return;
            }

            if ("F1".equals(key)) {
                onF1();
            } else if ("F2".equals(key)) {
                onF2();
            } else if ("F3".equals(key)) {
                onF3();
            } else if ("F4".equals(key)) {
                onF4();
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
        formSlot.add(weaponForm, "wconfig");
        formSlot.add(modifyWeaponForm, "modifyWeapon");
        formSlot.add(deleteWeaponForm, "deleteWeapon");
        formSlot.add(listWeaponForm, "listWeapon");
        formSlot.add(listEngagementForm, "listEngagement");
        formCards.show(formSlot, "parameters");

        WcPage wc = new WcPage();
        wcPagePanel = wc;
        leftSlot.setOpaque(false);
        leftSlot.add(left, "eng");
        leftSlot.add(wc.getBody(), "wc");
        leftCards.show(leftSlot, "eng");

        rightBody.setOpaque(false);
        rightBody.add(formSlot, "eng");
        rightBody.add(wc.getSide(), "wc");
        rightCards.show(rightBody, "eng");

        rightColumn.setOpaque(false);
        rightColumn.setPreferredSize(new Dimension(FORM_WIDTH, 100));
        rightColumn.add(readout, BorderLayout.NORTH);
        rightColumn.add(rightBody, BorderLayout.CENTER);

        JPanel stage = new JPanel(new BorderLayout(4, 0));
        stage.setBackground(DARK);
        stage.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        stage.add(leftSlot, BorderLayout.CENTER);
        stage.add(rightColumn, BorderLayout.EAST);

        page.add(topBar, BorderLayout.NORTH);
        page.add(stage, BorderLayout.CENTER);

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

    /** F1. Create Weapon Configuration. */
    private void onF1() {
        weaponForm.reset();
        formCards.show(formSlot, "wconfig");
        resultLabel.setText("Create weapon configuration");
    }

    /** F2. Modify Weapon Configuration for the chosen config name. */
    private void onF2() {
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

    /** F3. Delete Weapon Config. */
    private void onF3() {
        deleteWeaponForm.reset();
        formCards.show(formSlot, "deleteWeapon");
        resultLabel.setText("Delete weapon config");
    }

    /** F7 on the WC page. Swaps the bottom bar between the two WC word sets. */
    private void onWcF7() {
        wcTubeSet = !wcTubeSet;
        if (wcTubeSet) {
            footer.setLabels(FunctionKeyBar.KEYS, WC_TUBE_TEXT);
            resultLabel.setText("WC tubes");
        } else {
            footer.setLabels(FunctionKeyBar.KEYS, WC_TEXT);
            resultLabel.setText("WC");
        }
    }

    /** Tube 1 through Tube 6 on the swapped WC bar. */
    private void onWcTube(String key) {
        if ("F1".equals(key)) {
            wcPagePanel.showTube("01");
        } else if ("F2".equals(key)) {
            wcPagePanel.showTube("02");
        } else if ("F3".equals(key)) {
            wcPagePanel.showTube("03");
        } else if ("F4".equals(key)) {
            wcPagePanel.showTube("04");
        } else if ("F5".equals(key)) {
            wcPagePanel.showTube("05");
        } else if ("F6".equals(key)) {
            wcPagePanel.showTube("06");
        }
    }

    /** The top drop-down. Eng is the form screen. WC is the depth-window screen. */
    private void showChosenPage() {
        Object item = pageChoice.getSelectedItem();
        if ("WC".equals(item)) {
            wcPage = true;
            leftCards.show(leftSlot, "wc");
            rightCards.show(rightBody, "wc");
            rightColumn.setPreferredSize(new Dimension(300, 100));
            footer.setLabels(FunctionKeyBar.KEYS, wcTubeSet ? WC_TUBE_TEXT : WC_TEXT);
        } else {
            wcPage = false;
            leftCards.show(leftSlot, "eng");
            rightCards.show(rightBody, "eng");
            rightColumn.setPreferredSize(new Dimension(FORM_WIDTH, 100));
            footer.setLabels(FunctionKeyBar.KEYS, FIRST_TEXT);
        }
        rightColumn.revalidate();
    }

    /** The page that is showing when the window opens, and after OK or Cancel. */
    private void showParameters() {
        formCards.show(formSlot, "parameters");
    }

    /** F4. List of weapon configurations. The name drop-down loads one configuration for display. */
    private void onF4() {
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

    /** F5. List of engagements. Same form as Engagement Parameters, for display. */
    private void onF5() {
        String[] names = backend.names();
        listEngagementForm.setNames(names);
        if (names.length > 0) {
            Engagement selected = backend.findByName(listEngagementForm.getSelectedName());
            if (selected != null) {
                listEngagementForm.apply(selected);
            }
            resultLabel.setText("List engagement");
        } else {
            resultLabel.setText("No engagements");
        }
        formCards.show(formSlot, "listEngagement");
    }

    /** F6. */
    private void onF6() {
        resultLabel.setText("F6");
    }

    /** F7. */
    private void onF7() {
        resultLabel.setText("F7");
    }

    /** F8. */
    private void onF8() {
        resultLabel.setText("F8");
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FunctionPage().setVisible(true);
            }
        });
    }
}

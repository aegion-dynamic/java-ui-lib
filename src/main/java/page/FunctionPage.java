package page;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

import page.backend.EngagementService;
import page.backend.WeaponConfigLists;
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
 * WC is one view, {@link WcPage}. The window shows that view, or replaces it
 * with {@link #setWireGuidance}. The top bar and the footer stay on the window.
 * COG/SOG is one strip. It sits on Eng, and moves onto the WC view while WC is showing.
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
    /** The filtered linked list for the form that is open. */
    private List modifyChoices = new LinkedList();
    private List listChoices = new LinkedList();
    private List deleteChoices = new LinkedList();
    private FunctionKeyBar footer;
    private boolean wcPage;
    private boolean wcTubeSet;
    private WcPage wcPagePanel;
    private final CardLayout stageCards = new CardLayout();
    private final JPanel stageDeck = new JPanel(stageCards);
    private final JPanel rightColumn = new JPanel(new BorderLayout(0, 4));
    private final MotionReadout readout = new MotionReadout();
    private CommonHeader header;

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

        header = new CommonHeader(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                onHeader(event.getActionCommand());
            }
        });

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
        modifyWeaponForm.addWeaponListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loadModifyChoices();
            }
        });
        modifyWeaponForm.addNameListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (modifyWeaponForm.isFilling()) {
                    return;
                }
                WeaponConfig selected = WeaponConfigLists.named(modifyChoices, modifyWeaponForm.getSelectedName());
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
        deleteWeaponForm.addWeaponListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loadDeleteChoices();
            }
        });
        deleteWeaponForm.getOkButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String name = deleteWeaponForm.getConfigName();
                weaponBackend.deleteMatch(deleteWeaponForm.getWeaponState(), name);
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
        listWeaponForm.addWeaponListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loadListChoices();
            }
        });
        listWeaponForm.addNameListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (listWeaponForm.isFilling()) {
                    return;
                }
                WeaponConfig selected = WeaponConfigLists.named(listChoices, listWeaponForm.getSelectedName());
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

        rightColumn.setOpaque(false);
        rightColumn.setPreferredSize(new Dimension(FORM_WIDTH, 100));
        rightColumn.add(readout, BorderLayout.NORTH);
        rightColumn.add(formSlot, BorderLayout.CENTER);

        JPanel engStage = new JPanel(new BorderLayout(4, 0));
        engStage.setBackground(DARK);
        engStage.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        engStage.add(left, BorderLayout.CENTER);
        engStage.add(rightColumn, BorderLayout.EAST);

        stageDeck.setOpaque(false);
        stageDeck.add(engStage, "eng");
        stageDeck.add(wcPagePanel, "wc");
        stageCards.show(stageDeck, "eng");

        page.add(header, BorderLayout.NORTH);
        page.add(stageDeck, BorderLayout.CENTER);

        /* bar height, button width, button height — change these to fit the other screen */
        footer = new FunctionKeyBar(64, 96, 40, functionListener);
        footer.setLabels(FunctionKeyBar.KEYS, FIRST_TEXT);
        page.add(footer, BorderLayout.SOUTH);

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

    /** F2. Modify. The config list stays empty until a weapon type is chosen. */
    private void onF2() {
        modifyChoices = new LinkedList();
        modifyWeaponForm.reset();
        modifyWeaponForm.setNames(new String[0]);
        formCards.show(formSlot, "modifyWeapon");
        resultLabel.setText("Modify weapon configuration");
    }

    /** F3. Delete. The config list stays empty until a weapon type is chosen. */
    private void onF3() {
        deleteChoices = new LinkedList();
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

    /** Home and the ENGM chip. The header stays put. Only the centre view changes. */
    private void onHeader(String command) {
        if (command.startsWith("page:")) {
            String name = command.substring(5);
            showPage("WC".equals(name));
            header.setPageName(name);
            return;
        }
        if ("page".equals(command)) {
            showPage(!wcPage);
            return;
        }
        resultLabel.setText(command);
    }

    /** Eng is the form screen. WC is the wire-guidance view. */
    private void showPage(boolean wire) {
        if (wire == wcPage) {
            header.setPageName(wire ? "WC" : "ENGM");
            return;
        }
        if (wire) {
            wcPage = true;
            rightColumn.remove(readout);
            wcPagePanel.placeReadout(readout);
            stageCards.show(stageDeck, "wc");
            footer.setLabels(FunctionKeyBar.KEYS, wcTubeSet ? WC_TUBE_TEXT : WC_TEXT);
        } else {
            wcPage = false;
            wcPagePanel.takeReadout();
            rightColumn.add(readout, BorderLayout.NORTH);
            stageCards.show(stageDeck, "eng");
            footer.setLabels(FunctionKeyBar.KEYS, FIRST_TEXT);
        }
        header.setPageName(wire ? "WC" : "ENGM");
        stageDeck.revalidate();
        stageDeck.repaint();
    }

    /** Swap the whole wire-guidance view. The footer and the top bar stay. */
    public void setWireGuidance(WcPage next) {
        if (next == null || next == wcPagePanel) {
            return;
        }
        if (wcPage) {
            wcPagePanel.takeReadout();
            rightColumn.add(readout, BorderLayout.NORTH);
        }
        stageDeck.remove(wcPagePanel);
        wcPagePanel = next;
        stageDeck.add(wcPagePanel, "wc");
        if (wcPage) {
            rightColumn.remove(readout);
            wcPagePanel.placeReadout(readout);
            stageCards.show(stageDeck, "wc");
        }
        stageDeck.revalidate();
        stageDeck.repaint();
    }

    /** The page that is showing when the window opens, and after OK or Cancel. */
    private void showParameters() {
        formCards.show(formSlot, "parameters");
    }

    /** F4. List. The config list stays empty until a weapon type is chosen. */
    private void onF4() {
        listChoices = new LinkedList();
        listWeaponForm.reset();
        listWeaponForm.setNames(new String[0]);
        formCards.show(formSlot, "listWeapon");
        resultLabel.setText("List weapon configuration");
    }

    /** EHWT WAR or EHWT EXE on Modify. Asks the backend before OK. */
    private void loadModifyChoices() {
        if (modifyWeaponForm.isFilling()) {
            return;
        }
        modifyChoices = WeaponConfigLists.filter(weaponBackend.all(), modifyWeaponForm.getWeaponState());
        showChoiceNames(modifyWeaponForm, modifyChoices);
    }

    /** EHWT WAR or EHWT EXE on List. Asks the backend before any other click. */
    private void loadListChoices() {
        if (listWeaponForm.isFilling()) {
            return;
        }
        listChoices = WeaponConfigLists.filter(weaponBackend.all(), listWeaponForm.getWeaponState());
        showChoiceNames(listWeaponForm, listChoices);
    }

    /** EHWT WAR or EHWT EXE on Delete. Asks the backend before OK. */
    private void loadDeleteChoices() {
        if (deleteWeaponForm.isFilling()) {
            return;
        }
        deleteChoices = WeaponConfigLists.filter(weaponBackend.all(), deleteWeaponForm.getWeaponState());
        deleteWeaponForm.setNames(WeaponConfigLists.names(deleteChoices));
    }

    /** Names from the filtered list go in the drop-down. The first configuration fills the fields. */
    private void showChoiceNames(CreateWeaponConfigPanel form, List found) {
        form.setNames(WeaponConfigLists.names(found));
        WeaponConfig first = WeaponConfigLists.first(found);
        if (first == null) {
            form.clearDetails();
            return;
        }
        form.apply(first);
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

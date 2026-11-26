package page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * The one top bar for Eng and wire guidance.
 * ECS adds this once, to itself. A child view does not add, hide, or rebuild it.
 * Each icon keeps its own state. A click advances that state and tells the listener.
 * Commands that change the PPI end with ":ppi".
 */
public class CommonHeader extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color BAR = new Color(16, 16, 18);
    private static final Color GREEN = new Color(46, 168, 74);
    private static final Color YELLOW = new Color(232, 196, 40);
    private static final int ICON = 28;

    private final ActionListener listener;
    private final JLabel pageChip;
    private final JPanel rangeSlot;
    private String rangeText = "0.125 Nm";
    private final JLabel monit;
    private boolean monitOn = true;
    private boolean mapOn;
    private String[] pages = new String[] {"ENGM", "WC"};
    private Tool homeTool;
    private JPopupMenu homeMenu;
    private Timer homeHide;

    public CommonHeader(ActionListener listener) {
        super(new BorderLayout());
        this.listener = listener;
        setBackground(BAR);
        setOpaque(true);
        setPreferredSize(new Dimension(100, 52));

        pageChip = chip("ENGM", GREEN, Color.BLACK);
        pageChip.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                tell("page");
            }
        });

        rangeSlot = new JPanel();
        rangeSlot.setOpaque(false);
        rangeSlot.setPreferredSize(new Dimension(118, 28));

        monit = chip("MONIT", YELLOW, Color.WHITE);
        monit.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                monitOn = !monitOn;
                monit.setBackground(monitOn ? YELLOW : new Color(48, 48, 52));
                monit.setForeground(Color.WHITE);
                tell(monitOn ? "monit:on" : "monit:off");
            }
        });

        JPanel tools = row();
        tools.add(homeButton());
        tools.add(gap());
        tools.add(pageChip);
        tools.add(gap());
        tools.add(cycle("label", false, new String[] {"label-mark.png", "label-degree.png"}));
        tools.add(cycle("vector", false, new String[] {
            "vector-degree.png", "vector-15.png", "vector-30.png", "vector-60.png"
        }));
        tools.add(cycle("trail", false, new String[] {"trail-off.png", "trail-off-degree.png"}));
        tools.add(cycle("trail-time", false, new String[] {
            "trail-15.png", "trail-30.png", "trail-60.png", "trail-4h.png"
        }));
        tools.add(cycle("dial", true, new String[] {"dial-green.png", "dial-red.png"}));
        tools.add(cycle("grid", true, new String[] {"grid-light.png", "grid-blue.png"}));
        tools.add(cycle("baffle", true, new String[] {"baffle-blue.png", "baffle-gray.png"}));
        tools.add(mapButton());
        tools.add(cycle("rring", true, new String[] {"ring-blue.png", "ring-red.png"}));

        JPanel side = row();
        side.add(cycle("north", false, new String[] {"north-n.png", "north-c.png"}));
        side.add(rangeSlot);
        side.add(cycle("relative", false, new String[] {"relative-rel.png", "relative-tru.png"}));
        side.add(monit);
        side.add(plain("clock", false, "clock.png"));
        side.add(plain("calculator", false, "calendar.png"));
        side.add(plain("stopwatch", false, "stopwatch.png"));

        add(tools, BorderLayout.WEST);
        add(side, BorderLayout.EAST);
    }

    /**
     * The names on the home hover list.
     * The selected name is sent to the listener as "page:" plus that name.
     */
    public void setPages(String[] names) {
        if (names == null || names.length == 0) {
            return;
        }
        pages = new String[names.length];
        System.arraycopy(names, 0, pages, 0, names.length);
    }

    /** The green chip. ECS sets this when the view changes. ENGM or WC. */
    public void setPageName(String name) {
        pageChip.setText(name);
    }

    /**
     * The empty slot between North and REL.
     * The 0.125 Nm button goes here when it is ready. Add it to this panel.
     */
    public JPanel getRangeSlot() {
        return rangeSlot;
    }

    /** Remembered for the 0.125 Nm button. The slot stays empty until that button is added. */
    public void setRangeText(String text) {
        rangeText = text == null ? "" : text;
    }

    public String getRangeText() {
        return rangeText;
    }

    public boolean isMapOn() {
        return mapOn;
    }

    private JPanel row() {
        JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        line.setOpaque(false);
        return line;
    }

    private static JLabel gap() {
        JLabel space = new JLabel(" ");
        space.setPreferredSize(new Dimension(8, 8));
        return space;
    }

    private JLabel chip(String text, Color background, Color foreground) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(foreground);
        label.setFont(new Font("Dialog", Font.BOLD, 13));
        label.setPreferredSize(new Dimension(72, 34));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return label;
    }

    private JLabel homeButton() {
        homeTool = new Tool("home", false, new ImageIcon[] {icon("home.png")}, false);
        homeTool.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent event) {
                showHomeMenu(homeTool);
            }

            public void mouseExited(MouseEvent event) {
                scheduleHomeHide();
            }
        });
        return homeTool;
    }

    private void showHomeMenu(final Component source) {
        cancelHomeHide();
        if (homeMenu != null && homeMenu.isVisible()) {
            return;
        }
        homeMenu = new JPopupMenu();
        homeMenu.setLightWeightPopupEnabled(false);
        homeMenu.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent event) {
                cancelHomeHide();
            }

            public void mouseExited(MouseEvent event) {
                scheduleHomeHide();
            }
        });
        for (int i = 0; i < pages.length; i++) {
            final String name = pages[i];
            JMenuItem item = new JMenuItem(name);
            item.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent event) {
                    cancelHomeHide();
                }

                public void mouseExited(MouseEvent event) {
                    scheduleHomeHide();
                }

                public void mousePressed(MouseEvent event) {
                    choosePage(name);
                }
            });
            homeMenu.add(item);
        }
        homeMenu.show(source, 0, source.getHeight());
    }

    private void choosePage(String name) {
        cancelHomeHide();
        if (homeMenu != null) {
            homeMenu.setVisible(false);
        }
        tell("page:" + name);
    }

    private void scheduleHomeHide() {
        if (homeHide == null) {
            homeHide = new Timer(300, new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if (pointerOverHome()) {
                        homeHide.restart();
                        return;
                    }
                    if (homeMenu != null) {
                        homeMenu.setVisible(false);
                    }
                }
            });
            homeHide.setRepeats(false);
        }
        homeHide.restart();
    }

    /** True while the pointer is still on the house or on the open list. */
    private boolean pointerOverHome() {
        PointerInfo info = MouseInfo.getPointerInfo();
        if (info == null) {
            return false;
        }
        Point screen = info.getLocation();
        return covers(homeMenu, screen, 8) || covers(homeTool, screen, 4);
    }

    private static boolean covers(Component component, Point screen, int pad) {
        if (component == null || !component.isShowing()) {
            return false;
        }
        Point origin = component.getLocationOnScreen();
        Rectangle area = new Rectangle(
                origin.x - pad, origin.y - pad,
                component.getWidth() + pad * 2, component.getHeight() + pad * 2);
        return area.contains(screen);
    }

    private void cancelHomeHide() {
        if (homeHide != null) {
            homeHide.stop();
        }
    }

    private JLabel cycle(String name, boolean ppi, String[] files) {
        ImageIcon[] frames = new ImageIcon[files.length];
        for (int i = 0; i < files.length; i++) {
            frames[i] = icon(files[i]);
        }
        return new Tool(name, ppi, frames, true);
    }

    private JLabel plain(final String name, boolean ppi, String file) {
        final Tool tool = new Tool(name, ppi, new ImageIcon[] {icon(file)}, false);
        if ("clock".equals(name)) {
            tool.setExtra(new Runnable() {
                public void run() {
                    showClocks(tool);
                }
            });
        } else if ("calculator".equals(name)) {
            tool.setExtra(new Runnable() {
                public void run() {
                    showCalculator();
                }
            });
        } else if ("stopwatch".equals(name)) {
            tool.setExtra(new Runnable() {
                public void run() {
                    showStopwatch();
                }
            });
        }
        return tool;
    }

    private JLabel mapButton() {
        final Tool tool = new Tool("map", true, new ImageIcon[] {icon("diamond.png")}, false);
        tool.setExtra(new Runnable() {
            public void run() {
                showMapMenu(tool);
            }
        });
        return tool;
    }

    private void showMapMenu(final Component source) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem map = new JMenuItem(mapOn ? "Map Off" : "Map On");
        map.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mapOn = !mapOn;
                tell(mapOn ? "map:on:ppi" : "map:off:ppi");
            }
        });
        JMenuItem zoom = new JMenuItem("Map Zoom");
        zoom.setEnabled(false);
        JMenu scheme = new JMenu("Color Scheme");
        String[] colors = {
            "Day Bright", "Day_Black Back", "Dusk_Black Back", "Night_Bright", "Night_Filter"
        };
        for (int i = 0; i < colors.length; i++) {
            final String color = colors[i];
            JMenuItem item = new JMenuItem(color);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    tell("color:" + color + ":ppi");
                }
            });
            scheme.add(item);
        }
        JMenuItem group = new JMenuItem("Display Group");
        group.setEnabled(false);
        menu.add(map);
        menu.add(zoom);
        menu.add(scheme);
        menu.add(group);
        menu.show(source, 0, source.getHeight());
    }

    private void showClocks(Component source) {
        final JPopupMenu menu = new JPopupMenu();
        final JLabel mumbai = new JLabel(" ");
        final JLabel gmt = new JLabel(" ");
        mumbai.setBorder(BorderFactory.createEmptyBorder(6, 10, 2, 10));
        gmt.setBorder(BorderFactory.createEmptyBorder(2, 10, 6, 10));
        menu.add(mumbai);
        menu.add(gmt);
        final SimpleDateFormat mumbaiClock = clock("Asia/Kolkata");
        final SimpleDateFormat gmtClock = clock("GMT+2");
        final Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Date now = new Date();
                mumbai.setText("Mumbai  " + mumbaiClock.format(now));
                gmt.setText("GMT+2   " + gmtClock.format(now));
            }
        });
        timer.setInitialDelay(0);
        timer.start();
        menu.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
                timer.stop();
            }

            public void popupMenuCanceled(PopupMenuEvent event) {
                timer.stop();
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
            }
        });
        menu.show(source, 0, source.getHeight());
    }

    private static SimpleDateFormat clock(String zone) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone(zone));
        return format;
    }

    private void showCalculator() {
        final JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Calculator");
        dialog.setLayout(new BorderLayout());
        final JTextField display = new JTextField("0");
        display.setEditable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFont(new Font("Dialog", Font.BOLD, 18));
        dialog.add(display, BorderLayout.NORTH);
        JPanel keys = new JPanel(new GridLayout(4, 4, 4, 4));
        String[] labels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", "C", "=", "+"
        };
        final String[] pending = new String[] {""};
        final double[] left = new double[] {0};
        final boolean[] fresh = new boolean[] {true};
        for (int i = 0; i < labels.length; i++) {
            final String label = labels[i];
            JButton key = new JButton(label);
            key.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    pressCalculator(display, label, pending, left, fresh);
                }
            });
            keys.add(key);
        }
        dialog.add(keys, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private static void pressCalculator(JTextField display, String key, String[] pending,
            double[] left, boolean[] fresh) {
        if ("C".equals(key)) {
            display.setText("0");
            pending[0] = "";
            left[0] = 0;
            fresh[0] = true;
            return;
        }
        if ("=".equals(key)) {
            display.setText(applyOp(left[0], pending[0], number(display.getText())));
            pending[0] = "";
            fresh[0] = true;
            return;
        }
        if ("+".equals(key) || "-".equals(key) || "*".equals(key) || "/".equals(key)) {
            left[0] = number(display.getText());
            pending[0] = key;
            fresh[0] = true;
            return;
        }
        if (fresh[0] || "0".equals(display.getText())) {
            display.setText(key);
            fresh[0] = false;
        } else {
            display.setText(display.getText() + key);
        }
    }

    private static String applyOp(double left, String op, double right) {
        double result = right;
        if ("+".equals(op)) {
            result = left + right;
        } else if ("-".equals(op)) {
            result = left - right;
        } else if ("*".equals(op)) {
            result = left * right;
        } else if ("/".equals(op) && right != 0) {
            result = left / right;
        }
        if (result == Math.rint(result)) {
            return Long.toString(Math.round(result));
        }
        return Double.toString(result);
    }

    private static double number(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void showStopwatch() {
        final JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Stopwatch");
        final JLabel time = new JLabel("00:00.0", SwingConstants.CENTER);
        time.setFont(new Font("Dialog", Font.BOLD, 28));
        time.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        final long[] started = new long[] {0};
        final long[] accumulated = new long[] {0};
        final boolean[] running = new boolean[] {false};
        final Timer timer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                long elapsed = accumulated[0];
                if (running[0]) {
                    elapsed += System.currentTimeMillis() - started[0];
                }
                time.setText(formatWatch(elapsed));
            }
        });
        final JButton toggle = new JButton("Start");
        toggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JButton button = (JButton) event.getSource();
                if (running[0]) {
                    accumulated[0] += System.currentTimeMillis() - started[0];
                    running[0] = false;
                    button.setText("Start");
                } else {
                    started[0] = System.currentTimeMillis();
                    running[0] = true;
                    button.setText("Stop");
                    timer.start();
                }
            }
        });
        JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                running[0] = false;
                accumulated[0] = 0;
                time.setText("00:00.0");
                toggle.setText("Start");
            }
        });
        JPanel buttons = new JPanel();
        buttons.add(toggle);
        buttons.add(reset);
        dialog.add(time, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private static String formatWatch(long millis) {
        long tenths = (millis / 100) % 10;
        long seconds = (millis / 1000) % 60;
        long minutes = millis / 60000;
        String secondText = seconds < 10 ? "0" + seconds : Long.toString(seconds);
        String minuteText = minutes < 10 ? "0" + minutes : Long.toString(minutes);
        return minuteText + ":" + secondText + "." + tenths;
    }

    private void tell(String command) {
        if (listener != null) {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command));
        }
    }

    private static ImageIcon icon(String name) {
        URL url = CommonHeader.class.getResource("/icons/" + name);
        if (url == null) {
            return new ImageIcon();
        }
        Image scaled = new ImageIcon(url).getImage().getScaledInstance(ICON, ICON, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private final class Tool extends JLabel {

        private static final long serialVersionUID = 1L;

        private final String name;
        private final boolean ppi;
        private final ImageIcon[] frames;
        private final boolean cycles;
        private int state;
        private Runnable extra;

        private Tool(String name, boolean ppi, ImageIcon[] frames, boolean cycles) {
            this.name = name;
            this.ppi = ppi;
            this.frames = frames;
            this.cycles = cycles;
            setIcon(frames[0]);
            setPreferredSize(new Dimension(36, 36));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent event) {
                    pressed();
                }
            });
        }

        private void setExtra(Runnable extra) {
            this.extra = extra;
        }

        private void pressed() {
            if (cycles && frames.length > 1) {
                state = (state + 1) % frames.length;
                setIcon(frames[state]);
            }
            if (extra != null) {
                extra.run();
            }
            if ("home".equals(name)) {
                return;
            }
            String command = cycles ? name + ":" + state : name;
            if (ppi) {
                command = command + ":ppi";
            }
            tell(command);
        }
    }
}

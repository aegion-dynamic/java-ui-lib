package page;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import page.form.Mono;

/**
 * Depth Window curve. Nothing is drawn until data arrives.
 * setBulk replaces the whole line. addPoint appends one sample.
 * The gold cross follows the mouse along X. Its Y is the curve at that X.
 * X min, X max, Y min, and Y max only change how much of that line is in view.
 */
public class DepthGraph extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color BG = new Color(18, 18, 20);
    private static final Color AXIS = new Color(186, 186, 186);
    private static final Color CURVE = new Color(226, 226, 226);
    private static final Color GOLD = new Color(214, 186, 96);
    private static final Font LABEL = new Font("Dialog", Font.PLAIN, 12);

    private final Plot plot = new Plot();
    private final JTextField xMinField = Mono.number(6);
    private final JTextField xMaxField = Mono.number(6);
    private final JTextField yMinField = Mono.number(6);
    private final JTextField yMaxField = Mono.number(6);

    private double xMin = 0;
    private double xMax = 40;
    private double yMin = 0;
    private double yMax = 10;
    private final ArrayList<Double> xs = new ArrayList<Double>();
    private final ArrayList<Double> ys = new ArrayList<Double>();
    private double markerX;
    private double markerY;
    private boolean markerOn;

    public DepthGraph() {
        super(new BorderLayout());
        setBackground(BG);
        setOpaque(true);
        xMinField.setText("0");
        xMaxField.setText("40");
        yMinField.setText("0");
        yMaxField.setText("10");
        add(plot, BorderLayout.CENTER);
        add(rangeBar(), BorderLayout.SOUTH);
        listen(xMinField);
        listen(xMaxField);
        listen(yMinField);
        listen(yMaxField);
    }

    /** Visible x axis. The max has to be greater than the min. */
    public void setXRange(double min, double max) {
        if (max <= min) {
            return;
        }
        xMin = min;
        xMax = max;
        xMinField.setText(format(min));
        xMaxField.setText(format(max));
        plot.repaint();
    }

    /** Visible y axis. The max has to be greater than the min. */
    public void setYRange(double min, double max) {
        if (max <= min) {
            return;
        }
        yMin = min;
        yMax = max;
        yMinField.setText(format(min));
        yMaxField.setText(format(max));
        plot.repaint();
    }

    /**
     * Replace the line with this set. Both arrays are the same length.
     * The cross stays where the mouse left it.
     */
    public void setBulk(double[] x, double[] y) {
        if (x == null || y == null || x.length != y.length) {
            return;
        }
        xs.clear();
        ys.clear();
        for (int i = 0; i < x.length; i++) {
            xs.add(new Double(x[i]));
            ys.add(new Double(y[i]));
        }
        plot.repaint();
    }

    /** Append one sample. The cross stays where the mouse left it. */
    public void addPoint(double x, double y) {
        xs.add(new Double(x));
        ys.add(new Double(y));
        plot.repaint();
    }

    /** Drop every sample. The axes stay. */
    public void clear() {
        xs.clear();
        ys.clear();
        plot.repaint();
    }

    /** Move the cross without adding a sample. */
    public void setMarker(double x, double y) {
        markerX = x;
        markerY = y;
        markerOn = true;
        plot.repaint();
    }

    private JPanel rangeBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bar.setBackground(BG);
        bar.setOpaque(true);
        bar.add(caption("X min"));
        bar.add(xMinField);
        bar.add(caption("X max"));
        bar.add(xMaxField);
        bar.add(caption("Y min"));
        bar.add(yMinField);
        bar.add(caption("Y max"));
        bar.add(yMaxField);
        return bar;
    }

    private static JLabel caption(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(GOLD);
        label.setFont(LABEL);
        return label;
    }

    private void listen(JTextField field) {
        field.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                applyRanges();
            }
        });
        field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event) {
                applyRanges();
            }
        });
    }

    private void applyRanges() {
        Double nextXMin = read(xMinField);
        Double nextXMax = read(xMaxField);
        Double nextYMin = read(yMinField);
        Double nextYMax = read(yMaxField);
        if (nextXMin == null || nextXMax == null || nextYMin == null || nextYMax == null) {
            return;
        }
        if (nextXMax.doubleValue() <= nextXMin.doubleValue()) {
            return;
        }
        if (nextYMax.doubleValue() <= nextYMin.doubleValue()) {
            return;
        }
        xMin = nextXMin.doubleValue();
        xMax = nextXMax.doubleValue();
        yMin = nextYMin.doubleValue();
        yMax = nextYMax.doubleValue();
        plot.repaint();
    }

    private static Double read(JTextField field) {
        String text = field.getText().trim();
        if (text.length() == 0 || ".".equals(text)) {
            return null;
        }
        try {
            return new Double(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /** Y of the line at this X, or null when the line does not pass that X. */
    private Double yOnGraph(double x) {
        Double found = null;
        for (int i = 0; i < xs.size() - 1; i++) {
            double x0 = xs.get(i).doubleValue();
            double x1 = xs.get(i + 1).doubleValue();
            double y0 = ys.get(i).doubleValue();
            double y1 = ys.get(i + 1).doubleValue();
            double lo = x0 < x1 ? x0 : x1;
            double hi = x0 < x1 ? x1 : x0;
            if (x < lo || x > hi) {
                continue;
            }
            if (x1 == x0) {
                found = new Double(y1);
                continue;
            }
            double t = (x - x0) / (x1 - x0);
            found = new Double(y0 + t * (y1 - y0));
        }
        return found;
    }

    private static String format(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.000001d) {
            return Long.toString(Math.round(value));
        }
        return Double.toString(value);
    }

    private final class Plot extends JPanel {

        private static final long serialVersionUID = 1L;

        private Plot() {
            setBackground(BG);
            setOpaque(true);
            setPreferredSize(new Dimension(420, 170));
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent event) {
                    track(event);
                }

                public void mouseDragged(MouseEvent event) {
                    track(event);
                }
            });
            addMouseListener(new MouseAdapter() {
                public void mouseExited(MouseEvent event) {
                    markerOn = false;
                    repaint();
                }
            });
        }

        private void track(MouseEvent event) {
            int left = 58;
            int top = 14;
            int plotW = getWidth() - left - 14;
            int plotH = getHeight() - top - 30;
            if (plotW < 20 || plotH < 20) {
                return;
            }
            int px = event.getX();
            int py = event.getY();
            if (px < left || px > left + plotW || py < top || py > top + plotH) {
                markerOn = false;
                repaint();
                return;
            }
            markerX = xMin + (px - left) * (xMax - xMin) / plotW;
            Double onCurve = yOnGraph(markerX);
            if (onCurve == null) {
                markerOn = false;
                repaint();
                return;
            }
            markerY = onCurve.doubleValue();
            markerOn = true;
            repaint();
        }

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int left = 58;
            int top = 14;
            int plotW = getWidth() - left - 14;
            int plotH = getHeight() - top - 30;
            if (plotW < 20 || plotH < 20) {
                g.dispose();
                return;
            }

            int originX = xPixel(0, left, plotW);
            int originY = yPixel(0, top, plotH);
            boolean xZero = xMin <= 0 && xMax >= 0;
            boolean yZero = yMin <= 0 && yMax >= 0;
            int axisX = xZero ? originX : left;
            int axisY = yZero ? originY : top + plotH;

            g.setColor(AXIS);
            g.setStroke(new BasicStroke(1f));
            g.drawLine(axisX, top, axisX, top + plotH);
            g.drawLine(left, axisY, left + plotW, axisY);
            drawTicks(g, left, top, plotW, plotH, axisX, axisY);

            Graphics2D clipped = (Graphics2D) g.create();
            clipped.setClip(left, top, plotW, plotH);
            clipped.setColor(CURVE);
            clipped.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawCurve(clipped, left, top, plotW, plotH);
            if (markerInside()) {
                clipped.setColor(GOLD);
                clipped.setStroke(new BasicStroke(1.2f));
                int mx = xPixel(markerX, left, plotW);
                int my = yPixel(markerY, top, plotH);
                clipped.drawLine(left, my, left + plotW, my);
                clipped.drawLine(mx, top, mx, top + plotH);
            }
            clipped.dispose();

            g.setFont(LABEL);
            g.setColor(GOLD);
            if (markerInside()) {
                int mx = xPixel(markerX, left, plotW);
                int my = yPixel(markerY, top, plotH);
                g.drawString("y", 8, my + 4);
                drawVertical(g, format(markerY), 22, my);
                g.drawString("x", axisX + 8, axisY - 6);
                g.drawString(format(markerX), mx + 6, axisY + 16);
            } else {
                g.drawString("y", 8, top + 14);
                g.drawString("x", left + 8, axisY - 6);
            }
            g.dispose();
        }

        private void drawCurve(Graphics2D g, int left, int top, int plotW, int plotH) {
            if (xs.size() < 2) {
                return;
            }
            int lastX = 0;
            int lastY = 0;
            boolean pen = false;
            for (int i = 0; i < xs.size(); i++) {
                int px = xPixel(xs.get(i).doubleValue(), left, plotW);
                int py = yPixel(ys.get(i).doubleValue(), top, plotH);
                if (pen) {
                    g.drawLine(lastX, lastY, px, py);
                }
                lastX = px;
                lastY = py;
                pen = true;
            }
        }

        private void drawTicks(Graphics2D g, int left, int top, int plotW, int plotH, int axisX, int axisY) {
            g.setFont(LABEL);
            g.setColor(GOLD);
            double xStep = tickStep(xMax - xMin);
            double yStep = tickStep(yMax - yMin);
            double xStart = Math.ceil(xMin / xStep) * xStep;
            for (double x = xStart; x <= xMax + xStep * 0.001; x += xStep) {
                int px = xPixel(x, left, plotW);
                g.setColor(AXIS);
                g.drawLine(px, axisY - 3, px, axisY + 3);
                g.setColor(GOLD);
                String text = format(x);
                int width = g.getFontMetrics().stringWidth(text);
                g.drawString(text, px - width / 2, axisY + 16);
            }
            double yStart = Math.ceil(yMin / yStep) * yStep;
            for (double y = yStart; y <= yMax + yStep * 0.001; y += yStep) {
                int py = yPixel(y, top, plotH);
                g.setColor(AXIS);
                g.drawLine(axisX - 3, py, axisX + 3, py);
                g.setColor(GOLD);
                g.drawString(format(y), axisX - 28, py + 4);
            }
        }

        private void drawVertical(Graphics2D g, String text, int x, int y) {
            Graphics2D turned = (Graphics2D) g.create();
            turned.rotate(-Math.PI / 2, x, y);
            int width = turned.getFontMetrics().stringWidth(text);
            turned.drawString(text, x - width / 2, y);
            turned.dispose();
        }

        private boolean markerInside() {
            return markerOn
                && markerX >= xMin && markerX <= xMax
                && markerY >= yMin && markerY <= yMax;
        }

        private int xPixel(double x, int left, int plotW) {
            double t = (x - xMin) / (xMax - xMin);
            if (t < -1) {
                t = -1;
            } else if (t > 2) {
                t = 2;
            }
            return left + (int) Math.round(t * plotW);
        }

        private int yPixel(double y, int top, int plotH) {
            double t = (yMax - y) / (yMax - yMin);
            if (t < -1) {
                t = -1;
            } else if (t > 2) {
                t = 2;
            }
            return top + (int) Math.round(t * plotH);
        }
    }

    private static double tickStep(double span) {
        if (span <= 0) {
            return 1;
        }
        double raw = span / 4.0;
        double mag = Math.pow(10, Math.floor(Math.log(raw) / Math.log(10)));
        if (mag == 0) {
            return 1;
        }
        double norm = raw / mag;
        double step;
        if (norm < 1.5) {
            step = 1;
        } else if (norm < 3.5) {
            step = 2;
        } else if (norm < 7.5) {
            step = 5;
        } else {
            step = 10;
        }
        return step * mag;
    }
}

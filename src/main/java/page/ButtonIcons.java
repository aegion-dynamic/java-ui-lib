package page;

import javax.swing.ImageIcon;

/**
 * Pictures for the weapon-combat toolbar buttons.
 *
 * On the reference sheet, each dashed box is one button. The pictures
 * inside that box are the variants of that button. These icons have no
 * background, so they sit on the dark panel the way they do on the sheet.
 * The files are in src/main/resources/icons. Most are 128 by 128.
 * The Trail pictures are 196 by 128, because the word is wider.
 */
public final class ButtonIcons {

    private ButtonIcons() {
    }

    /** Label. Blue L with the green tag. */
    public static final ImageIcon LABEL_MARK = load("label-mark.png");

    /** Label. Blue L with a degree mark. */
    public static final ImageIcon LABEL_DEGREE = load("label-degree.png");

    /** Vector. Blue V with a degree mark. */
    public static final ImageIcon VECTOR_DEGREE = load("vector-degree.png");

    /** Vector. Blue V with 15. */
    public static final ImageIcon VECTOR_15 = load("vector-15.png");

    /** Vector. Blue V with 30. */
    public static final ImageIcon VECTOR_30 = load("vector-30.png");

    /** Vector. Blue V with 60. */
    public static final ImageIcon VECTOR_60 = load("vector-60.png");

    /** Trail Off. Green Trail with a degree mark. */
    public static final ImageIcon TRAIL_OFF_DEGREE = load("trail-off-degree.png");

    /** Trail Off. Green Trail with no mark. */
    public static final ImageIcon TRAIL_OFF = load("trail-off.png");

    /** Trail. Green Trail with 15. */
    public static final ImageIcon TRAIL_15 = load("trail-15.png");

    /** Trail. Green Trail with 30. */
    public static final ImageIcon TRAIL_30 = load("trail-30.png");

    /** Trail. Green Trail with 60. */
    public static final ImageIcon TRAIL_60 = load("trail-60.png");

    /** Trail. Green Trail with 4H. */
    public static final ImageIcon TRAIL_4H = load("trail-4h.png");

    /** Dial. Green center, white ring. */
    public static final ImageIcon DIAL_GREEN = load("dial-green.png");

    /** Dial. Red center, pale ring. */
    public static final ImageIcon DIAL_RED = load("dial-red.png");

    /** R Ring. Gray disc with two blue rings. */
    public static final ImageIcon RING_BLUE = load("ring-blue.png");

    /** R Ring. Gray disc with two red rings. */
    public static final ImageIcon RING_RED = load("ring-red.png");

    /** Grid. Light gray cells. */
    public static final ImageIcon GRID_LIGHT = load("grid-light.png");

    /** Grid. Blue cells. */
    public static final ImageIcon GRID_BLUE = load("grid-blue.png");

    /** Baffle. Blue ring, red and green triangle. */
    public static final ImageIcon BAFFLE_BLUE = load("baffle-blue.png");

    /** Baffle. Gray ring, red and green triangle. */
    public static final ImageIcon BAFFLE_GRAY = load("baffle-gray.png");

    /** North Up. Red ring with N. */
    public static final ImageIcon NORTH_N = load("north-n.png");

    /** North Up. Green ring with C. */
    public static final ImageIcon NORTH_C = load("north-c.png");

    /** Relative. Red ring with REL. */
    public static final ImageIcon RELATIVE_REL = load("relative-rel.png");

    /** Relative. Blue ring with Tru. */
    public static final ImageIcon RELATIVE_TRU = load("relative-tru.png");

    /** Gray diamond. Red tip, blue band above it. */
    public static final ImageIcon DIAMOND = load("diamond.png");

    /** Clock face. */
    public static final ImageIcon CLOCK = load("clock.png");

    /** Calendar, one red day. */
    public static final ImageIcon CALENDAR = load("calendar.png");

    /** Stopwatch. */
    public static final ImageIcon STOPWATCH = load("stopwatch.png");

    /** Home. White house, red roof. */
    public static final ImageIcon HOME = load("home.png");

    private static ImageIcon load(String fileName) {
        java.net.URL url = ButtonIcons.class.getResource("/icons/" + fileName);
        if (url == null) {
            throw new IllegalStateException("Missing icon /icons/" + fileName);
        }
        return new ImageIcon(url);
    }
}

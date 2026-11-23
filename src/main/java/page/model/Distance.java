package page.model;

/**
 * A distance in metres, stored as a float.
 * Enb Dist, UDL, LDL, and Max RD use this.
 */
public final class Distance {

    private final float meters;

    public Distance(float meters) {
        this.meters = meters;
    }

    public float getMeters() {
        return meters;
    }

    /** Blank text is no distance. Anything that is not a number is no distance. */
    public static Distance parse(String text) {
        if (Numbers.blank(text)) {
            return null;
        }
        try {
            return new Distance(Float.parseFloat(text.trim()));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public String toString() {
        return Numbers.format(meters);
    }
}

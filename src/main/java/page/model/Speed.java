package page.model;

/**
 * A speed in knots. Knots are not an SI unit.
 * Initial Speed and Search Speed use this.
 */
public final class Speed {

    private final double knots;

    public Speed(double knots) {
        this.knots = knots;
    }

    public double getKnots() {
        return knots;
    }

    /** Blank text is no speed. Anything that is not a number is no speed. */
    public static Speed parse(String text) {
        if (Numbers.blank(text)) {
            return null;
        }
        try {
            return new Speed(Double.parseDouble(text.trim()));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public boolean equals(Object other) {
        if (!(other instanceof Speed)) {
            return false;
        }
        return Double.doubleToLongBits(knots) == Double.doubleToLongBits(((Speed) other).knots);
    }

    public int hashCode() {
        long bits = Double.doubleToLongBits(knots);
        return (int) (bits ^ (bits >>> 32));
    }

    public String toString() {
        return Numbers.format(knots);
    }
}

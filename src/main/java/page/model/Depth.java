package page.model;

/** A depth in metres. Ordered Depth uses this. */
public final class Depth {

    private final double meters;

    public Depth(double meters) {
        this.meters = meters;
    }

    public double getMeters() {
        return meters;
    }

    /** Blank text is no depth. Anything that is not a number is no depth. */
    public static Depth parse(String text) {
        if (Numbers.blank(text)) {
            return null;
        }
        try {
            return new Depth(Double.parseDouble(text.trim()));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public String toString() {
        return Numbers.format(meters);
    }
}

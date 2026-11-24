package page.model;

/** Homing. Their enum name is InternalGuidanceMode. */
public enum InternalGuidanceMode {

    PRO_NAVIGATION("Pro Navigation"),
    INTERCEPT("Intercept");

    private final String label;

    private InternalGuidanceMode(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}

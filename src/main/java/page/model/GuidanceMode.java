package page.model;

/** The guidance field. Their enum name is GuidanceMode. */
public enum GuidanceMode {

    BG_RIDER_OUT("Bg Rider Out"),
    CCM("CCM"),
    TCCM("TCCM"),
    SELF_GUIDED("Self Guided");

    private final String label;

    private GuidanceMode(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}

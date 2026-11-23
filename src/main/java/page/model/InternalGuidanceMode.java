package page.model;

/** Guidance Mode. Their enum name is InternalGuidanceMode. */
public enum InternalGuidanceMode {

    BG_RIDER_OUT("Bg Rider Out"),
    CCM("CCM"),
    TCCM("TCCM"),
    SELF_GUIDED("Self Guided");

    private final String label;

    private InternalGuidanceMode(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}

package page.model;

/** Acoustic Head Mode. Their enum name is AcousticHeadMode. */
public enum AcousticHeadMode {

    PASSIVE("Passive"),
    SIMULTANEOUS("Simultaneous"),
    WAKE("Wake");

    private final String label;

    private AcousticHeadMode(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}

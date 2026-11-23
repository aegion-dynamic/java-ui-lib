package page.model;

/** ASH. Their enum name is AcousticHeadMode. The choices on the form are ON and OFF. */
public enum AcousticHeadMode {

    ON("ON"),
    OFF("OFF");

    private final String label;

    private AcousticHeadMode(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}

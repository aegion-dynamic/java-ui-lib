package page.model;

/** Wake Guidance. Their enum name is GuidanceMode. The choices on the form are ON and OFF. */
public enum GuidanceMode {

    ON("ON"),
    OFF("OFF");

    private final String label;

    private GuidanceMode(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}

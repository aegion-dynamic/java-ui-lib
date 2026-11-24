package page.model;

/** ASH and Wake Guidance. The only choices are OFF and ON. */
public enum OnOff {

    OFF("OFF"),
    ON("ON");

    private final String label;

    private OnOff(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}

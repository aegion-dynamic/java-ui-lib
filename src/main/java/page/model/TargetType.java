package page.model;

/** Attack Mode. Their enum name is TargetType. */
public enum TargetType {

    SUB_SURFACE("Sub Surface"),
    SURFACE("Surface");

    private final String label;

    private TargetType(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}

package page.model;

/** EHWT WAR and EHWT EXE. Their enum name is WeaponConfigState. */
public enum WeaponConfigState {

    EHWT_WAR("EHWT WAR"),
    EHWT_EXE("EHWT EXE");

    private final String label;

    private WeaponConfigState(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }

    public static WeaponConfigState fromLabel(String label) {
        if (label == null) {
            return null;
        }
        WeaponConfigState[] all = values();
        for (int i = 0; i < all.length; i++) {
            if (all[i].label.equals(label)) {
                return all[i];
            }
        }
        return null;
    }
}

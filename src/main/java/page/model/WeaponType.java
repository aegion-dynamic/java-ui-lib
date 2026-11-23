package page.model;

/** EHWT WAR and EHWT EXE. Their enum name is WeaponType. */
public enum WeaponType {

    EHWT_WAR("EHWT WAR"),
    EHWT_EXE("EHWT EXE");

    private final String label;

    private WeaponType(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }

    public static WeaponType fromLabel(String label) {
        if (label == null) {
            return null;
        }
        WeaponType[] all = values();
        for (int i = 0; i < all.length; i++) {
            if (all[i].label.equals(label)) {
                return all[i];
            }
        }
        return null;
    }
}

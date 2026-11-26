package page;

/**
 * List of weapon configurations. Same fields as Modify Weapon Configuration.
 * EHWT WAR / EHWT EXE stay clickable. That choice asks for the matching names.
 * Config name chooses which one to show. The other fields only display.
 */
public class ListWeaponConfigPanel extends CreateWeaponConfigPanel {

    private static final long serialVersionUID = 1L;

    public ListWeaponConfigPanel() {
        super("LIST OF WEAPON CONFIGURATIONS", true, true);
    }
}

package page;

/**
 * List of weapon configurations. Same fields as Modify Weapon Configuration.
 * The fields only display. Config name chooses which one to show.
 */
public class ListWeaponConfigPanel extends CreateWeaponConfigPanel {

    private static final long serialVersionUID = 1L;

    public ListWeaponConfigPanel() {
        super("LIST OF WEAPON CONFIGURATIONS", true, true);
    }
}

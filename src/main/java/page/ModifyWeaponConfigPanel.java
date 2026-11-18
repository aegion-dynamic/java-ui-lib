package page;

/**
 * Modify Weapon Configuration. A separate form from Create Weapon Configuration.
 * The header says MODIFY WEAPON CONFIGURATION.
 * Config name is a drop-down of names already stored.
 * Choosing a name loads that configuration into the rest of the fields.
 */
public class ModifyWeaponConfigPanel extends CreateWeaponConfigPanel {

    private static final long serialVersionUID = 1L;

    public ModifyWeaponConfigPanel() {
        super("MODIFY WEAPON CONFIGURATION", true);
    }
}

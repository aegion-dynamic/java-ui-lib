package page.backend;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import page.model.WeaponConfig;
import page.model.WeaponConfigState;

/**
 * Stores weapon configurations by config name.
 * Create adds one. Modify replaces the one with the same name.
 */
public class WeaponConfigService {

    private final List configs = new LinkedList();

    public void create(WeaponConfig config) {
        configs.add(config);
    }

    /** Replace the stored configuration with the same weapon type and name. */
    public void update(WeaponConfig config) {
        ListIterator it = configs.listIterator();
        while (it.hasNext()) {
            WeaponConfig stored = (WeaponConfig) it.next();
            if (sameWeapon(stored, config.getWeapon()) && sameName(stored, config.configNameText())) {
                it.set(config);
                return;
            }
        }
        configs.add(config);
    }

    /**
     * Remove the configuration with this weapon type and this name.
     * A WAR name does not remove an EXE configuration of the same name.
     */
    public boolean deleteMatch(WeaponConfigState weapon, String name) {
        if (weapon == null) {
            return false;
        }
        Iterator it = configs.iterator();
        while (it.hasNext()) {
            WeaponConfig stored = (WeaponConfig) it.next();
            if (weapon == stored.getWeapon() && sameName(stored, name)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public boolean deleteByName(String name) {
        Iterator it = configs.iterator();
        while (it.hasNext()) {
            WeaponConfig stored = (WeaponConfig) it.next();
            if (sameName(stored, name)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Every stored configuration, as a new linked list.
     * The page filters this list. This copy is not the list inside the service.
     */
    public List all() {
        return new LinkedList(configs);
    }

    /**
     * The stored configurations for one weapon type, as a linked list.
     * EHWT WAR and EHWT EXE are separate. A null type returns an empty list.
     */
    public List listByWeapon(WeaponConfigState weapon) {
        return WeaponConfigLists.filter(configs, weapon);
    }

    public WeaponConfig findByName(String name) {
        return WeaponConfigLists.named(configs, name);
    }

    public String[] names() {
        return WeaponConfigLists.names(configs);
    }

    private static boolean sameWeapon(WeaponConfig stored, WeaponConfigState weapon) {
        return weapon == stored.getWeapon();
    }

    private static boolean sameName(WeaponConfig stored, String name) {
        if (name == null) {
            return false;
        }
        return name.equals(stored.configNameText());
    }
}

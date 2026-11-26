package page.backend;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import page.model.WeaponConfig;
import page.model.WeaponConfigState;

/**
 * Turns the backend's list into the names on the form, then back into one configuration.
 * The list is usually a LinkedList. These methods walk it with an iterator.
 * They copy into a new list. They do not remove anything from the list the backend gave.
 */
public final class WeaponConfigLists {

    private WeaponConfigLists() {
    }

    /**
     * The configurations of one weapon type.
     * Pass the list the backend returned, and EHWT_WAR or EHWT_EXE.
     * The result is a new LinkedList. An empty list means that type has no configurations.
     */
    public static List filter(List configs, WeaponConfigState weapon) {
        List found = new LinkedList();
        if (configs == null || weapon == null) {
            return found;
        }
        Iterator it = configs.iterator();
        while (it.hasNext()) {
            Object item = it.next();
            if (!(item instanceof WeaponConfig)) {
                continue;
            }
            WeaponConfig stored = (WeaponConfig) item;
            if (weapon == stored.getWeapon()) {
                found.add(stored);
            }
        }
        return found;
    }

    /** The config names, in the same order, for the drop-down. */
    public static String[] names(List configs) {
        if (configs == null || configs.isEmpty()) {
            return new String[0];
        }
        String[] names = new String[configs.size()];
        int i = 0;
        Iterator it = configs.iterator();
        while (it.hasNext()) {
            WeaponConfig stored = (WeaponConfig) it.next();
            names[i] = stored.configNameText();
            i++;
        }
        return names;
    }

    /**
     * The configuration in this list whose name was chosen on the form.
     * Pass the filtered list, not the whole backend list.
     */
    public static WeaponConfig named(List configs, String name) {
        if (configs == null || name == null) {
            return null;
        }
        Iterator it = configs.iterator();
        while (it.hasNext()) {
            WeaponConfig stored = (WeaponConfig) it.next();
            if (name.equals(stored.configNameText())) {
                return stored;
            }
        }
        return null;
    }

    /** The first configuration in the list, or null when the list is empty. */
    public static WeaponConfig first(List configs) {
        if (configs == null) {
            return null;
        }
        Iterator it = configs.iterator();
        if (!it.hasNext()) {
            return null;
        }
        return (WeaponConfig) it.next();
    }
}

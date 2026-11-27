package page.backend;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import page.model.WeaponConfig;
import page.model.WeaponConfigState;

/**
 * The weapon-config collection.
 * Their system keeps these in a Map on a model collection.
 * This sample's map is the same shape. The key is the weapon type plus the config name.
 * {@link #getAllWc()} returns every model in that map.
 */
public class WeaponConfigService {

    /** Their static collection. The page reads this. It does not build a second store. */
    public static final WeaponConfigService modelCollection = new WeaponConfigService();

    private final Map configs = new HashMap();

    public void create(WeaponConfig config) {
        configs.put(key(config), config);
    }

    /** Replace the stored configuration with the same weapon type and name. */
    public void update(WeaponConfig config) {
        configs.put(key(config), config);
    }

    /**
     * Remove the configuration with this weapon type and this name.
     * A WAR name does not remove an EXE configuration of the same name.
     */
    public boolean deleteMatch(WeaponConfigState weapon, String name) {
        if (weapon == null) {
            return false;
        }
        return configs.remove(key(weapon, name)) != null;
    }

    public boolean deleteByName(String name) {
        Iterator it = configs.values().iterator();
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
     * Every weapon-config model in the map.
     * Their loop is {@code for (Model wc : collection.getAllWc())}.
     * This returns those models. It is not the map itself.
     */
    public Collection getAllWc() {
        return new LinkedList(configs.values());
    }

    /**
     * Every stored configuration, copied out of the map.
     * The page filters this list. This copy is not the map inside the collection.
     */
    public List all() {
        return WeaponConfigLists.takenFrom(getAllWc());
    }

    /**
     * The stored configurations for one weapon type, as a linked list.
     * EHWT WAR and EHWT EXE are separate. A null type returns an empty list.
     */
    public List listByWeapon(WeaponConfigState weapon) {
        return WeaponConfigLists.filter(all(), weapon);
    }

    public WeaponConfig findByName(String name) {
        return WeaponConfigLists.named(all(), name);
    }

    public String[] names() {
        return WeaponConfigLists.names(all());
    }

    private static String key(WeaponConfig config) {
        return key(config.getWeapon(), config.configNameText());
    }

    /** One map entry for one weapon type and one name. WAR and EXE do not share a key. */
    private static String key(WeaponConfigState weapon, String name) {
        String type = weapon == null ? "" : weapon.name();
        return type + "/" + (name == null ? "" : name);
    }

    private static boolean sameName(WeaponConfig stored, String name) {
        if (name == null) {
            return false;
        }
        return name.equals(stored.configNameText());
    }
}

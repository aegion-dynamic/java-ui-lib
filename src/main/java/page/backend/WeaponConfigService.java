package page.backend;

import java.util.ArrayList;
import java.util.List;

import page.model.WeaponConfig;

/**
 * Stores weapon configurations by config name.
 * Create adds one. Modify replaces the one with the same name.
 */
public class WeaponConfigService {

    private final List configs = new ArrayList();

    public void create(WeaponConfig config) {
        configs.add(config);
    }

    /** Replace the stored configuration that has the same name. */
    public void update(WeaponConfig config) {
        for (int i = 0; i < configs.size(); i++) {
            WeaponConfig stored = (WeaponConfig) configs.get(i);
            if (sameName(stored, config.getConfigName())) {
                configs.set(i, config);
                return;
            }
        }
        configs.add(config);
    }

    /**
     * Remove the configuration whose config name matches.
     * Returns true when one was removed.
     */
    public boolean deleteByName(String name) {
        for (int i = 0; i < configs.size(); i++) {
            WeaponConfig stored = (WeaponConfig) configs.get(i);
            if (sameName(stored, name)) {
                configs.remove(i);
                return true;
            }
        }
        return false;
    }

    public WeaponConfig findByName(String name) {
        for (int i = 0; i < configs.size(); i++) {
            WeaponConfig stored = (WeaponConfig) configs.get(i);
            if (sameName(stored, name)) {
                return stored;
            }
        }
        return null;
    }

    public String[] names() {
        String[] names = new String[configs.size()];
        for (int i = 0; i < configs.size(); i++) {
            WeaponConfig stored = (WeaponConfig) configs.get(i);
            names[i] = stored.getConfigName();
        }
        return names;
    }

    private static boolean sameName(WeaponConfig stored, String name) {
        if (name == null) {
            return false;
        }
        return name.equals(stored.getConfigName());
    }
}

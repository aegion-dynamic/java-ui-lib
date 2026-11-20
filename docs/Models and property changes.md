# Models and property changes

Java 1.6. A model is a class that holds the field values. It is the stand-in for a struct: private fields, a getter, and a setter. `WeaponConfig` and `Engagement` are models. They import no Swing classes.

`readForm()` builds a model from the controls. `apply` writes a model back onto the controls. A second form, or the backend, receives that object. The methods below are examples. This project does not contain these extra classes yet. `apply` and `applyWeapon` are the copies the sample uses today.

---

## 1. One model

```java
public class WeaponConfig {
    private String configName = "";
    private String weapon = "";

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName == null ? "" : configName;
    }

    public String getWeapon() {
        return weapon;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon == null ? "" : weapon;
    }
}
```

The panel keeps the text fields. OK asks the panel for a model, then hands the model to the presenter:

```java
WeaponConfig config = weaponForm.readForm();
weaponPresenter.save(config);
```

The presenter stores the `WeaponConfig`. It does not store `CreateWeaponConfigPanel`.

---

## 2. Pass one model into another

Two screens keep two objects. A copy method reads the source with getters and writes the target with setters. Copy only the fields the second screen shows.

```java
public void copyFrom(WeaponConfig source) {
    setWeapon(source.getWeapon());
    setConfigName(source.getConfigName());
    setAcousticHeadMode(source.getAcousticHeadMode());
    setAttackMode(source.getAttackMode());
    setWakeGuidance(source.getWakeGuidance());
    setGuidanceMode(source.getGuidanceMode());
}
```

Create OK is the call site. The same object fills the store and the engagement screen:

```java
WeaponConfig saved = weaponForm.readForm();
weaponPresenter.save(saved);
parameters.copyFrom(saved);
```

`EngagementParametersPanel.applyWeapon` is this copy in the sample. It writes weapon, config name, the four torpedo trajectory fields, the safety fields (Enb Dist, Sonar Range, UDL, LDL, ASH, Max RD), acoustic head mode, attack mode, wake guidance, and guidance mode.

A full copy makes a second `WeaponConfig` so the form and the stored row are separate objects:

```java
public WeaponConfig copy() {
    WeaponConfig other = new WeaponConfig();
    other.setWeapon(getWeapon());
    other.setConfigName(getConfigName());
    other.setOrderedSpeed(getOrderedSpeed());
    return other;
}
```

```java
WeaponConfig stored = weaponForm.readForm().copy();
weaponPresenter.save(stored);
```

Later edits on the form change the object `readForm()` just built. `stored` changes only when something calls its setters, or when OK copies again.

Passing one object to both sides shares it. `parameters` and `weaponPresenter` then hold the same reference, and a setter on that object changes both.

The modify path is the reverse. The presenter returns the stored model, and the form writes it onto its controls:

```java
WeaponConfig found = weaponPresenter.findByName(name);
if (found != null) {
    modifyWeaponForm.apply(found);
}
```

`apply` is `copyFrom` aimed at text fields, radios, and drop-downs. A text field uses `setText`. A radio uses `setSelected`. A drop-down uses `setSelectedItem` when that text is already in the list.

---

## 3. An interface for the store

An interface lists methods and holds no fields. The presenter implements it. The listener keeps the interface, so the form code calls `save` and `findByName` without naming the concrete class.

```java
public interface WeaponConfigStore {
    void save(WeaponConfig config);
    WeaponConfig findByName(String name);
}
```

```java
public class WeaponConfigService implements WeaponConfigStore {
    private final List configs = new ArrayList();

    public void save(WeaponConfig config) {
        configs.add(config);
    }

    public WeaponConfig findByName(String name) {
        for (int i = 0; i < configs.size(); i++) {
            WeaponConfig stored = (WeaponConfig) configs.get(i);
            if (name.equals(stored.getConfigName())) {
                return stored;
            }
        }
        return null;
    }
}
```

On the other system the class is `weaponPresenter`. Give that class `implements WeaponConfigStore` when its methods already match. The view holds the interface:

```java
private final WeaponConfigStore store;

public void onOk() {
    store.save(weaponForm.readForm());
}
```

---

## 4. A property tells listeners that a field changed

A property is one field plus its getter and setter. The setter stores the new value, then calls every listener that registered for that property. The listener is an interface with one method.

```java
public interface ConfigNameListener {
    void nameChanged(String oldName, String newName);
}
```

The model keeps the listeners in a list. Java 6 code here uses a raw `ArrayList` and a cast, matching the services in this project.

```java
public class WeaponConfig {
    private String configName = "";
    private final List nameListeners = new ArrayList();

    public void addConfigNameListener(ConfigNameListener listener) {
        nameListeners.add(listener);
    }

    public void setConfigName(String configName) {
        String oldName = this.configName;
        this.configName = configName == null ? "" : configName;
        if (this.configName.equals(oldName)) {
            return;
        }
        for (int i = 0; i < nameListeners.size(); i++) {
            ConfigNameListener listener = (ConfigNameListener) nameListeners.get(i);
            listener.nameChanged(oldName, this.configName);
        }
    }
}
```

Register once, with an anonymous class. A lambda is Java 8 and will not compile here.

```java
model.addConfigNameListener(new ConfigNameListener() {
    public void nameChanged(String oldName, String newName) {
        configNameField.setText(newName);
    }
});
```

After that, any `setConfigName("abc")` writes `abc` into the field. The form does not poll the model.

A second model can be the listener and copy that one property across:

```java
source.addConfigNameListener(new ConfigNameListener() {
    public void nameChanged(String oldName, String newName) {
        target.setConfigName(newName);
    }
});
```

`target.setConfigName` runs its own setter. If `target` has listeners, they hear the new value too.

---

## 5. The JDK property interface

`java.beans.PropertyChangeListener` is the same idea for every property on the object. `PropertyChangeSupport` stores the listeners and sends the event. `firePropertyChange` skips the call when the old and new values are equal.

```java
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class WeaponConfig {
    private String configName = "";
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void setConfigName(String configName) {
        String oldName = this.configName;
        this.configName = configName == null ? "" : configName;
        changes.firePropertyChange("configName", oldName, this.configName);
    }
}
```

The event carries the property name, the old value, and the new value. One listener can serve several fields:

```java
model.addPropertyChangeListener(new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent event) {
        String name = event.getPropertyName();
        if ("configName".equals(name)) {
            configNameField.setText((String) event.getNewValue());
        } else if ("weapon".equals(name)) {
            weaponGroup.setSelected((String) event.getNewValue());
        }
    }
});
```

`addPropertyChangeListener("configName", listener)` registers for that property only. The support object then calls that listener for `configName` alone.

Use one custom interface when a single field has a meaning of its own, such as the config name. Use `PropertyChangeListener` when many fields on the same model need to refresh a screen.

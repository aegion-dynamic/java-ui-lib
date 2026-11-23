# How data moves between forms

A form never hands its text boxes to another form. The values leave the screen as one plain object. That object is what the backend stores, and it is what a form uses when it needs to show those values again.

Two objects do this job:

| Object | Which forms use it | Backend |
|---|---|---|
| `Engagement` | Create, Modify, and List engagement | `EngagementService` |
| `WeaponConfig` | Create, Modify, and List weapon configuration, and Engagement Parameters | `WeaponConfigService` |

`readForm()` copies the screen into a new object. `apply(...)` copies an object back onto the screen. The backend methods only see the object.

In this sample the backend is a list kept in memory. On the other machine, the body of `create`, `update`, and `findByName` is replaced by the presenter method that already saves or loads. The form calls stay the same.

---

## 1. A name on Modify

This is the path when the user picks a name on Modify Engagement or Modify Weapon Configuration. List Engagement and List Weapon Configuration use the same path. The name goes out. The stored record comes back. The same form shows it.

```text
User picks a name on Modify
        |
        v
getSelectedName()                 the name string only
        |
        v
backend.findByName(name)          the backend looks up that name
        |
        v
Engagement or WeaponConfig        the stored record, or null
        |
        v
modifyForm.apply(record)          the same form fills its fields
```

Modify Weapon Configuration, in `FunctionPage`:

```java
WeaponConfig selected = weaponBackend.findByName(modifyWeaponForm.getSelectedName());
if (selected != null) {
    modifyWeaponForm.apply(selected);
}
```

Modify Engagement is the same shape. The name is `modifyForm.getSelectedName()`. The lookup is `backend.findByName`. The fill is `modifyForm.apply(selected)`.

The lookup runs twice:

1. When the form opens (`onF2` for an engagement, `onB2` for a weapon). The name list is loaded first, the first name is selected, and that record is shown.
2. When the user changes the name drop-down. The listener on that drop-down runs the same three lines.

`apply` writes the name back into the drop-down. That would fire the listener again. `isFilling()` is true during `apply` and during `setNames`, and the listener returns immediately, so the lookup does not repeat.

If `findByName` returns `null`, the form is left as it is. Nothing is cleared.

On the other machine, `findByName` is the presenter call that takes the name and returns their engagement or weapon configuration. `apply` is still what paints that result. Do not pass the panel into the presenter.

---

## 2. OK sends the form to the backend

OK reads the screen, then gives the object to the backend. Cancel does neither. Cancel only returns to Engagement Parameters.

```text
User presses OK
        |
        v
readForm()                        a new Engagement or WeaponConfig
        |
        v
backend.create or backend.update
        |
        v
Engagement Parameters is shown
```

| Form | OK calls | What is stored |
|---|---|---|
| Create Engagement | `backend.create` | a new `Engagement`. The name is `getField2()` |
| Modify Engagement | `backend.update` | replaces the `Engagement` with the same name |
| Create Weapon Configuration | `weaponBackend.create` | a new `WeaponConfig`. The name is `getConfigName()` |
| Modify Weapon Configuration | `weaponBackend.update` | replaces the `WeaponConfig` with the same name |

`update` matches on the name. Engagement name is `getField2()`. Weapon config name is `getConfigName()`. If no stored record has that name, `update` adds the object.

Delete sends only the typed name. See section 6.

---

## 3. Which form copies onto which form

Only the weapon configuration is copied onto another form.

Create Weapon Configuration OK and Modify Weapon Configuration OK do both of these, in this order:

1. `weaponBackend.create` or `weaponBackend.update` stores the `WeaponConfig`.
2. `parameters.applyWeapon(config)` writes that same object onto Engagement Parameters.

The fields that move are the weapon choice, config name, the four torpedo trajectory fields, the safety fields, the attack sonar range, acoustic head mode, attack mode, wake guidance, and guidance mode. Engagement Parameters then becomes the visible form.

Create Engagement OK and Modify Engagement OK do not write onto Engagement Parameters. They store the `Engagement` and show that screen. The engagement fields stay on the engagement forms.

List forms do not have OK. Choosing a name fills the list form only. Nothing is copied to Engagement Parameters, and nothing is saved.

```text
Create or Modify weapon  --OK-->  WeaponConfigService
                               \
                                -->  Engagement Parameters

Create or Modify engagement  --OK-->  EngagementService
                                      (Engagement Parameters is shown, not filled)

Modify or List, name changed  --name-->  findByName  --record-->  the same form
```

---

## 4. Where each step lives

| Step | Method | File |
|---|---|---|
| Read the engagement screen | `readForm()` | `CreateEngagementPanel` (Modify and List extend it) |
| Paint an engagement | `apply(Engagement)` | `CreateEngagementPanel` |
| Read the weapon screen | `readForm()` | `CreateWeaponConfigPanel` (Modify and List extend it) |
| Paint a weapon configuration | `apply(WeaponConfig)` | `CreateWeaponConfigPanel` |
| Paint a weapon configuration on the opening screen | `applyWeapon(WeaponConfig)` | `EngagementParametersPanel` |
| Save, replace, look up, and delete by name | `create`, `update`, `findByName`, `deleteByName`, `names` | `EngagementService`, `WeaponConfigService` |
| The OK listeners and the name listeners | the `ActionListener` blocks | `FunctionPage` |

The listener belongs in the view (`FunctionPage` here, `EngagementView` on the other machine). The model does not know which form is open. The backend does not know about text fields.

---

## 5. The backend sends a record, and properties fill the form

`findByName` does not touch the screen. It returns the object it stored. Each value on that object is a property: a private field with a getter. `apply` is the only method that reads those getters and writes the controls.

```text
backend.findByName(name)
        |
        v
WeaponConfig                      properties are still inside the object
        |
        v
apply(config)
        |
        +-- config.getConfigName()        -->  name drop-down
        +-- config.getInitialSpeed()      -->  Initial Speed
        +-- config.getOrderedSpeed()      -->  Ordered Speed text
        +-- config.getAsh()               -->  ASH drop-down
        +-- config.getAcousticHeadMode()  -->  Acoustic Head Mode
        +-- the other getters             -->  the matching control
```

The weapon form method, shortened to the lines that show the property:

```java
public void apply(WeaponConfig config) {
    filling = true;
    weapon.setSelected(written(config.getWeapon()));
    nameList.setSelectedItem(config.getConfigName());
    showChoice(initialSpeed, config.getInitialSpeed());
    orderedSpeed.setText(written(config.getOrderedSpeed()));
    showChoice(ash, config.getAsh());
    showChoice(acousticHeadMode, config.getAcousticHeadMode());
    comments.setText(config.getComments());
    filling = false;
}
```

`EngagementParametersPanel.applyWeapon` is the same idea aimed at the opening screen. It calls the same getters. A text field uses `setText`. A drop-down uses `setSelectedItem`.

Notes:

- The form is not allowed to read the private field. It asks the getter. `getOrderedSpeed()` is the Ordered Speed property. `getAsh()` is the ASH property.
- A text property (`getConfigName`, `getComments`, `getAcousticHeadMode`) is a `String`. `setText` or `setSelectedItem` receives that string.
- A measure property (`getOrderedSpeed`, `getEnbDist`) is a `Speed`, `Depth`, or `Distance`. `written` calls `toString()`, which is the number the box shows. A null property writes `""`.
- A choice property (`getAsh`, `getAttackMode`, `getWeapon`) is the enum. `setSelectedItem` finds the same constant already in the drop-down. `getWeapon()` is turned into the radio label `EHWT WAR` or `EHWT EXE` by `toString()`. A null choice clears the control.
- `apply` asks for every property once. It does not listen for later changes. If the backend changes a property after `apply` has returned, the form stays as it was until something calls `apply` again.
- Modify Engagement uses `apply(Engagement)`. Each line is `getField1()`, `getField2()`, `getGroup1()`, and the other getters on `Engagement`. `getField2()` is the engagement name.

---

## 6. Delete sends the typed name

Delete does not load a record and does not fill another form. OK reads the name the user typed and asks the backend to remove the stored object with that name.

```text
User types the name on Delete
        |
        v
getEngName() or getConfigName()     the name string only
        |
        v
backend.deleteByName(name)
        |
        v
Engagement Parameters is shown
```

Delete Weapon Config, in `FunctionPage`:

```java
String name = deleteWeaponForm.getConfigName();
weaponBackend.deleteByName(name);
```

Delete Engagement sends the Eng Name field, not the Eng No list:

```java
String name = deleteForm.getEngName();
backend.deleteByName(name);
```

The method on `WeaponConfigService`. `EngagementService.deleteByName` is the same loop, matching `getField2()`:

```java
public boolean deleteByName(String name) {
    for (int i = 0; i < configs.size(); i++) {
        WeaponConfig stored = (WeaponConfig) configs.get(i);
        if (name != null && name.equals(stored.getConfigName())) {
            configs.remove(i);
            return true;
        }
    }
    return false;
}
```

Notes:

- Only the typed name is sent. The EHWT WAR / EHWT EXE radios on Delete Weapon Config are not part of the lookup. Two configurations are different when their names differ.
- `true` means a stored object with that name was removed. `false` means the name was missing or blank, and the list is unchanged. Either way the screen returns to Engagement Parameters.
- Cancel does not call `deleteByName`.
- On the other machine, replace the body of `deleteByName` with the presenter method that already deletes by that name. The OK listener still passes the string from `getConfigName()` or `getEngName()`.

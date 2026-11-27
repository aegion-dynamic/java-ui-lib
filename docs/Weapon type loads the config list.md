# Weapon type loads the config list

This is for Modify, List, and Delete. Create is different. On Create you type a new name and OK saves it. These three pages do not show every saved name when they open.

The config-name box starts empty. It fills only after you click **EHWT WAR** or **EHWT EXE**. That click happens before OK. OK is a later step.

---

## 1. What you do on the screen

1. Open Modify (F2), Delete (F3), or List (F4).
2. Click **EHWT WAR** or **EHWT EXE**.
3. The Config name drop-down shows only the names of that type.
4. Click a name.
5. That configuration is written into the form.

On Modify and List, step 5 fills the speeds, depths, modes, and comments. On Delete the form only has the name, so step 5 leaves that name selected in the drop-down.

The first name in the list is selected for you. Its data is written into the form at once. Choosing another name replaces those fields.

If that weapon type has no saved configurations, the drop-down stays empty and the other fields are cleared. The radio you clicked stays selected.

---

## 2. Why it is two steps

The weapon type and the name do different jobs.

| When | What is sent | What comes back | What the form does |
|---|---|---|---|
| You click EHWT WAR or EHWT EXE | That one weapon type | The list of configurations of that type | Puts the names in Config name |
| You click a name | Nothing new. The list is already here | | Puts that one configuration into the fields |
| You click OK | The whole form | | Modify saves. Delete removes. List has no OK |

Do not wait for OK to fill the drop-down. Do not send the whole form when the radio is clicked. The radio sends only the weapon type.

Do not put every saved name in the drop-down when the page opens. A WAR name must not appear after you click EHWT EXE.

---

## 3. The three pieces

```text
the form          the page (EngagementView)        the backend
---------         -------------------------        -----------
radios            hears the radio click            keeps every
drop-down         asks for that weapon type        saved configuration
fields            puts the names in the drop-down
                  hears the name click
                  writes one configuration
                  into the fields
```

The form does not call the backend. The page does. The backend does not know about drop-downs. It only returns `WeaponConfig` objects.

---

## 4. Their collection is a map

The weapon configs are not a list they hand you. They sit in a `Map` on a model collection. The collection hangs off a static class. `getAllWc()` walks that map and gives you every model.

```java
ModelCollection a = SomeStaticClass.modelCollection;
for (Model wc : a.getAllWc()) {
    // wc is one weapon-config model from the map
}
```

In this sample that is:

```java
WeaponConfigService a = WeaponConfigService.modelCollection;
Collection allWc = a.getAllWc();
```

`getAllWc()` returns the models. It does not return the map. The map stays inside the collection. One entry is one weapon type plus one config name, so a WAR named `Alpha` and an EXE named `Alpha` are two entries.

Copy each model into a list. That list is what the form filters. Do not filter the map in place, and do not remove entries while you read.

```java
private List everyWeaponConfig() {
    Collection allWc = weaponBackend.getAllWc();
    List models = new LinkedList();
    Iterator it = allWc.iterator();
    while (it.hasNext()) {
        Object wc = it.next();
        if (wc instanceof WeaponConfig) {
            models.add(wc);
        }
    }
    return models;
}
```

If the method you have returns the `Map` itself, pass that map to `WeaponConfigLists.takenFrom`. It copies `map.values()`, which is the same set of models `getAllWc()` returns.

Change `WeaponConfig` and `getAllWc` to their class and method names. The loop stays the same.

---

## 5. Clicking the weapon type

Ask for their list, filter it, then put the names on the form.

```java
private List modifyChoices = new LinkedList();

modifyWeaponForm.addWeaponListener(new ActionListener() {
    public void actionPerformed(ActionEvent event) {
        if (modifyWeaponForm.isFilling()) {
            return;
        }
        List all = everyWeaponConfig();
        modifyChoices = WeaponConfigLists.filter(all, modifyWeaponForm.getWeaponState());
        modifyWeaponForm.setNames(WeaponConfigLists.names(modifyChoices));
        WeaponConfig first = WeaponConfigLists.first(modifyChoices);
        if (first == null) {
            modifyWeaponForm.clearDetails();
            return;
        }
        modifyWeaponForm.apply(first);
    }
});
```

`everyWeaponConfig()` is the loop in section 4. `getWeaponState()` is `EHWT_WAR`, `EHWT_EXE`, or null. It compares the words on the radios. Their enum has no `fromLabel`.

`isFilling()` is true while the form is writing values into itself. Ignore the click then, or the fields you just filled get wiped.

Store `modifyChoices`. The name click in the next section reads that same list. List and Delete do the same with `listChoices` and `deleteChoices`. Delete only calls `setNames`. It has no other fields to fill.

`first` writes the first configuration in as soon as the names appear. The drop-down shows that name. Choosing another name replaces the fields.

Opening the page does not ask for the list. F2, F3, and F4 call `reset()` and `setNames(new String[0])`.

---

## 6. Clicking a name

Find that name in the filtered linked list, then `apply` writes that object into the form.

```java
modifyWeaponForm.addNameListener(new ActionListener() {
    public void actionPerformed(ActionEvent event) {
        if (modifyWeaponForm.isFilling()) {
            return;
        }
        WeaponConfig selected = WeaponConfigLists.named(
                modifyChoices, modifyWeaponForm.getSelectedName());
        if (selected != null) {
            modifyWeaponForm.apply(selected);
        }
    }
});
```

`named` walks the filtered list with an iterator and returns the object whose `configNameText()` equals the drop-down. `apply` writes speeds, depths, modes, comments, and the name.

Use the filtered list. Do not search the whole backend list by name. Two types can share a name. The list from the radio click is the one that belongs on the form.

The name on their object may be a `char[]`. `configNameText()` is `new String` of that array, which is what the drop-down shows. If their getter has another name, change that one call inside `names` and `named`.

List locks the speeds and the modes. `apply` can still write them. The weapon radios stay clickable. Do not lock the radios on the list page.

Delete has no other fields. The clicked name is the selected item. OK reads it with `getConfigName()`.

---

## 7. OK is still later

| Page | OK |
|---|---|
| Modify | `readForm()`, then `update` for that weapon type and name, then show Engagement Parameters |
| Delete | `deleteMatch(getWeaponState(), getConfigName())`, then show Engagement Parameters |
| List | There is no OK. Cancel returns to Engagement Parameters |

`update` and `deleteMatch` must use the weapon type and the name together. A WAR configuration named `Alpha` must not replace or remove an EXE configuration named `Alpha`.

The radio click does not save and does not delete.

---

## 8. Try it

1. F1. Choose EHWT WAR, type a config name, set a speed, press OK.
2. F1 again. Choose EHWT EXE, type a different name, press OK.
3. F2. The name box is empty.
4. Click EHWT WAR. Only the WAR name is in the box, and its speed is in the form.
5. Click EHWT EXE. The WAR name is gone. The EXE name and its values are in the form.
6. F4 does the same, and the fields cannot be edited.
7. F3. Click one type, choose the name, press OK. The other type is still saved.

# Weapon configuration — how to add it

The other window is `EngagementView`. It extends `CommonView`. Footers, headers, and the shared pieces live on `CommonView`. `EngagementView` owns the page panels and decides which of them are visible.

The constructor receives three things:

```text
public EngagementView(
        CommonItem commonItem,
        presentation model for engagement configuration,    // stored as engagementPresenter
        presentation model for the weapon configuration collection)  // stored as weaponPresenter
```

`engagementPresenter` is the engagement side. `weaponPresenter` is the weapon side. The form’s OK should hand its values to `weaponPresenter`. It should not hand the panel over.

`WeaponConfigurationView` also extends `CommonView`. Its constructor receives the engagement view, the right end panel, the bottom end panel, and the common item:

```text
WeaponConfigurationView(
        EngagementView engagement,
        right end panel,
        bottom end panel,
        common item)
```

You reach the weapon pages from the engagement page. Swap the function keys, then press F1. That F1 action is the one that shows the weapon configuration forms.

Inside `EngagementView` the screen is a set of panels: a left panel, a right panel, an engagement right panel, and one panel per field. An action method for a key calls `setVisible(false)` on the panels that key hides and `setVisible(true)` on the panels that key shows. A new form joins that same pattern. It does not replace the constructor, and it does not use a card layout.

This sample uses one panel for the whole form. Their code uses one panel per field. Add `CreateWeaponConfigPanel` as a single child of the panel F1 already shows. Do not split it into a panel per field.

---

## 1. Files to copy

If `page/form/` is already in the other project, leave it there.

| Copy this | Why |
|---|---|
| `page/CreateWeaponConfigPanel.java` | The whole Create Weapon Configuration form. One `JPanel`. |
| `page/model/WeaponConfig.java` | The values OK reads. No Swing types. This is what `weaponPresenter` should receive. |
| `page/form/` | Only when that folder is not in the other project yet. |

Stage 6 is the engagement parameters screen already on `EngagementView`. This sample does not keep a second copy. OK and Cancel on every form show that screen again. On OK from a weapon form, the weapon choice and config name are written onto it first.

Change `package page...;` to their package. Do not copy `FunctionPage`. Their view already has the footer.

---

## 2. Construct the form once

Build it where `EngagementView` or `WeaponConfigurationView` already builds the right-hand panels. Once, not inside the F1 method. Store it on a field so the key action and the OK listener can both see it.

```java
weaponForm = new CreateWeaponConfigPanel();
weaponForm.setVisible(false);
rightPanel.add(weaponForm);
```

Use the panel their F1 action already turns on, if the weapon pages appear in a different one than `rightPanel`. `add` puts it in the window. `setVisible(false)` keeps it hidden until that key runs. Their other field panels start the same way.

The create form must be constructed with the same objects that view already has: the engagement view, the right end panel, the bottom end panel, and the common item, when the surrounding view requires them. `CreateWeaponConfigPanel` itself takes no arguments. The view that holds it is the one with that constructor.

---

## 3. The F1 action after the swap

Find the method that runs for F1 after the keys have been swapped. It already hides some panels and shows others. Add the create form to that same method.

```java
leftPanel.setVisible(false);
engagementRightPanel.setVisible(false);
weaponForm.reset();
weaponForm.setVisible(true);
weaponForm.revalidate();
weaponForm.repaint();
```

Hide every panel that action already turns off. Show `weaponForm`. `reset()` clears it to a new create: no EHWT choice, empty name and speeds, Enb Dist `500`, Max RD `26.99`, attack sonar range `2000.00`, drop-downs empty.

Every other action that shows a different page needs `weaponForm.setVisible(false)`. If that line is missing, the form stays on screen after the next key.

The blue square can still say F1 after the swap. The method that runs is the one wired to that swapped key. Follow that method. Do not add a second listener that also handles F1.

---

## 4. OK shows their engagement page

```java
weaponForm.getOkButton().addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent event) {
        WeaponConfig config = weaponForm.readForm();
        weaponPresenter.save(config);
        weaponForm.setVisible(false);
        showEngagementPage();
    }
});
```

`save` stands for the method already on `weaponPresenter`. If the name differs, call that method. The argument is the `WeaponConfig` from `readForm()`, not `weaponForm`.

`showEngagementPage` stands for the action they already have that turns the engagement panels visible: `setVisible(true)` on the engagement right panel and the fields that belong to that page, `setVisible(false)` on the panels that page hides. Call that existing method. Do not write a new set of visibility lines if the method is already there.

Cancel calls that same show-engagement action. It does not call `readForm()`. OK and Cancel on every form return to Engagement Parameters.

| Field | Choices |
|---|---|
| Initial Speed | 24, 30, 35, 40, 45, 50 |
| ASH | ON, OFF |
| Acoustic Head Mode | Passive, Simultaneous, Wake |
| Attack Mode | Sub Surface, Surface |
| Wake Guidance | ON, OFF |
| Guidance Mode | Bg Rider Out, CCM, TCCM, Self Guided |
| Homing Mode | Pro Navigation, Intercept |

An untouched drop-down is stored as `""`.

---

## 5. If F1 does nothing

1. Swap the keys first. F1 before the swap is still the engagement action.
2. Put a print on the first line of the swapped F1 method. No print means the key never entered that method.
3. The form must be `add`ed in the constructor and `setVisible(true)` in that method. Building it without `add` compiles and shows nothing.
4. After `setVisible(true)`, call `revalidate()` and `repaint()` on the form or on the right panel. A panel added to a container that is already on screen stays blank until the container lays itself out again.
5. Check the other actions. One of them may set the right panel, or the new form, back to invisible at the end of the same click.

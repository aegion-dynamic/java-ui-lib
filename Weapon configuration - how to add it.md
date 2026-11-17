# Weapon configuration — how to add it

This form is not on the other system. Add it next to the screens that are already there. Leave `createWidget()` in place. That method still builds the engagement window. This page is one more form inside it.

Two screens come with it:

| Screen | Class | When it shows |
|---|---|---|
| Create Weapon Configuration | `CreateWeaponConfigPanel` | The first function key after the bar has been swapped |
| Stage 6, the saved screen | `SavedWeaponConfigPanel` | OK on Create Weapon Configuration |

Stage 6 is the engagement screen with the weapon values just entered. Engm, Tube, Engm Type, the CAL block, and the target block are part of stage 6. They are not on the create form.

---

## 1. Files to copy

Copy these into the other project. If `page/form/` is already there from Create Engagement, do not copy it again.

| Copy this | Why |
|---|---|
| `page/CreateWeaponConfigPanel.java` | The create form. F1 after the swap shows it. |
| `page/SavedWeaponConfigPanel.java` | Stage 6. OK shows it. |
| `page/model/WeaponConfig.java` | The values after they leave the form. No buttons inside it. |
| `page/form/` | Only if that folder is not in the other project yet. The panels do not compile without it. |

Change `package page...;` to the package of the screen you are joining. The three files and `page/form` must agree, or the `import` lines must be changed to match the folders.

Do not copy `FunctionPage` onto their window. Their window already exists. You only need the two panels and the one listener branch below.

---

## 2. Which key opens it

F4 swaps the words on the footer. The blue squares stay F1 through F12. After the swap, the first square still says F1, and the word beside it is WConfig.

The action command of that button changes. In this sample it becomes `"B1"`. The listener does not look at the blue square. It looks at the command:

```java
} else if ("B1".equals(key)) {
    onB1();
}
```

`onB1` shows the create form:

```java
private void onB1() {
    weaponForm.reset();
    formCards.show(formSlot, "wconfig");
}
```

On the other system, find the listener that already handles the function keys. Find the branch that runs for the first key after their swap. Put the show-the-form lines in that branch.

If their command is not `"B1"`, do not invent `"B1"`. Use the string their button already sends. Search their footer code for `setActionCommand` or `getActionCommand`. The word painted on the key and the command can differ. The command is the one the `if` must match.

`reset()` puts the form back to a blank create: no weapon selected, empty name and speeds, Enb Dist `500`, Max RD `26.99`, attack sonar range `2000.00`, drop-downs cleared.

---

## 3. Where to put the panels

Construct both panels once, when their window is built. Add them to the same place the other forms already sit. In this sample that place is a `CardLayout` in the lower-right slot:

```java
final CreateWeaponConfigPanel weaponForm = new CreateWeaponConfigPanel();
final SavedWeaponConfigPanel stage6 = new SavedWeaponConfigPanel();
formSlot.add(weaponForm, "wconfig");
formSlot.add(stage6, "stage6");
```

`weaponForm` and `stage6` must be fields, or `final` locals, if the listeners below use them. Java 6 only lets an anonymous listener read a local variable when that variable is `final`.

If their screen has no `CardLayout`, keep the container they already use. One `add` puts the create form where a form is allowed to appear. A second `add` puts stage 6 in that same container. Showing one and hiding the other is their layout’s job. `CardLayout.show` is the way this sample does it:

```java
formCards.show(formSlot, "wconfig");
```

The string `"wconfig"` must match the string used in `add`. `"stage6"` works the same way.

---

## 4. OK connects the form to stage 6

Hang this listener on the create form’s OK button. `readForm()` copies the fields into a `WeaponConfig`. `apply` writes that object onto stage 6. Then the slot shows stage 6.

```java
weaponForm.getOkButton().addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent event) {
        WeaponConfig config = weaponForm.readForm();
        stage6.apply(config);
        formCards.show(formSlot, "stage6");
    }
});
```

`apply` fills weapon, config name, the trajectory fields, the safety fields, and the attack fields. It does not clear Engm, the CAL block, or the target block. Those stay as stage 6 built them: Engm `1`, Tube `1`, Engm Type Prosp Eng, the CAL numbers, Type Surface, and the latitude.

If the other project already has a method that stores a weapon configuration, call it in this listener after `readForm()` and pass `config`. Do not pass `weaponForm`. The panel stays on the screen. The object goes to the save method.

```java
WeaponConfig config = weaponForm.readForm();
theirService.saveWeapon(config);
stage6.apply(config);
formCards.show(formSlot, "stage6");
```

Cancel does not read the form and does not show stage 6.

The drop-down lists on the create form are:

| Field | Choices |
|---|---|
| Initial Speed | 24, 30, 35, 40, 45, 50 |
| ASH | ON, OFF |
| Acoustic Head Mode | Simultaneous, Wake |
| Attack Mode | Sub Surface, Surface |
| Wake Guidance | ON, OFF |
| Guidance Mode | Bg Rider Out, CCM, TCCM, Self Guided |
| Homing Mode | Pro Navigation, Intercept |

An untouched drop-down is empty. `readForm()` stores `""` for it, and stage 6 shows that drop-down empty too.

---

## 5. Checks if the key does nothing

1. Press F4 first. Before the swap, F1 is Create Engagement, not this form.
2. After the swap, confirm the first button’s command. Print `event.getActionCommand()` in the listener. The `if` must use that exact string.
3. Confirm `onB1` runs. A print on its first line tells you the key arrived.
4. Confirm both panels were `add`ed to the slot that is on screen. A panel built and never added compiles, and OK then has nowhere visible to go.
5. Confirm the OK listener is on `getOkButton()` of the create form, and that `show` uses the same card name passed to `add` for stage 6.

# Listeners and data

Java 1.6. No lambdas. A listener is an anonymous class. A form shows data by calling setters on its controls. The controls never go into the presenter. The presenter stores plain objects.

Every OK and every Cancel returns to Engagement Parameters. That screen is also stage 6. There is no second copy of it.

| Key | Form | OK | Cancel |
|---|---|---|---|
| at open | `EngagementParametersPanel` | | |
| F1 | `CreateEngagementPanel`, empty | `readForm()`, `EngagementService.create`, show parameters | show parameters, do not read |
| F2 | `ModifyEngagementPanel`. Name drop-down loads one stored engagement | `readForm()`, `EngagementService.update`, show parameters | show parameters |
| F3 | `DeleteEngagementPanel` | read Eng No, show parameters | show parameters |
| F4 | swaps the footer words. The blue squares stay F1–F12 | | |
| F4 then F1, command `"B1"` | `CreateWeaponConfigPanel` | `readForm()`, `WeaponConfigService.create`, `applyWeapon`, show parameters | show parameters |
| F4 then F2, command `"B2"` | `ModifyWeaponConfigPanel`. Config name drop-down loads one stored configuration | `readForm()`, `WeaponConfigService.update`, `applyWeapon`, show parameters | show parameters |
| F4 then F3, command `"B3"` | `DeleteWeaponConfigPanel` | read the weapon and config name, show parameters | show parameters |

---

## 1. A listener in Java 1.6

`addActionListener` attaches the listener. `actionPerformed` runs on the click. Both names are from `java.awt.event`.

```java
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

form.getOkButton().addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent event) {
        Engagement engagement = form.readForm();
        engagementPresenter.save(engagement);
        form.setVisible(false);
        parametersPanel.setVisible(true);
    }
});
```

Attach this once, in the constructor, after `new CreateEngagementPanel()`. The F1 method only shows the form. OK is a different button.

Rules that keep this compiling on Java 6:

* Write `new ActionListener()` and `public void actionPerformed`. Do not write `->`.
* A local variable used inside `actionPerformed` is `final`. A field of the view is not.
* `JComboBox` has no type argument. Write `JComboBox`, not `JComboBox<String>`.
* `new ArrayList()` has no `<>`. A value taken out of a raw `List` needs a cast, `(Engagement) list.get(i)`.

```java
final CreateEngagementPanel form = new CreateEngagementPanel();
```

`final` is what lets the anonymous class call `form.readForm()`.

On their window, the key method only calls `setVisible`. The OK listener does the save. After `setVisible(true)` on a panel that is already on screen, call `revalidate()` and `repaint()` on that panel.

```java
private void onCreateKey() {
    form.resetForCreate();
    parametersPanel.setVisible(false);
    form.setVisible(true);
    form.revalidate();
    form.repaint();
}
```

---

## 2. `FunctionPage`

`src/main/java/page/FunctionPage.java`

The window. It owns the one key listener and every OK and Cancel listener. It does not store the form values itself. It calls `readForm()`, hands the object to a service, then `showParameters()`.

The key listener reads `event.getActionCommand()`. `"F1"` calls `create()`. `"F4"` swaps the bar. After the swap the first key still looks like F1, and its command is `"B1"`, which calls `onB1()`. `"B2"` calls `onB2()`.

`showParameters()` is `formCards.show(formSlot, "parameters")`. Every OK and Cancel ends there. In `EngagementView`, the same ending is `setVisible(true)` on the engagement panels and `setVisible(false)` on the form that was open.

---

## 3. `FunctionKeyBar`

`src/main/java/page/FunctionKeyBar.java`

Draws twelve black keys. A blue square on each says F1–F12. The words beside the squares are the operations. This class has no form data.

The screen passes one `ActionListener` into the constructor. A mouse press on a key, and the matching physical key, both call that listener with the key’s command. `setLabels(commands, texts)` changes the words and the commands. The blue squares stay F1–F12.

```java
footer = new FunctionKeyBar(64, 96, 40, functionListener);
footer.setLabels(FunctionKeyBar.KEYS, FIRST_TEXT);
```

The first array is the commands (`"F1"` or, after the swap, `"B1"`). The second array is the visible words (`"Create"`, `"Modify"`, `"WConfig"`). The listener must test the command, not the word painted on the key.

---

## 4. `EngagementParametersPanel`

`src/main/java/page/EngagementParametersPanel.java`

The screen at open, and the screen after every OK and Cancel. It is stage 6. It has no OK button and no listener of its own.

`applyWeapon(WeaponConfig)` writes the weapon choice, the config name, torpedo trajectory, and safety parameters onto this screen, then acoustic head mode, attack mode, wake guidance, and guidance mode. Trajectory is Initial Speed, Ordered Speed, Ordered Depth, and Search Speed. Safety is Enab dist from Enb Dist, UDL, LDL, Sonar Range, ASH, and MAX DIST from Max RD. Create Weapon Configuration OK and Modify Weapon Configuration OK call it, then show this panel.

The four attack drop-downs are read with `getAcousticHeadMode()`, `getAttackMode()`, `getWakeSensor()`, and `getGuidanceMode()`. Each returns the selected string, or `""` when nothing is selected.

---

## 5. `CreateEngagementPanel`

`src/main/java/page/CreateEngagementPanel.java`

F1. Header `CREATE ENGAGEMENT`. Engagement Name is a typed field with the hint “Enter Engagement Name”.

`resetForCreate()` clears it to a new engagement. `readForm()` returns a new `Engagement`. `apply(Engagement)` writes one back onto the controls. F1 calls `resetForCreate()` and does not call `apply`.

OK listener: `readForm()`, then `EngagementService.create`, then show Engagement Parameters. Cancel shows Engagement Parameters and does not call `readForm()`.

The small OK beside Torpedo Distance has no listener. It does not save.

| On the form | On `Engagement` |
|---|---|
| Eng No | `getField1` / `setField1` |
| Engagement Name | `getField2` / `setField2` |
| Engagement State Selection | `getGroup1` / `setGroup1` |
| Weapon Select | `getGroup2` / `setGroup2` |
| Linked Launch | `getGroup3` / `setGroup3` |
| Torpedo Distance | `getField3` / `setField3` |
| Tube Select | `getGroup4` / `setGroup4` |
| Weapon Configuration Selection | `getGroup5` / `setGroup5` |
| TB, RB, TP, Dp, Tpe, TCa, ATg | `getP1` … `getP7` |
| Type | `getField4` / `setField4` |
| Long, Lat | `getP8`, `getP9` |
| Track | `getField5` / `setField5` |
| Comments | `getField6` / `setField6` |

Putting one value on one control:

| Control | Call |
|---|---|
| Eng No, Engagement Name, Comments | `setValue`. `""` shows the grey hint again. Do not use `setText` on these three. `setText` would store the hint sentence as the value. |
| A radio group | `setSelected("Tube 1")`. The string must equal the button text. `""` clears the group. |
| Torpedo Distance and the number fields | `setText` on the `JTextField`. |
| A drop-down | `setSelectedItem("Surface")`. The item must already be in the combo. |

`getValue()` on a hint field returns `""` while the hint is showing.

---

## 6. `ModifyEngagementPanel`

`src/main/java/page/ModifyEngagementPanel.java`

F2. A separate panel. The header is `MODIFY ENGAGEMENT`. It is a `CreateEngagementPanel` whose Engagement Name is a drop-down of names already stored.

F2 calls `setNames(backend.names())`. The first name is selected, and `apply` loads that engagement. A later choice in the drop-down fires the name listener. That listener calls `findByName` and `apply`. `isFilling()` is true while `apply` or `setNames` is writing the combo, so the listener does not load the form twice.

```java
modifyForm.addNameListener(new ActionListener() {
    public void actionPerformed(ActionEvent event) {
        if (modifyForm.isFilling()) {
            return;
        }
        Engagement selected = backend.findByName(modifyForm.getSelectedName());
        if (selected != null) {
            modifyForm.apply(selected);
        }
    }
});
```

OK calls `readForm()` and `EngagementService.update`. Update replaces the stored engagement with the same name. Then Engagement Parameters is shown.

If `names()` is empty, F2 does not open the form. The blue label says to create an engagement first. The name that appears in the list is `getField2()`, the Engagement Name typed on Create.

---

## 7. `DeleteEngagementPanel`

`src/main/java/page/DeleteEngagementPanel.java`

F3. Header `DELETE ENGAGEMENT`. Two controls: Eng No, a combo that starts as `"1"`, `"2"`, `"3"`, and Eng Name, a text field.

`getEngNo()` is the selected number. `getEngName()` and `setEngName(String)` are the name field. `setEngNumbers(String[])` replaces the combo. The first number is selected. An empty array leaves the combo empty.

```java
String[] numbers = new String[engagements.size()];
for (int i = 0; i < engagements.size(); i++) {
    Engagement engagement = (Engagement) engagements.get(i);
    numbers[i] = engagement.getField1();
}
deleteForm.setEngNumbers(numbers);
Engagement engagement = engagementPresenter.find(deleteForm.getEngNo());
if (engagement == null) {
    deleteForm.setEngName("");
} else {
    deleteForm.setEngName(engagement.getField2());
}
```

OK in this sample reads the number, writes it on the blue label, and shows Engagement Parameters. On the other system the OK listener removes that engagement, then shows the parameters page:

```java
deleteForm.getOkButton().addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent event) {
        engagementPresenter.remove(deleteForm.getEngNo());
        deleteForm.setVisible(false);
        parametersPanel.setVisible(true);
    }
});
```

`remove` finds the `Engagement` whose `getField1()` equals that number and takes it out of the list. The next F3 builds the combo from what remains.

---

## 8. `CreateWeaponConfigPanel`

`src/main/java/page/CreateWeaponConfigPanel.java`

F1 after the footer swap. The command is `"B1"`. Header `CREATE WEAPON CONFIGURATION`. Config name is a typed field.

`reset()` clears it to a new configuration. Enb Dist stays `500`, Max RD stays `26.99`, attack sonar range stays `2000.00`. `readForm()` returns a new `WeaponConfig`. `apply(WeaponConfig)` writes one back.

OK stores it with `WeaponConfigService.create`, calls `parameters.applyWeapon(config)`, and shows Engagement Parameters. Cancel shows Engagement Parameters and does not read the form.

| On the form | On `WeaponConfig` |
|---|---|
| EHWT WAR / EHWT EXE | `getWeapon` / `setWeapon` |
| Config name | `getConfigName` / `setConfigName` |
| Initial Speed | `getInitialSpeed` / `setInitialSpeed` |
| Ordered Speed, Ordered Depth, Search Speed | `getOrderedSpeed`, `getOrderedDepth`, `getSearchSpeed` |
| Enb Dist, safety Sonar Range, UDL, LDL, ASH, Max RD | `getEnbDist`, `getSafetySonarRange`, `getUdl`, `getLdl`, `getAsh`, `getMaxRd` |
| Acoustic Head Mode, attack Sonar Range, Attack Mode | `getAcousticHeadMode`, `getAttackSonarRange`, `getAttackMode` |
| Wake Guidance, Guidance Mode, Homing Mode | `getWakeGuidance`, `getGuidanceMode`, `getHomingMode` |
| WC Comments | `getComments` / `setComments` |

Drop-down lists: Initial Speed is 24, 30, 35, 40, 45, 50. ASH and Wake Guidance are ON, OFF. Acoustic Head Mode is Simultaneous, Wake. Attack Mode is Sub Surface, Surface. Guidance Mode is Bg Rider Out, CCM, TCCM, Self Guided. Homing Mode is Pro Navigation, Intercept. An untouched drop-down is stored as `""`.

---

## 9. `ModifyWeaponConfigPanel`

`src/main/java/page/ModifyWeaponConfigPanel.java`

F2 after the footer swap. The command is `"B2"`. A separate panel. The header is `MODIFY WEAPON CONFIGURATION`. Config name is a drop-down of names already stored. Choosing one calls `apply` for that `WeaponConfig`.

The listener is the same shape as Modify Engagement, with `WeaponConfigService.findByName` and `modifyWeaponForm.apply`. OK calls `update`, then `parameters.applyWeapon`, then shows Engagement Parameters.

The name in the list is `getConfigName()`, the Config name typed on Create. If none has been stored, this key does not open the form.

---

## 10. `DeleteWeaponConfigPanel`

`src/main/java/page/DeleteWeaponConfigPanel.java`

F3 after the footer swap. The command is `"B3"`. The word beside the blue square is Delete. Header `DELETE WEAPON CONFIG`. Two controls: EHWT WAR / EHWT EXE, with neither selected at first, and Config name, a text field.

`getWeapon()` is the selected radio, or `""` when neither is selected. `setWeapon(String)` selects the radio whose text matches, or clears both. `getConfigName()` and `setConfigName(String)` are the name field. `reset()` clears both.

OK in this sample reads the name, writes it on the blue label, and shows Engagement Parameters. It does not remove the configuration. On the other system the OK listener removes that configuration, then shows the parameters page:

```java
deleteWeaponForm.getOkButton().addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent event) {
        weaponPresenter.remove(deleteWeaponForm.getWeapon(), deleteWeaponForm.getConfigName());
        deleteWeaponForm.setVisible(false);
        parametersPanel.setVisible(true);
    }
});
```

`remove` finds the `WeaponConfig` whose `getConfigName()` equals that name and takes it out of the list. Cancel shows Engagement Parameters and does not read the form.

---

## 11. `MotionReadout`

`src/main/java/page/MotionReadout.java`

The strip above the form. COG is a title. SOG shows knots. DSM shows metres. No listener. No button.

`setSog(String)` and `setDsm(String)` replace the dashes. `""` puts the dashes back. `getSog()` and `getDsm()` return `""` while the dashes are showing. Call these from the same place that already receives speed and distance. Do not read the labels from another thread.

---

## 12. `page.model.Engagement`

`src/main/java/page/model/Engagement.java`

Plain strings. No Swing import. This is the object `readForm()` builds and the object `apply` accepts. The presenter stores a list of these. The panel stays on the screen.

A Java 6 list:

```java
private final List engagements = new ArrayList();

public void save(Engagement engagement) {
    engagements.add(engagement);
}

public Engagement findByName(String name) {
    for (int i = 0; i < engagements.size(); i++) {
        Engagement engagement = (Engagement) engagements.get(i);
        if (name.equals(engagement.getField2())) {
            return engagement;
        }
    }
    return null;
}
```

Create OK adds one. Modify OK replaces the one with the same Engagement Name (`getField2()`). Delete OK, on their presenter, removes the one whose Eng No (`getField1()`) matches.

---

## 13. `page.model.WeaponConfig`

`src/main/java/page/model/WeaponConfig.java`

Plain strings for one weapon configuration. No Swing import. `CreateWeaponConfigPanel.readForm()` builds it. `apply` and `EngagementParametersPanel.applyWeapon` accept it.

The list of these lives in `WeaponConfigService`, not in the panel. Create adds. Modify replaces the one with the same config name.

---

## 14. `EngagementService`

`src/main/java/page/backend/EngagementService.java`

The sample presenter for engagements. `create` appends. `update` replaces the engagement with the same name, or appends when that name is new. `findByName` returns that engagement or `null`. `names()` is the `String[]` passed to `ModifyEngagementPanel.setNames`. `getLastCreated()` is the last one passed to `create` or `update`.

On the other system this class is `engagementPresenter`. Keep their method names. The argument is still an `Engagement`, not the panel.

---

## 15. `WeaponConfigService`

`src/main/java/page/backend/WeaponConfigService.java`

The sample presenter for weapon configurations. `create` appends. `update` replaces the configuration with the same config name. `findByName` returns it or `null`. `names()` feeds `ModifyWeaponConfigPanel.setNames`.

On the other system this is `weaponPresenter`. Pass the `WeaponConfig` from `readForm()`. Do not pass `CreateWeaponConfigPanel`.

---

## 16. The form pieces

These classes do not store an engagement. They draw one row or one group. The panels above own the data.

### `page.form.Block`

`src/main/java/page/form/Block.java`

A `JPanel` whose tallest size is its preferred size, so a vertical stack does not stretch one row to fill the window. `FormRow`, `OptionGroup`, `Heading`, `GroupLabel`, `FormCard`, and `SectionPanel` extend it. No listener. No data.

### `page.form.Mono`

`src/main/java/page/form/Mono.java`

Colours, fonts, and factories. `field(columns)` makes a white text field. `combo(String[])` makes a raw `JComboBox`. `area(rows, columns)` makes a white text area. `button` and `accentButton` make the dark and cyan buttons. `radio` and `check` make the option controls. No listener is attached here. The screen attaches listeners to the buttons these methods return.

### `page.form.FormStack`

`src/main/java/page/form/FormStack.java`

A vertical `BoxLayout` that also implements `Scrollable`, so the form scrolls up and down and stays as wide as the scroll pane. `add` puts the next row at the bottom. No listener. No data.

### `page.form.FormRow`

`src/main/java/page/form/FormRow.java`

One label on the left and one control on the right. `new FormRow("Eng No", field)`. The label width is fixed. The control can be a field, a combo, or a small panel. No listener. The value lives in the control you passed in.

### `page.form.FormCard`

`src/main/java/page/form/FormCard.java`

The bordered box around one group of rows. `addRow` adds the next child. Create Engagement uses one card for ENGAGEMENT CONFIGURATION. No listener. No data.

### `page.form.Heading`

`src/main/java/page/form/Heading.java`

A cyan section title, left aligned. `new Heading("TARGET PARAMETERS")`. No listener. The string is the title, not a stored value.

### `page.form.GroupLabel`

`src/main/java/page/form/GroupLabel.java`

A centred label with a line on each side, used inside a card. `new GroupLabel("Tube Select")`. No listener. No data.

### `page.form.OptionGroup`

`src/main/java/page/form/OptionGroup.java`

Radio buttons for one choice. `new OptionGroup(new String[] {"Yes", "No"}, 1, 2)` selects index 1, which is No. The last number is how many sit on one line. Pass `-1` for none selected.

`getSelected()` returns the selected label, or `""`. `setSelected("Tube 1")` selects that label. `setSelected("")` clears the group. No `ActionListener` is required to read it. `readForm()` calls `getSelected()` when OK is pressed.

### `page.form.CheckGroup`

`src/main/java/page/form/CheckGroup.java`

Check boxes that can be ticked together. `getSelected()` joins the ticked labels with `", "`. None ticked is `""`. Create Engagement does not use this class. Weapon Select is an `OptionGroup`. No listener is attached by the class.

### `page.form.HintField`

`src/main/java/page/form/HintField.java`

A one-line field with grey hint text. Java 6 has no placeholder, so the hint is the field’s text until focus arrives. A focus listener clears it on entry and restores it when the field is left empty.

`getValue()` returns `""` while the hint is showing, and the typed text otherwise. `setValue("1")` shows that text as a real value. `setValue("")` shows the hint again. Eng No and Engagement Name on Create use this. `readForm()` calls `getValue()`, not `getText()`.

### `page.form.HintArea`

`src/main/java/page/form/HintArea.java`

The same hint rule on several lines. Comments use `new HintArea("Enter comments...", 4, 20)`. `getValue()` and `setValue(String)` are the data methods. The focus listener is the only listener.

### `page.form.SectionPanel`

`src/main/java/page/form/SectionPanel.java`

A block with a triangle. A mouse listener on the title opens and closes the body. `addContent` adds a row inside. `setOpen(false)` starts it closed. Create Engagement does not use it. It holds whatever components you add. It does not copy them into an `Engagement`.

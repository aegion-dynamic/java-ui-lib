# Use their weapon types

Java 1.6. This note is for the other project.

The plan is sound. These forms already say `WeaponConfigState`, `Speed`, `Depth`, `Distance`, `AcousticHeadMode`, `GuidanceMode`, `InternalGuidanceMode`, and `TargetType`. On the other machine you do not copy those classes. You delete the `page.model` import and import their class of the same name. The rest of the line can stay, because the name on the line did not change.

Create, Modify, and List are the three forms. Modify and List are not separate copies of the fields. Both extend `CreateWeaponConfigPanel`, so they use its drop-downs, `readForm()`, and `apply()`. Change the parent once and all three follow.

| Form | Class | What you edit |
|---|---|---|
| Create Weapon Configuration | `CreateWeaponConfigPanel` | The imports, and any call their class does not have |
| Modify Weapon Configuration | `ModifyWeaponConfigPanel` | Nothing inside this file. It only sets the title and the name drop-down. |
| List of Weapon Configurations | `ListWeaponConfigPanel` | Nothing inside this file. It only sets the title and locks the fields. |

Engagement forms are the same kind of job later. This note stops at the weapon forms.

---

## 1. Files you do not copy

Leave these out of their tree. Their project already has the real ones.

| Our file | Their name, if it matches |
|---|---|
| `page/model/WeaponConfigState.java` | `WeaponConfigState` |
| `page/model/AcousticHeadMode.java` | `AcousticHeadMode` |
| `page/model/GuidanceMode.java` | `GuidanceMode` |
| `page/model/InternalGuidanceMode.java` | `InternalGuidanceMode` |
| `page/model/TargetType.java` | `TargetType` |
| `page/model/Speed.java` | `Speed` |
| `page/model/Depth.java` | `Depth` |
| `page/model/Distance.java` | `Distance` |
| `page/model/Numbers.java` | only used by our Speed, Depth, and Distance |

`page/model/WeaponConfig.java` is the bag this sample uses to hold one configuration. Their presenter already has that object. Do not add a second one. `readForm()` should fill their object. `apply()` should read their object.

---

## 2. The import lines

In `CreateWeaponConfigPanel`, replace the `page.model` imports with their package. The class name at the end stays.

```java
import their.package.AcousticHeadMode;
import their.package.Depth;
import their.package.Distance;
import their.package.GuidanceMode;
import their.package.InternalGuidanceMode;
import their.package.Speed;
import their.package.TargetType;
import their.package.WeaponConfigState;
```

`their.package` means the package you find when you open their class. Each class can live in a different package. Use the package on that file.

After the import change, the compiler looks up their `WeaponConfigState` wherever the file says `WeaponConfigState`. You do not rename those words in the form.

Modify and List have no `page.model` import. Leave them.

---

## 3. Which field uses which type

The words on the screen stay as they are. The value behind the control is their type.

| On all three forms | Stored as | Becomes |
|---|---|---|
| Config name | `char[]` | text only when a drop-down needs it |
| EHWT WAR / EHWT EXE | `WeaponConfigState` | |
| Initial Speed, Ordered Speed, Search Speed | `double` knots | `Speed` |
| Ordered Depth | `double` metres | `Depth` |
| Enb Dist, UDL, LDL, Max RD, Sonar Range | `float` metres | `Distance` |
| ASH, Wake Guidance | `OnOff` | OFF or ON |
| Acoustic Head Mode | `AcousticHeadMode` | Passive, Simultaneous, Wake |
| Guidance Mode | `GuidanceMode` | Bg Rider Out, CCM, TCCM, Self Guided |
| Homing Mode | `InternalGuidanceMode` | Pro Navigation, Intercept |
| Attack Mode | `TargetType` | |

The `double` and `float` stay on `WeaponConfig`. `toInitialSpeed()`, `toOrderedSpeed()`, `toSearchSpeed()`, `toOrderedDepth()`, `toEnbDist()`, `toUdl()`, `toLdl()`, `toMaxRd()`, and `toSonarRange()` build the object with the unit. An empty box is `null`, and the matching `to` method returns `null`.

WC Comments stay text. There is one Sonar Range, under Attack Parameters.

---

## 4. What to check after the import

A matching name does not always mean a matching method. Compile `CreateWeaponConfigPanel`. Each error is one call their class does not have. Fix that call. You do not start the form over.

These are the calls this sample makes. Their class might spell them differently.

| Our call | Where | What to do if it fails |
|---|---|---|
| `new Speed(24)` and the other five speeds | the Initial Speed list | Build a `Speed` the way their code already does, including knots if their constructor asks for a unit |
| `Speed.parse(text)` then `getKnots()` | Ordered Speed and Search Speed | The struct stores the `double`. `toOrderedSpeed()` and `toSearchSpeed()` build the `Speed` |
| `Depth.parse(text)` then `getMeters()` | Ordered Depth | The struct stores the `double`. `toOrderedDepth()` builds the `Depth` |
| `Distance.parse(text)` then `getMeters()` | Enb Dist, UDL, LDL, Max RD, Sonar Range | The struct stores the `float`. The `to` methods build the `Distance` |
| `OnOff.values()` | ASH and Wake Guidance | OFF and ON |
| `AcousticHeadMode.values()` | Acoustic Head Mode | Passive, Simultaneous, Wake |
| `GuidanceMode.values()` | Guidance Mode | Bg Rider Out, CCM, TCCM, Self Guided |
| `InternalGuidanceMode.values()` | Homing Mode | Pro Navigation, Intercept |
| `TargetType.values()` | Attack Mode | Same |
| `configNameText()` | the name drop-down | The struct field is a `char[]`. This method is `new String` of that array |
| radio text `"EHWT WAR"` / `"EHWT EXE"` | `setWeapon` | Their enum has no `fromLabel`. Compare the radio text and pass `EHWT_WAR` or `EHWT_EXE`. |
| `getConfigName().toCharArray()` | `setConfigName` | Their setter takes `char[]`. The form still reads a `String`. |

The drop-downs show `toString()` of the value. Our enums return `EHWT WAR`, `ON`, `Sub Surface`, and so on. If their `toString()` returns `EHWT_WAR` instead, the box shows the constant name. The value is still correct. Change the drawing of the box only if the words on screen must stay the spaced labels.

`applyWeapon` must not call `setSelectedItem` with the enum. Their acoustic, attack, wake, and guidance boxes already hold the words (`Passive`, `Sub Surface`, `ON`, `Bg Rider Out`). `setSelectedItem` selects a row only when that same object is in the list. An enum is not equal to the string `"Passive"`, so the box stays blank. Walk the items and select the one whose text matches, turning `PASSIVE` into `Passive` and `BG_RIDER_OUT` into `Bg Rider Out`. `EngagementParametersPanel.choose` does that.

---

## 5. Their Speed has no parse

`Speed.parse` exists only in this sample. Their `Speed` does not have it. Do not add `parse` to their class. Read the number from the text box yourself, and call their constructor only when you set their field.

`EngagementParametersPanel.getInitialSpeed()` is the sample version:

```java
public Double getInitialSpeed() {
    Speed speed = Speed.parse(initialSpeed.getText());
    return speed == null ? null : new Double(speed.getKnots());
}
```

`initialSpeed.getText()` is the text in the box, such as `"24"` or `""`. `Speed.parse` trims that text. A blank box, or text that is not a number, comes back as `null`. Anything else becomes `new Speed(the number)`. The next line throws that `Speed` away and keeps the knots as a `Double`. The struct stores the number. It does not store a `Speed`.

On their machine the getter stops at the `double`:

```java
public Double getInitialSpeed() {
    String text = initialSpeed.getText();
    if (text == null || text.trim().length() == 0) {
        return null;
    }
    try {
        return new Double(Double.parseDouble(text.trim()));
    } catch (NumberFormatException ex) {
        return null;
    }
}
```

Build their `Speed` at the moment you set their field. Ordered Speed and Search Speed use the same two steps. The unit is knots, which their constructor already knows.

```java
Double knots = getInitialSpeed();
if (knots != null) {
    theirConfig.setInitialSpeed(new Speed(knots.doubleValue()));
}
```

Ordered Depth is the same shape with `Depth`. The number is metres.

```java
public Double getOrderedDepth() {
    String text = orderedDepth.getText();
    if (text == null || text.trim().length() == 0) {
        return null;
    }
    try {
        return new Double(Double.parseDouble(text.trim()));
    } catch (NumberFormatException ex) {
        return null;
    }
}
```

```java
Double meters = getOrderedDepth();
if (meters != null) {
    theirConfig.setOrderedDepth(new Depth(meters.doubleValue()));
}
```

Enb Dist, UDL, LDL, Max RD, and Sonar Range are a `float`, then their `Distance`. Use `Float.parseFloat`. A blank box is `null`, and you skip the constructor.

```java
public Float getEnbDist() {
    String text = enbDist.getText();
    if (text == null || text.trim().length() == 0) {
        return null;
    }
    try {
        return new Float(Float.parseFloat(text.trim()));
    } catch (NumberFormatException ex) {
        return null;
    }
}
```

```java
Float meters = getEnbDist();
if (meters != null) {
    theirConfig.setEnbDist(new Distance(meters.floatValue()));
}
```

`CreateWeaponConfigPanel.readForm()` does this same split. It parses the text to a `Double` or a `Float` and stores that. `toInitialSpeed()`, `toOrderedDepth()`, and `toEnbDist()` are the sample's way of calling `new Speed`, `new Depth`, and `new Distance`. If their constructor needs a unit argument as well as the number, pass the unit their code already uses. Knots for speed. Metres for depth and distance.

The number boxes already refuse letters. `parseDouble` can still fail on a half-typed value such as `"12."`. The `catch` keeps that as `null` instead of stopping the form.

---

## 6. OK and Modify

`readForm()` builds one object from the controls. Point it at their configuration class and call their setters.

```java
TheirWeaponConfig config = new TheirWeaponConfig();
String radio = weapon.getSelected();
if ("EHWT WAR".equals(radio)) {
    config.setWeapon(WeaponConfigState.EHWT_WAR);
} else if ("EHWT EXE".equals(radio)) {
    config.setWeapon(WeaponConfigState.EHWT_EXE);
}
config.setConfigName(getConfigName().toCharArray());
if (chosen(initialSpeed) instanceof Speed) {
    config.setInitialSpeed((Speed) chosen(initialSpeed));
}
String searchText = searchSpeed.getText().trim();
if (searchText.length() > 0) {
    config.setSearchSpeed(new Speed(Double.parseDouble(searchText)));
}
String depthText = orderedDepth.getText().trim();
if (depthText.length() > 0) {
    config.setOrderedDepth(new Depth(Double.parseDouble(depthText)));
}
String enbText = enbDist.getText().trim();
if (enbText.length() > 0) {
    config.setEnbDist(new Distance(Float.parseFloat(enbText)));
}
config.setAsh((AcousticHeadMode) chosen(ash));
config.setAttackMode((TargetType) chosen(attackMode));
config.setWakeGuidance((GuidanceMode) chosen(wakeGuidance));
config.setGuidanceMode((InternalGuidanceMode) chosen(guidanceMode));
```

`setWeapon` and the other `set` names are the names from this sample. Use the setter their class actually has. The type in parentheses is the import from section 2. Wrap `parseDouble` and `parseFloat` the way section 5 does, so a blank or half-typed box stays unset.

The OK listener stays three steps. Read, hand the object to `weaponPresenter`, hide the form.

```java
TheirWeaponConfig config = weaponForm.readForm();
weaponPresenter.save(config);
weaponForm.setVisible(false);
showEngagementPage();
```

Modify and List load a saved one through `apply()`. Their presenter returns their object. `apply()` puts each getter back on the matching control. Create, Modify, and List share that method, so you edit it once.

```java
TheirWeaponConfig found = weaponPresenter.findByName(name);
if (found != null) {
    modifyWeaponForm.apply(found);
}
```

List uses the same `apply()`. The fields are locked, so the user can look and cannot type.

---

## 7. Order of work

1. Copy `CreateWeaponConfigPanel`, `ModifyWeaponConfigPanel`, and `ListWeaponConfigPanel`. Do not copy the model files in section 1.
2. Change the imports in section 2.
3. Compile. Fix only the lines the compiler names. Section 4 is the list of likely lines.
4. Point `readForm()` and `apply()` at their configuration class.
5. OK calls their presenter with that object. Modify and List call `apply()` with the object the presenter gives back.

Engagement Create, Modify, and List are the next pass. Same steps, once you have their engagement enums.

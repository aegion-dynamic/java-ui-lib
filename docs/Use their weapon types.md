# Use their weapon types

Java 1.6. This note is for the other project.

The plan is sound. These forms already say `WeaponType`, `Speed`, `Depth`, `Distance`, `AcousticHeadMode`, `GuidanceMode`, `InternalGuidanceMode`, and `TargetType`. On the other machine you do not copy those classes. You delete the `page.model` import and import their class of the same name. The rest of the line can stay, because the name on the line did not change.

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
| `page/model/WeaponType.java` | `WeaponType` |
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
import their.package.WeaponType;
```

`their.package` means the package you find when you open their class. Each class can live in a different package. Use the package on that file.

After the import change, the compiler looks up their `WeaponType` wherever the file says `WeaponType`. You do not rename those words in the form.

Modify and List have no `page.model` import. Leave them.

---

## 3. Which field uses which type

The words on the screen stay as they are. The value behind the control is their type.

| On all three forms | Their type |
|---|---|
| EHWT WAR / EHWT EXE | `WeaponType` |
| Initial Speed, Search Speed | `Speed`, knots |
| Ordered Depth | `Depth`, metres |
| Enb Dist, UDL, LDL, Max RD | `Distance`, metres |
| ASH | `AcousticHeadMode` |
| Wake Guidance | `GuidanceMode` |
| Guidance Mode | `InternalGuidanceMode` |
| Attack Mode | `TargetType` |

Ordered Speed, Config name, Acoustic Head Mode, Sonar Range, Homing Mode, and WC Comments are still text. There is one Sonar Range, under Attack Parameters.

---

## 4. What to check after the import

A matching name does not always mean a matching method. Compile `CreateWeaponConfigPanel`. Each error is one call their class does not have. Fix that call. You do not start the form over.

These are the calls this sample makes. Their class might spell them differently.

| Our call | Where | What to do if it fails |
|---|---|---|
| `new Speed(24)` and the other five speeds | the Initial Speed list | Build a `Speed` the way their code already does, including knots if their constructor asks for a unit |
| `Speed.parse(text)` | Search Speed | Call their parse, or write `new Speed(...)` from the text |
| `Depth.parse(text)` | Ordered Depth | Same idea |
| `Distance.parse(text)` | Enb Dist, UDL, LDL, Max RD | Same idea |
| `AcousticHeadMode.values()` | ASH list | Use their list of constants if they have no `values()` method you can call. An enum does have `values()`. |
| `GuidanceMode.values()` | Wake Guidance | Same |
| `InternalGuidanceMode.values()` | Guidance Mode | Same |
| `TargetType.values()` | Attack Mode | Same |
| `WeaponType.fromLabel(...)` | the EHWT radios | Their enum may not have `fromLabel`. Pick the constant that matches the radio text, `EHWT_WAR` or `EHWT_EXE`. |

The drop-downs show `toString()` of the value. Our enums return `EHWT WAR`, `ON`, `Sub Surface`, and so on. If their `toString()` returns `EHWT_WAR` instead, the box shows the constant name. The value is still correct. Change the drawing of the box only if the words on screen must stay the spaced labels.

---

## 5. OK and Modify

`readForm()` builds one object from the controls. Point it at their configuration class and call their setters.

```java
TheirWeaponConfig config = new TheirWeaponConfig();
config.setWeapon(WeaponType.fromLabel(weapon.getSelected()));
config.setInitialSpeed((Speed) chosen(initialSpeed));
config.setSearchSpeed(Speed.parse(searchSpeed.getText()));
config.setOrderedDepth(Depth.parse(orderedDepth.getText()));
config.setEnbDist(Distance.parse(enbDist.getText()));
config.setAsh((AcousticHeadMode) chosen(ash));
config.setAttackMode((TargetType) chosen(attackMode));
config.setWakeGuidance((GuidanceMode) chosen(wakeGuidance));
config.setGuidanceMode((InternalGuidanceMode) chosen(guidanceMode));
```

`setWeapon` and the other `set` names are the names from this sample. Use the setter their class actually has. The type in parentheses is the import from section 2.

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

## 6. Order of work

1. Copy `CreateWeaponConfigPanel`, `ModifyWeaponConfigPanel`, and `ListWeaponConfigPanel`. Do not copy the model files in section 1.
2. Change the imports in section 2.
3. Compile. Fix only the lines the compiler names. Section 4 is the list of likely lines.
4. Point `readForm()` and `apply()` at their configuration class.
5. OK calls their presenter with that object. Modify and List call `apply()` with the object the presenter gives back.

Engagement Create, Modify, and List are the next pass. Same steps, once you have their engagement enums.

# Replace these in their engagement and weapon config

Copy the methods below into their forms. Do not replace their model classes, their enums, or their view. Their `WeaponConfig.setConfigName` already takes `char[]`. Their `WeaponConfigState` has no `fromLabel`. Their drop-downs hold the words, not the enum object.

Leave every other file in this sample where it is.

---

## 1. Weapon create and modify — two lines in `readForm`

File in this sample: `src/main/java/page/CreateWeaponConfigPanel.java`

In their create-weapon OK, replace these two calls:

```java
config.setWeapon(WeaponConfigState.fromLabel(weapon.getSelected()));
config.setConfigName(getConfigName());
```

with:

```java
config.setWeapon(weaponState(weapon.getSelected()));
config.setConfigName(getConfigName().toCharArray());
```

Add this method next to `readForm`. `getConfigName()` stays a `String`. `toCharArray()` is only for their setter.

```java
private static WeaponConfigState weaponState(String label) {
    if ("EHWT WAR".equals(label)) {
        return WeaponConfigState.EHWT_WAR;
    }
    if ("EHWT EXE".equals(label)) {
        return WeaponConfigState.EHWT_EXE;
    }
    return null;
}
```

`Modify` uses the same `readForm`. One change covers both. Do not add `fromLabel` to their enum.

---

## 2. Weapon create and modify — putting a saved config back on the form

Same file. Replace `showChoice` so a saved enum selects the word already in the box. Add `words` and `constantName` with it.

```java
private static void showChoice(JComboBox box, Object value) {
    if (value == null || "".equals(value)) {
        box.setSelectedIndex(-1);
        return;
    }
    for (int i = 0; i < box.getItemCount(); i++) {
        Object item = box.getItemAt(i);
        if (item != null && (value.equals(item)
                || words(value.toString()).equalsIgnoreCase(words(item.toString())))) {
            box.setSelectedIndex(i);
            return;
        }
    }
    box.setSelectedIndex(-1);
}

private static String words(String text) {
    if (text.indexOf('_') < 0 && !constantName(text)) {
        return text;
    }
    String[] parts = text.split("_");
    StringBuffer joined = new StringBuffer();
    for (int i = 0; i < parts.length; i++) {
        if (i > 0) {
            joined.append(' ');
        }
        if (parts[i].length() == 0) {
            continue;
        }
        String lower = parts[i].toLowerCase();
        joined.append(Character.toUpperCase(lower.charAt(0)));
        joined.append(lower.substring(1));
    }
    return joined.toString();
}

private static boolean constantName(String text) {
    if (text.length() == 0) {
        return false;
    }
    for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        if (c >= 'a' && c <= 'z') {
            return false;
        }
    }
    return true;
}
```

The `apply` calls stay `showChoice(box, config.get...)`. Only the method body changes.

---

## 3. Engagement parameters — reading the EHWT radios

File in this sample: `src/main/java/page/EngagementParametersPanel.java`

Replace `getWeapon` so it does not call `fromLabel`.

```java
public WeaponConfigState getWeapon() {
    String label = weapon.getSelected();
    if ("EHWT WAR".equals(label)) {
        return WeaponConfigState.EHWT_WAR;
    }
    if ("EHWT EXE".equals(label)) {
        return WeaponConfigState.EHWT_EXE;
    }
    return null;
}
```

---

## 4. Engagement parameters — writing their getters onto the form

Same file. Replace the whole `applyWeapon` method. Their struct has getters and setters only. Do not call `configNameText`, `toInitialSpeed`, `toOrderedSpeed`, `toOrderedDepth`, `toSearchSpeed`, `toEnbDist`, `toUdl`, `toLdl`, `toSonarRange`, or `toMaxRd`. Those methods are not on their model.

The form turns the getter into text. A `char[]` name becomes `new String`. A `Double` or `Float` becomes the number in the box. An enum is not passed to `setSelectedItem`. `choose` finds the row whose words match the enum name: `PASSIVE` matches `Passive`, `SUB_SURFACE` matches `Sub Surface`, `BG_RIDER_OUT` matches `Bg Rider Out`, `ON` matches `ON`, `EHWT_WAR` matches the EHWT WAR radio.

```java
public void applyWeapon(WeaponConfig config) {
    weapon.setSelected(choiceText(config.getWeapon()));
    configName.setText(nameText(config.getConfigName()));
    text(initialSpeed, numberText(config.getInitialSpeed()));
    text(orderedSpeed, numberText(config.getOrderedSpeed()));
    text(orderedDepth, numberText(config.getOrderedDepth()));
    text(searchSpeed, numberText(config.getSearchSpeed()));
    text(enbDist, numberText(config.getEnbDist()));
    text(udl, numberText(config.getUdl()));
    text(ldl, numberText(config.getLdl()));
    text(safetySonarRange, numberText(config.getAttackSonarRange()));
    choose(ash, config.getAsh());
    text(maxRd, numberText(config.getMaxRd()));
    choose(acousticHeadMode, config.getAcousticHeadMode());
    choose(attackMode, config.getAttackMode());
    choose(wakeSensor, config.getWakeGuidance());
    choose(guidanceMode, config.getGuidanceMode());
}
```

`getAttackSonarRange()` is their sonar getter. If their method has another name, change that one call. The rest stay getters.

Initial Speed and Ordered Speed use the same decimal path as the distances. The form does not call `Speed.parse` or `getKnots`.

Reading the box gives a `Double`. Their setter receives `new Speed(thatNumber)`:

```java
Double initial = getInitialSpeed();
if (initial != null) {
    config.setInitialSpeed(new Speed(initial.doubleValue()));
}
Double ordered = getOrderedSpeed();
if (ordered != null) {
    config.setOrderedSpeed(new Speed(ordered.doubleValue()));
}
```

Writing the box back takes the number out of whatever `getInitialSpeed()` returns. A `Double` is written as it is. A `Speed` is written from the number in its text, such as `"24"` or `"24 kn"`.

```java
text(initialSpeed, numberText(knotsOf(config.getInitialSpeed())));
text(orderedSpeed, numberText(knotsOf(config.getOrderedSpeed())));
```

`getInitialSpeed()` and `getOrderedSpeed()` on the form are:

```java
public Double getInitialSpeed() {
    return decimal(initialSpeed.getText());
}

public Double getOrderedSpeed() {
    return decimal(orderedSpeed.getText());
}
```

`decimal` and `knotsOf` are the methods below `metres` in `EngagementParametersPanel`. Copy those two with `applyWeapon`.

Add `nameText`, `numberText`, `choiceText`, and replace `choose` with the methods below. `setSelectedItem(enum)` leaves their boxes blank.

```java
private static String nameText(char[] name) {
    return name == null ? "" : new String(name);
}

private static String numberText(Object number) {
    if (!(number instanceof Number)) {
        return "";
    }
    java.text.DecimalFormat format = new java.text.DecimalFormat("0.##");
    format.setDecimalFormatSymbols(new java.text.DecimalFormatSymbols(java.util.Locale.US));
    return format.format(((Number) number).doubleValue());
}

private static String choiceText(Object value) {
    if (value == null) {
        return "";
    }
    if (value instanceof Enum) {
        return ((Enum) value).name();
    }
    return String.valueOf(value);
}

private static void choose(JComboBox box, Object value) {
    if (value == null || "".equals(value)) {
        box.setSelectedIndex(-1);
        return;
    }
    for (int i = 0; i < box.getItemCount(); i++) {
        Object item = box.getItemAt(i);
        if (item != null && sameChoice(value, item)) {
            box.setSelectedIndex(i);
            return;
        }
    }
    box.setSelectedIndex(-1);
}

private static boolean sameChoice(Object value, Object item) {
    if (value.equals(item)) {
        return true;
    }
    String[] left = keys(value);
    String[] right = keys(item);
    for (int a = 0; a < left.length; a++) {
        for (int b = 0; b < right.length; b++) {
            if (left[a].length() > 0 && left[a].equals(right[b])) {
                return true;
            }
        }
    }
    return false;
}

private static String[] keys(Object value) {
    String shown = letters(String.valueOf(value));
    if (value instanceof Enum) {
        String constant = letters(((Enum) value).name());
        if (!constant.equals(shown)) {
            return new String[] {constant, shown};
        }
        return new String[] {constant};
    }
    return new String[] {shown};
}

private static String letters(String text) {
    StringBuffer out = new StringBuffer();
    for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        if (c == ' ' || c == '_' || c == '-') {
            continue;
        }
        out.append(Character.toLowerCase(c));
    }
    return out.toString();
}
```

ASH uses the same `choose`. It starts working with this replacement too.

---

## 5. Do not replace these

| Their file | Leave it |
|---|---|
| Their `WeaponConfig` | `setConfigName(char[])` is already theirs. Do not paste this sample's class over it. |
| Their enums | No `fromLabel`. The form maps the radio text itself. |
| ECS view, footer, top panel | Not part of this change. |
| Wire guidance view | Not part of this change. |

`getConfigName()` on the form still returns a `String` for the name list and for delete. Only the line that calls their `setConfigName` passes `toCharArray()`.

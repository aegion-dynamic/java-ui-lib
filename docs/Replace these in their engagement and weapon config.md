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

## 4. Engagement parameters — showing acoustic, attack, wake, and guidance

Same file. These four lines stay as they are:

```java
choose(acousticHeadMode, config.getAcousticHeadMode());
choose(attackMode, config.getAttackMode());
choose(wakeSensor, config.getWakeGuidance());
choose(guidanceMode, config.getGuidanceMode());
```

Replace `choose`, and add `sameChoice`, `words`, `word`, and `constantName`. `setSelectedItem(enum)` leaves their boxes blank. The new `choose` picks the row by the words on screen: `PASSIVE` matches `Passive`, `SUB_SURFACE` matches `Sub Surface`, `BG_RIDER_OUT` matches `Bg Rider Out`, `ON` matches `ON`.

```java
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
    String left = words(value.toString());
    String right = words(item.toString());
    return left.equalsIgnoreCase(right);
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
        joined.append(word(parts[i]));
    }
    return joined.toString();
}

private static String word(String token) {
    if (token.length() == 0) {
        return token;
    }
    String lower = token.toLowerCase();
    return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
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

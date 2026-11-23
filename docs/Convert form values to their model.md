# Convert form values to their model

Java 1.6. Write listeners as anonymous classes. Do not use a lambda. Do not use `switch` on a `String`. Java 1.6 cannot do either.

This note is for the other project. The screen starts in `engagement/view/EngagementView.java`. That constructor builds the window. You add forms there, the same way `FunctionPage` adds them in this sample.

---

## 1. The problem, with one field

The user clicks the radio **EHWT WAR**.

Your form remembers that as text:

```text
"EHWT WAR"
```

That text is a `String`. `readForm()` puts it in `WeaponConfig.getWeapon()`. Every other box on the form is a `String` too. A number box still holds text, such as `"500"`.

Their model does not store that text. It stores an **enum**. An enum is a fixed list of named choices written in code:

```java
public enum TheirWeaponType {
    EHWT_WAR,
    EHWT_EXE
}
```

`EHWT_WAR` is one choice in that list. It is not the same thing as the text `"EHWT WAR"`. The space and the underscore are different, so you cannot hand the text straight to their setter.

Some of their fields are **objects**. An object is a small class that groups a few values, for example a distance and the unit it is measured in. A text field cannot hold that object. You build the object from the text.

The form can stay full of strings. A **converter** is a class whose only job is to translate. One method turns your strings into their enums and objects. Another method turns their enums and objects back into strings when a form needs to display them.

---

## 2. Words used below

| Word | Meaning |
|---|---|
| View | The class that builds the window and shows or hides panels. `EngagementView` is one. |
| Constructor | The method that runs when the object is created with `new`. It has the same name as the class. |
| Listener | Code that runs when a button is clicked. You attach it once, with `addActionListener`. |
| Form | The panel of fields. `readForm()` copies the fields into your string model. `apply` copies a string model back onto the fields. |
| Your model | `WeaponConfig` or `Engagement`. All fields are `String`. |
| Their model | The classes already in the other system. Fields are enums and objects. |
| Converter | A new class you write. It translates between those two. |
| Presenter | `weaponPresenter` or `engagementPresenter`. The view already receives it in the constructor. This is the object that saves and sends. |

---

## 3. Where each piece goes

You do not put everything in one file.

| What | Where you write it | What it does |
|---|---|---|
| Create the form, attach OK and Cancel | The view that owns that form | Engagement forms: `EngagementView`. Weapon forms: `WeaponConfigurationView`. |
| Show the form when a function key is pressed | The key method that already exists | Shows the panel. Does not convert. Does not attach OK. |
| Translate strings to their types | A new class in the model folder, for example `WeaponConfigConverter` | One method per field. |
| Their enums and objects | Leave their classes as they are | They keep the data. They do not learn the words on your buttons. They do not send. |
| Save and send | The presenter that is already there | The OK method calls a method the presenter already has. |

So: the listener lives in the view. The translation lives in the converter, next to the models. The sending stays in the presenter.

Putting the translation inside their enum mixes button labels into a class that should only list the choices. Putting the send inside the model makes the model talk to the network. The presenter already does that.

---

## 4. Attach the listener once

In the view constructor, after you create the form:

```java
weaponForm = new CreateWeaponConfigPanel();
weaponForm.setVisible(false);
rightPanel.add(weaponForm);

weaponForm.getOkButton().addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent event) {
        onWeaponOk();
    }
});
```

`onWeaponOk` is a normal private method on that same view. The click just calls it.

Do this in the constructor, not inside the F-key method. The F-key method runs every time the key is pressed. Attaching the listener there would stack a new listener on every press, and one OK click would save several times.

Weapon buttons are attached in `WeaponConfigurationView`, because that view created those forms. Engagement buttons are attached in `EngagementView`. A listener can only use fields of the class it is written in. If the weapon form field is on `WeaponConfigurationView`, the OK method for it belongs there too.

The F-key method only shows the form:

```java
weaponForm.reset();
weaponForm.setVisible(true);
weaponForm.revalidate();
weaponForm.repaint();
```

---

## 5. What OK does, in order

Four steps. Nothing else.

1. Read the form. You get strings.
2. Convert the strings into their object.
3. Give that object to the presenter. The presenter saves and sends.
4. Hide the form and show the engagement page again.

```java
private void onWeaponOk() {
    WeaponConfig formValues = weaponForm.readForm();
    TheirWeaponConfig theirs = WeaponConfigConverter.toTheirs(formValues);
    weaponPresenter.save(theirs);
    weaponForm.setVisible(false);
    showEngagementPage();
}
```

`TheirWeaponConfig` means their real class. Use the real class name.

`save` means the method their presenter already uses to store or send. Use the real method name. Pass `theirs`. Do not pass `weaponForm`. The presenter wants the data, not the panel.

`showEngagementPage` means the method they already have that shows the engagement panels. Call that method. Cancel calls the same method. Cancel does not read the form and does not convert.

Create Engagement OK is the same four steps. Use `EngagementConverter` and `engagementPresenter`.

---

## 6. Write the converter

Create `WeaponConfigConverter.java` in the model folder. One public method does the whole form. Small private methods do one field each.

```java
public final class WeaponConfigConverter {

    private WeaponConfigConverter() {
    }

    public static TheirWeaponConfig toTheirs(WeaponConfig form) {
        TheirWeaponConfig model = new TheirWeaponConfig();
        model.setWeapon(weapon(form.getWeapon()));
        model.setName(form.getConfigName());
        model.setInitialSpeed(speed(form.getInitialSpeed()));
        model.setEnbDist(wholeNumber(form.getEnbDist()));
        model.setMaxRd(decimal(form.getMaxRd()));
        model.setAcousticHead(acoustic(form.getAcousticHeadMode()));
        model.setWakeGuidance(onOff(form.getWakeGuidance()));
        return model;
    }

    private static TheirWeaponType weapon(String label) {
        if ("EHWT WAR".equals(label)) {
            return TheirWeaponType.EHWT_WAR;
        }
        if ("EHWT EXE".equals(label)) {
            return TheirWeaponType.EHWT_EXE;
        }
        return null;
    }

    private static TheirOnOff onOff(String label) {
        if ("ON".equals(label)) {
            return TheirOnOff.ON;
        }
        if ("OFF".equals(label)) {
            return TheirOnOff.OFF;
        }
        return null;
    }

    private static TheirAcoustic acoustic(String label) {
        if ("Simultaneous".equals(label)) {
            return TheirAcoustic.SIMULTANEOUS;
        }
        if ("Wake".equals(label)) {
            return TheirAcoustic.WAKE;
        }
        return null;
    }

    private static int wholeNumber(String text) {
        if (text == null || text.trim().length() == 0) {
            return 0;
        }
        return Integer.parseInt(text.trim());
    }

    private static double decimal(String text) {
        if (text == null || text.trim().length() == 0) {
            return 0;
        }
        return Double.parseDouble(text.trim());
    }
}
```

How to read `weapon`:

- `form.getWeapon()` returns the text `"EHWT WAR"` or `"EHWT EXE"` or `""`.
- The first `if` sees the WAR text and returns their constant `EHWT_WAR`.
- The second `if` does the same for EXE.
- Anything else, including a blank radio, returns `null`. Their setter then receives "no choice".

Copy this shape for every enum on the form. The text inside the quotes must match the label on the screen, including spaces. The value after `return` must be their real constant.

`wholeNumber` is for a box like Enb Dist. The box holds `"500"`. `Integer.parseInt` turns that text into the number `500`. A blank box is `""`, and `parseInt` crashes on a blank, so the method returns `0` first.

`decimal` is the same idea for a box like Max RD (`"26.99"`). It uses `Double.parseDouble`.

Change `setWeapon`, `setName`, and the other setters to the names their class really has. Add one line in `toTheirs` for each field you have not listed yet.

A field that is an object is built the same way. This example makes a range from the text and their unit:

```java
private static TheirRange range(String text) {
    return new TheirRange(decimal(text), TheirUnit.METRES);
}
```

Then in `toTheirs`:

```java
model.setSonarRange(range(form.getSafetySonarRange()));
```

`EngagementConverter` is a second class, same shape. `getGroup1()` is the engagement-state text (`"Prosp Eng"`, `"Real Engine"`). `getGroup2()` is the weapon text. `getP1()` through `getP9()` are the number boxes TB, RB, TP, and the rest. `getField2()` is the engagement name. If their model stores the name as text, copy that string across with no conversion.

---

## 7. Showing their data on a form

Modify and the list pages start from their object and fill the form. That is the reverse translation. Their constant `EHWT_WAR` becomes the text `"EHWT WAR"`, because that is the text the radio knows.

Add this method to the same converter:

```java
public static WeaponConfig fromTheirs(TheirWeaponConfig model) {
    WeaponConfig form = new WeaponConfig();
    if (model.getWeapon() == TheirWeaponType.EHWT_WAR) {
        form.setWeapon("EHWT WAR");
    } else if (model.getWeapon() == TheirWeaponType.EHWT_EXE) {
        form.setWeapon("EHWT EXE");
    }
    form.setConfigName(model.getName());
    form.setEnbDist(Integer.toString(model.getEnbDist()));
    return form;
}
```

`Integer.toString` turns the number `500` back into the text `"500"`.

The view asks the presenter for the stored object, converts it, and gives the strings to the form:

```java
TheirWeaponConfig found = weaponPresenter.findByName(name);
if (found != null) {
    modifyWeaponForm.apply(WeaponConfigConverter.fromTheirs(found));
}
```

`findByName` means their real find method. `apply` only accepts a `WeaponConfig`. After `fromTheirs`, that is what you have, so the text fields, radios, and drop-downs can show it.

---

## 8. A blank field

If the user leaves a box empty, `readForm()` gives `""`.

- For an enum method, return `null`.
- For `wholeNumber` and `decimal`, return `0`.
- For a name, pass the empty string through.

Do this inside the converter. The OK method stays the four steps in section 5.

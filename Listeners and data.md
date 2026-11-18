# Listeners and data

Java 1.6. No lambdas. A listener is an anonymous class with `actionPerformed`. A form shows data by calling setters on the controls. The controls never go into the presenter.

The window opens on Engagement Parameters.

| Key | What appears | OK does |
|---|---|---|
| F1 | `CreateEngagementPanel`, empty | `readForm()`, store the `Engagement`, show Engagement Parameters again |
| F2 | `ModifyEngagementPanel`. Engagement Name is a drop-down of stored names | Choosing a name loads that engagement. OK replaces it, then shows Engagement Parameters again |
| F3 | `DeleteEngagementPanel` | Read Eng No and Eng Name, remove that engagement, show Engagement Parameters again |

Modify is its own panel, `ModifyEngagementPanel`. The header is MODIFY ENGAGEMENT. Engagement Name is a drop-down. F2 fills that drop-down from the names already stored. Choosing one calls `apply` for that engagement.

The number list on Delete, and the values on Modify, come from the engagements you stored at OK. That link is the part to add on the other system. The forms themselves are already built.

---

## 1. A listener in Java 1.6

`addActionListener` is the attachment. `actionPerformed` is the method that runs on the click. Both names are from `java.awt.event`.

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

Attach this once, in the constructor of `EngagementView` or `WeaponConfigurationView`, after `new CreateEngagementPanel()`. Do not attach it again inside the F1 method. F1 only shows the form. OK is a different button and has its own listener.

Rules that keep this compiling on Java 6:

* Write `new ActionListener()` and a `public void actionPerformed`. Do not write `->`.
* A local variable used inside `actionPerformed` is `final`. A field of the view does not need `final`.
* `JComboBox` has no type argument. Write `JComboBox`, not `JComboBox<String>`.
* Use `ArrayList` or `Vector`. Both exist in Java 6. Construct them as `new ArrayList()` with no `<>`.

```java
final CreateEngagementPanel form = new CreateEngagementPanel();
```

`final` is there so the anonymous class is allowed to call `form.readForm()`.

Cancel is a second listener on `getCancelButton()`. It hides the form and shows Engagement Parameters. It does not call `readForm()`.

---

## 2. Where this sits on their window

`EngagementView` already has an action method per key. That method calls `setVisible(false)` and `setVisible(true)` on the panels it owns. Leave that method as the place that shows a page. Put the OK work in the listener above, not in the key method.

```java
private void onCreateKey() {
    form.resetForCreate();
    parametersPanel.setVisible(false);
    form.setVisible(true);
    form.revalidate();
    form.repaint();
}

private void onModifyKey() {
    Engagement engagement = engagementPresenter.getSelected();
    if (engagement == null) {
        return;
    }
    form.apply(engagement);
    parametersPanel.setVisible(false);
    form.setVisible(true);
}

private void onDeleteKey() {
    deleteForm.setEngNumbers(engagementPresenter.numbers());
    parametersPanel.setVisible(false);
    deleteForm.setVisible(true);
}
```

`getSelected` and `numbers` stand for the methods on their engagement presenter. Use the names that presenter already has.

The form is added once, in the constructor, to the same panel their other pages use, and it starts invisible:

```java
form = new CreateEngagementPanel();
form.setVisible(false);
rightPanel.add(form);

deleteForm = new DeleteEngagementPanel();
deleteForm.setVisible(false);
rightPanel.add(deleteForm);
```

---

## 3. The data object

`page.model.Engagement` is a class of strings. It has no `JTextField`, no `JButton`, and no import from `javax.swing`. The presenter stores these objects. The panel is only a way to edit one of them.

| On the form | On the object |
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

`readForm()` builds a new `Engagement` from the controls. `apply(engagement)` writes an existing `Engagement` back onto those same controls.

A list on the presenter holds every engagement that OK has stored. Java 6:

```java
private final List engagements = new ArrayList();

public void save(Engagement engagement) {
    engagements.add(engagement);
}

public Engagement find(String engNo) {
    for (int i = 0; i < engagements.size(); i++) {
        Engagement engagement = (Engagement) engagements.get(i);
        if (engNo.equals(engagement.getField1())) {
            return engagement;
        }
    }
    return null;
}
```

The cast `(Engagement)` is required. Java 6 lists are not generic here if the project compiles without generics on collections. If their presenter already uses `List<Engagement>`, keep their style. Do not introduce a library.

Create OK adds one object. Modify OK finds the object with the same Eng No and replaces it. Delete OK finds it and removes it. After each of those, the parameters panels go visible and the form goes invisible.

---

## 4. You received an engagement. Put it on the form

Call `apply`. One call fills every control. F2 is this call.

```java
Engagement engagement = engagementPresenter.find(engNo);
if (engagement != null) {
    form.apply(engagement);
}
```

`apply` uses the setters below. Use a setter yourself only when you are filling one control.

| Control | How the value goes in |
|---|---|
| Eng No, Engagement Name, Comments | `setValue` on the hint field. `""` shows the grey hint again. |
| A radio group | `setSelected("Tube 1")`. The string must equal the button text. `""` clears the group. |
| Torpedo Distance and the number fields | `setText` on the `JTextField`. |
| A drop-down | `setSelectedItem("Surface")`. The item must already be in the combo. |
| Delete, Eng Name | `deleteForm.setEngName(engagement.getField2())`. |

Hint fields are the exception to `setText`. `setText` would store the hint sentence as a real value. `setValue` is the method those three fields understand.

---

## 5. You have several engagements. Fill the Delete list

`setEngNumbers` replaces the Eng No drop-down. Build the array from the stored engagements, then pass it.

```java
String[] numbers = new String[engagements.size()];
for (int i = 0; i < engagements.size(); i++) {
    Engagement engagement = (Engagement) engagements.get(i);
    numbers[i] = engagement.getField1();
}
deleteForm.setEngNumbers(numbers);
```

The combo then contains those numbers, and the first one is selected. An empty array leaves the combo empty.

Read the selected number with `getEngNo()`. Put the matching name in with `setEngName`. Do this when F3 opens the page, for the number that is already selected:

```java
deleteForm.setEngNumbers(numbers);
Engagement engagement = engagementPresenter.find(deleteForm.getEngNo());
if (engagement == null) {
    deleteForm.setEngName("");
} else {
    deleteForm.setEngName(engagement.getField2());
}
```

The list on screen starts as `"1"`, `"2"`, `"3"` so the form matches the Delete picture before any engagement has been stored. `setEngNumbers` is what replaces that practice list.

---

## 6. Delete OK

```java
deleteForm.getOkButton().addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent event) {
        String engNo = deleteForm.getEngNo();
        engagementPresenter.remove(engNo);
        deleteForm.setVisible(false);
        parametersPanel.setVisible(true);
    }
});
```

`remove` finds the `Engagement` whose `getField1()` equals `engNo` and takes it out of the list. The next F3 builds the combo from whatever is left, so the deleted number is gone.

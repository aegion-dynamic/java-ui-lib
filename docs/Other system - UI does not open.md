# Other system — the window does not open

This note is for the other project, the one under the `engagement` folder. The practice screen in this repository already opens. Use this when a change compiles and the window still never appears.

The files in that folder are:

| File | Role |
|---|---|
| `EngagementView.java` | The window. It is what should appear on screen. |
| `EngagementParameters.java` | The first page, Engagement Parameters. |
| `CreateEngagement.java` | The Create Engagement page. It has the bean, the constructor, and `createWidget()`. |

`CreateEngagement` also builds the Engagement Parameters page. Both pages are initialized from that one class. Commenting out `createWidget()` in its constructor leaves both pages unbuilt.

---

## 1. A clean build does not mean the window was built

The compiler checks that every name exists and every type matches. It does not run the constructor.

`createWidget()` can sit unused in the class and the build still succeeds. At run time the constructor returns an object whose screen was never created. `EngagementView` then has nothing to show, so no window appears.

Put the new form in the same place `createWidget()` used to fill. Do not delete that call until the view is reading the new panel instead.

---

## 2. What that one call is doing

Open `CreateEngagement` and read `createWidget()` from top to bottom. Mark each line as one of these:

| Kind of line | What breaks if you skip it |
|---|---|
| `new EngagementParameters(...)` or a call into that class | The first page never exists. |
| `new` of the Create Engagement fields, or a call that builds that page | The second page never exists. |
| Assignment into a field (`this.panel = ...`, `this.bean = ...`) | Later code reads that field and finds `null`. |
| `add(...)`, `setContentPane(...)`, `cards.add(...)` | The page exists and is not attached to the window. |
| `setPreferredSize`, `setSize`, `pack`, `revalidate`, `repaint` | The window can open with no size and look like it did not open. |
| `setVisible(true)` on the view | Nothing displays until some other line shows it. |
| `addActionListener`, `new` of the bean, filling the bean | The screen can still appear. OK and the first page then have empty data. |

If the only call to `createWidget()` is that constructor line, the whole table above is skipped together.

---

## 3. The reference that usually goes null

Search the `engagement` folder for every name that `createWidget()` writes.

1. In `CreateEngagement`, list every field assigned inside `createWidget()` and nowhere else.
2. Search `EngagementView.java` for those same names: `getPanel`, `getWidget`, `getContent`, the parameters page, the create page, the bean.
3. Search for `new CreateEngagement`. There should be one instance. A second `new` builds a screen the view never adds.

A typical break looks like this:

```text
EngagementView
    holds CreateEngagement screen
    screen is still null
         |
         v
view tries to add(screen) or screen.getPanel()
         |
         v
NullPointerException on the startup thread
         |
         v
setVisible never runs, so no window
```

The stack trace names the line. A window that fails this way does not draw a half-built form first.

---

## 4. Checks before you remove `createWidget()`

Do these in the other project. Each one is a yes or no.

1. **Who calls it.** Search for `createWidget`. Write down every caller. The constructor of `CreateEngagement` is one. `EngagementParameters` may have its own `createWidget` that `CreateEngagement` calls. Comment out only the call you have replaced.
2. **Who shows the window.** Search `EngagementView` for `setVisible(true)`, `show()`, `open()`, and `pack()`. That line must still run after your change. If it sits inside `createWidget()`, move it out before you comment the method out.
3. **What the view adds.** Find the `add` that places the engagement pages onto the view. The object in that `add` must be the panel you still construct. An empty `JPanel` compiles and shows a blank window.
4. **One instance.** The object you build and the object you `add` must be the same variable. Building the new form and leaving the view attached to the old field shows nothing of the new form.
5. **Bean before fields.** The bean is created first, then `createWidget()` reads it. If a line in `createWidget()` uses the bean, keep the `new` of the bean above the widget call. A widget built with a null bean throws before the window opens.
6. **Both pages.** `CreateEngagement.createWidget()` builds Engagement Parameters as well as Create Engagement. Replacing that method means constructing both pages yourself and still adding both to the view.
7. **Caught errors.** Search the view and the constructor for `catch (Exception` with an empty body, or a catch that only sets a flag. An exception there stops the window and prints nothing in the UI.

---

## 5. See which line actually stops

Run the other system from a terminal so the stack trace stays on screen. Add four prints, then start it once:

```java
System.out.println("view start");
System.out.println("create engagement constructor");
System.out.println("createWidget start");
System.out.println("createWidget end");
System.out.println("setVisible");
```

Read the last line that prints.

| Last line you see | What it means |
|---|---|
| `view start` only | The view throws before it constructs `CreateEngagement`. The fault is in `EngagementView`, above that `new`. |
| `constructor` and no `createWidget start` | The constructor throws, or the call you commented out is the last work it did. Anything the view needs was not created. |
| `createWidget start` and no `createWidget end` | The throw is inside `createWidget`. The stack trace points at the line. |
| `createWidget end` and no `setVisible` | The pages exist and the view throws while adding them or showing them. Often a null page reference. |
| `setVisible` and still no window | The window was shown with no size, or the panel was never added. Check `pack()` or `setSize`, and check that `add` received the panel. |

Wrap the constructor body once while you are hunting. Take the print out when the window opens.

```java
try {
    createWidget();
} catch (RuntimeException ex) {
    ex.printStackTrace();
    throw ex;
}
```

---

## 6. How to swap the form without losing the window

Keep this order in `CreateEngagement`:

1. Create the bean.
2. Create the outer panel the view already knows how to add.
3. Build Engagement Parameters and add it where the first page goes.
4. Build Create Engagement and add it where the second page goes.
5. Return from the constructor.
6. Let `EngagementView` add that outer panel and call `setVisible`.

The practice form from this repository is a `JPanel`. It can be the child added in step 4. It does not replace `EngagementView`, and it does not replace the outer panel the view already adds.

```java
CreateEngagementPage form = new CreateEngagementPage();
outer.add(form, BorderLayout.CENTER);
```

Use the class name you copied in. Keep the variable the view already stores. Point that variable at `outer`, the panel you still add to the view.

Leave `createWidget()` in place until step 2 and the view’s `add` both use that same panel. Then the method can become the place that only fills the panel, or you can inline those lines in the constructor. The view’s `add` and `setVisible` stay where they are.

---

## 7. Mismatches that compile and still show no window

| What you changed | What to look at |
|---|---|
| Commented out `createWidget()` and did not add another panel | The view’s child is null or empty. |
| Built the new form into a local variable and did not store it on the field the view reads | The view still has the old null field. |
| Added the form to a panel that is never added to `EngagementView` | The form exists off to the side. |
| Left `EngagementParameters` created only inside the removed method | The first page is gone, and a later `get` on it throws before `setVisible`. |
| Two classes with the same simple name in different packages | The view imports the old one. The new one never runs. |
| `createWidget()` caught an exception and returned | The method ends quietly. The prints in section 5 show start without end, or end with a stack trace above it. |

Search the view for the field name. If that field is not assigned on the path you left in the constructor, the window cannot show that page.

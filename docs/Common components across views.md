# Common components across views

Engagement and wire guidance are two views. ECS is the parent. The top panel and the footer are common: one of each, for both views.

What you are seeing fits that split. The footer works on engagement. After wire guidance is shown, and again after engagement is shown once more, the same bar is still on screen and the buttons do nothing. The top panel fails the same way. The paint path is fine. The objects that receive clicks and keys are no longer the objects ECS is showing, or a parent has turned input off.

`setVisible(false)` inside a child view is the usual source. A child view may hide itself. It must not hide, disable, remove, or rebuild the top panel or the footer.

The top panel in this sample is `CommonHeader`. How that one bar is shared, and what each icon does, is in `docs/Common header.md`.

---

## 1. Who owns what

ECS creates the common pieces once and adds them once, to itself.

```text
ECSView
    NORTH   top panel          one instance, child of ECS
    CENTER  engagement view    engagement content only
            wire guidance view wire-guidance content only
    SOUTH   footer             one FunctionKeyBar, child of ECS
```

The engagement view holds engagement panels. The wire guidance view holds the note, the Depth Window, and the drop-downs. Neither view is the parent of the top panel or the footer.

A Swing component has one parent. `add` on a second container removes the component from the first. After that, `setVisible` on the first container no longer controls it, and `setVisible` on the second one does.

---

## 2. What the switch should do

ECS is the only class that changes views. The home button and the view choice both call the same method.

```java
private void showWireGuidance() {
    engagementView.setVisible(false);
    wireGuidanceView.setVisible(true);
    footer.setLabels(FunctionKeyBar.KEYS, WC_TEXT);
}

private void showEngagement() {
    wireGuidanceView.setVisible(false);
    engagementView.setVisible(true);
    footer.setLabels(FunctionKeyBar.KEYS, ENG_TEXT);
}
```

`setVisible(false)` runs on the view that is leaving. `setVisible(true)` runs on the view that is entering. The top panel and the footer are not in either call.

`setLabels` changes the words on the one footer. It does not build a new bar. The listener stays the listener ECS passed in when the bar was constructed.

The child view's own `setVisible` is the inherited one:

```java
public void setVisible(boolean visible) {
    super.setVisible(visible);
}
```

That hides or shows that view's own children. It does not mention the footer, the top panel, or any other common component.

---

## 3. Why the buttons stay visible and stop working

Check these in order. The first one that matches is the one to fix. More than one can be true at once.

### The footer was added to the engagement view

The working bar is a child of engagement. Engagement's `setVisible(false)` hides that whole subtree, including the bar our listener is on.

Wire guidance, or ECS, still has a footer-shaped panel in the south. That panel was built earlier and never received the listener. It paints the same F1–F12 squares, so it looks right. Clicks land on it and nothing is listening.

Coming back runs `engagementView.setVisible(true)`. That shows the original bar only if it is still a child of engagement. If wire guidance also executed `add(footer)`, Swing already moved the real bar under wire guidance. Engagement comes back empty of that bar, wire guidance is now hidden, and the bar you see is the other one.

Print this on the way out and on the way back:

```java
System.out.println(footer.getParent().getClass().getName());
System.out.println(footer.isShowing());
```

The parent must be ECS both times. `isShowing()` must be true while the bar is on screen. A parent that changes from ECS to a view, or `isShowing()` false while you can still see a bar, means two bars.

### The view's hide code touches the common objects

The view was given the top panel and the footer in its constructor, the same way a view is given the right end panel and the bottom end panel. Its hide path then does one of these:

```java
footer.setVisible(false);
topPanel.setVisible(false);
footer.setEnabled(false);
topPanel.setEnabled(false);
footer.removeAll();
removeActionListener(...)
```

ECS later sets the view visible again. That does not undo those lines. The view's `setVisible(true)` shows the view. It does not put the listeners back, and it does not enable a component the view disabled on the way out.

On this GTK theme a disabled control often still looks enabled. The button is on screen, the colours are normal, and the click is ignored. Check `isEnabled()` on the footer and on the top-panel controls after the round trip. A false value here is this cause.

`removeAll()` on the footer throws away the key buttons. Whatever is painted afterwards is new buttons with no listener.

### A panel is covering the common bar

The wire guidance view is added on top of the footer, or it is sized to the whole window, including the south strip. The footer still paints. The mouse hits the view that is higher in the z-order.

`setVisible(false)` on that view should stop it taking clicks. If the hide path hides an inner panel and leaves the full-size view visible, the cover stays after you return to engagement.

Click the dead button and print the deepest component at that point:

```java
Component hit = SwingUtilities.getDeepestComponentAt(ecs, x, y);
System.out.println(hit.getClass().getName());
```

The class has to be the footer button. A wire-guidance panel, a glass pane, or an empty `JPanel` means the footer is underneath something else.

### The function keys are bound to a bar that is no longer showing

`FunctionKeyBar` binds F1–F12 with `WHEN_IN_FOCUSED_WINDOW` on itself. Swing delivers those keys only while that bar is showing. A parent set to invisible takes the bindings with it.

A second bar on screen has no bindings. The physical keys and the clicks on that second bar do nothing. The bar that owns the bindings is under the hidden view.

### The listener is still attached and returns immediately

This one leaves the buttons working and the operations wrong. A click still runs the listener. If the listener starts with `if (wireGuidanceVisible) return;` and that flag stays true after the return to engagement, engagement keys do nothing useful.

That does not, by itself, kill the top panel. The top panel and the footer dying together points at the parent, `setEnabled`, `removeAll`, or a second copy. Use this check only after the footer and the top panel are confirmed to be the same objects, enabled, showing, and uncovered.

---

## 4. The right rules

1. Construct the top panel and the footer in ECS. Add each one to ECS. Do not construct them again inside a view.
2. A child view calls `setVisible` only on itself. The method body is `super.setVisible(visible)`.
3. A child view does not call `setVisible`, `setEnabled`, `remove`, `removeAll`, or `removeActionListener` on the top panel or the footer.
4. Do not pass the footer into a view and `add` it there. The constructor can receive ECS so the view can ask ECS to run an operation. It should not receive the bar in order to parent it.
5. One listener, owned by ECS. The listener reads which view is showing, then calls a method on that view. The view does not own a second bar.
6. Switching views changes labels on the existing bar. `footer.setLabels(...)`. The blue squares and the listener stay.
7. `setEnabled(false)` is not a way to hide a view. On GTK the control still looks active. Hide with `setVisible(false)` on that view alone.

A shared piece that has to sit inside the showing view, such as COG/SOG above the wire-guidance column, is moved by ECS. Take it off the current parent, add it to the view that is about to show, and take it back before that view is hidden. The view does not hide that piece in its own `setVisible`.

```java
private void showWireGuidance() {
    engagementColumn.remove(readout);
    wireGuidanceView.placeReadout(readout);
    engagementView.setVisible(false);
    wireGuidanceView.setVisible(true);
    footer.setLabels(FunctionKeyBar.KEYS, WC_TEXT);
}

private void showEngagement() {
    wireGuidanceView.takeReadout();
    engagementColumn.add(readout, BorderLayout.NORTH);
    wireGuidanceView.setVisible(false);
    engagementView.setVisible(true);
    footer.setLabels(FunctionKeyBar.KEYS, ENG_TEXT);
}
```

The move happens before the old view is hidden, so the shared strip is not a child of the view that is about to disappear.

---

## 5. What to look at in ECS and in each view

Search the engagement view and the wire guidance view for the footer field, the top panel field, `setVisible`, `setEnabled`, `removeAll`, and `add(`.

| Found in a child view | What it does on a page change |
|---|---|
| `add(footer)` or `add(topPanel)` | Moves the real component under that view. The next `setVisible(false)` on that view hides it. |
| `new FunctionKeyBar` | A second bar. Only the instance that was given the listener works. |
| `footer.setVisible(false)` or `topPanel.setVisible(false)` | Hides the common piece. The matching `setVisible(true)` has to be in the show path of every view, or it stays hidden. |
| `footer.setEnabled(false)` or the same on the top panel | Clicks stop. GTK still paints it as active. The show path has to set enabled back to true. |
| `removeAll()` on the bar, the top panel, or a parent of either | Drops the buttons. The new ones have no listener. |
| `setVisible` that hides every component the view can see, including ones passed in | Same result as the lines above. Limit it to `super.setVisible(visible)`. |

In ECS, search for a second `add` of the same footer and for the home-button handler. The handler should call one show method. Each show method hides one view, shows the other, and updates labels. It does not construct a bar.

---

## 6. Notes

- Visible and working are different. A round trip that leaves the colours in place can still have dropped the listener, moved the parent, or disabled the component.
- Fix the parent first. While `footer.getParent()` is a view, every later `setVisible(false)` on that view will take the bar with it.
- Keep one footer instance for the life of the window. `setLabels` is the switch. A new `FunctionKeyBar` in the wire guidance path will not have the ECS listener.
- The physical F-keys follow the bar they were bound on. A bar that is not showing does not receive them, even when another bar is painted in the south.
- COG/SOG is the pattern for a shared piece that must move. The parent moves it. The views only offer `placeReadout` and `takeReadout`.
- This sample does the same split. `FunctionPage` owns the top bar and the footer. `WcPage` is the wire guidance view and does not contain either one. ECS in the other project is the class in the `FunctionPage` role.

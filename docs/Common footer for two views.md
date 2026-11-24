# One footer, two views

Eng and WC use the same footer. The buttons are not rebuilt when the page changes. The blue squares stay F1–F12. What changes is the words beside each square, and which method runs when that key is pressed.

There is one `FunctionKeyBar`, added once with `BorderLayout.SOUTH`. Do not put a second bar inside the WC page. The real F-keys are bound with `WHEN_IN_FOCUSED_WINDOW`, so a bar that is only hidden would still receive F1–F12.

---

## 1. What stays, and what changes

| | Eng | WC |
|---|---|---|
| The twelve buttons | the same objects | the same objects |
| The blue F1–F12 squares | unchanged | unchanged |
| The words | Create, Modify, Delete, … or the set after F4 | Guid Mode, LL BR Out, BR In, … |
| The method that runs | `create()`, `onF2()`, … | `onGuidMode()`, `onLlBrOut()`, … |

`setLabels` is the only call that switches the bar:

```java
footer.setLabels(FunctionKeyBar.KEYS, WC_TEXT);
```

`FunctionKeyBar.KEYS` is `"F1"` through `"F12"`. That string is the command the listener receives. `WC_TEXT` is only the words drawn on the button. The listener never reads the words. It reads the command.

The WC words, in order:

| Key | Words |
|---|---|
| F1 | Guid Mode |
| F2 | LL BR Out |
| F3 | BR In |
| F4 | CCM |
| F5 | TCCM |
| F6 | Self Guidance |
| F7 | Manu |
| F8 | WG End |
| F9 | Avoid Target |
| F10 | LLCHM |
| F11 | -------- |
| F12 | Stop |

F11 has no operation. The dashes are the caption. The listener does nothing for that key.

---

## 2. Change the words when the view changes

The top drop-down is the moment the view changes. Set the flag first, then the cards, then the footer. The flag has to be set before a key can be pressed, so the listener already knows which view it is in.

```java
if ("WC".equals(item)) {
    wcPage = true;
    leftCards.show(leftSlot, "wc");
    rightCards.show(rightBody, "wc");
    footer.setLabels(FunctionKeyBar.KEYS, WC_TEXT);
} else {
    wcPage = false;
    leftCards.show(leftSlot, "eng");
    rightCards.show(rightBody, "eng");
    if (secondSet) {
        footer.setLabels(SECOND_KEYS, SECOND_TEXT);
    } else {
        footer.setLabels(FunctionKeyBar.KEYS, FIRST_TEXT);
    }
}
```

Eng has two word sets of its own. F4 flips `secondSet` and the commands become `"B1"`, `"B2"`, and the rest. Coming back from WC must restore the set that was showing, not always the first set. `secondSet` is that memory. WC does not change `secondSet`.

`setLabels` also rebinds the physical F-keys. The map name is the key code (`function-112` for F1), so the new command replaces the old one. A click and a keypress send the same command to the same listener.

---

## 3. Make the operation follow the view

One listener serves every button. The command says which slot was pressed. The flag says which view is open. Both are required. F1 on Eng and F1 on WC send the same command, `"F1"`, and they must not call the same method.

```java
public void actionPerformed(ActionEvent event) {
    String key = event.getActionCommand();
    if (wcPage) {
        onWcKey(key);
        return;
    }
    if ("F1".equals(key)) {
        create();
    } else if ("B1".equals(key)) {
        onB1();
    }
    // the rest of the Eng keys
}

private void onWcKey(String key) {
    if ("F1".equals(key)) {
        onGuidMode();
    } else if ("F2".equals(key)) {
        onLlBrOut();
    } else if ("F3".equals(key)) {
        onBrIn();
    } else if ("F4".equals(key)) {
        onCcm();
    } else if ("F5".equals(key)) {
        onTccm();
    } else if ("F6".equals(key)) {
        onSelfGuidance();
    } else if ("F7".equals(key)) {
        onManu();
    } else if ("F8".equals(key)) {
        onWgEnd();
    } else if ("F9".equals(key)) {
        onAvoidTarget();
    } else if ("F10".equals(key)) {
        onLlchm();
    } else if ("F12".equals(key)) {
        onStop();
    }
}
```

Each `on…` method is the operation for that WC key. It belongs in the view (`FunctionPage` here, `EngagementView` on the other machine), next to `create()` and `onF2()`. The footer does not call the presenter. The method does.

On the other machine the body of `onGuidMode()` is the call they already have for that algorithm. The sample methods can stay empty until those calls are known. An empty method still counts as wired: the key reaches it, and it does not fall through into Create or Modify.

---

## 4. Notes

- The sample listener returns immediately when `wcPage` is true, before any `if`. The WC words show, and no operation runs. Replace that bare `return` with `onWcKey(key)`, as in section 3.
- Do not test the caption. `"Guid Mode".equals(key)` is never true. The command is `"F1"`.
- Do not build the WC operations by copying the Eng branch. F1 would open Create Engagement while the WC page is showing.
- F4 on Eng swaps the Eng word set. F4 on WC is CCM. The swap must sit in the Eng branch, after the `wcPage` test.
- A second footer, even inside a card that is not showing, also receives F1–F12. Keep one bar and call `setLabels`.
- `setLabels` needs twelve commands and twelve words. A shorter array throws when the loop reads past the end.
- Java 1.6 has no `switch` on a `String`. The chain of `else if` is the branch.

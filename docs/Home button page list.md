# Home button page list

Hover the home icon. A list opens under it. The list in this sample is ENGM and WC. Choosing one tells the window which page to show. The header does not show the page itself.

Their system passes its own list in, and the listener passes the chosen name back to their view switch.

---

## 1. Give the header their names

Call this once, after the header is constructed, from ECS. The strings are the words on the menu. They are also the words the listener receives.

```java
header.setPages(new String[] {"ENGM", "WC"});
```

Use their real page names if those words are different. The header does not know what a name means. It only shows the list and sends the chosen string.

---

## 2. What the listener receives

A choice sends one command: `page:` followed by the name they put in the list.

| They put in the list | The listener receives |
|---|---|
| `ENGM` | `page:ENGM` |
| `WC` | `page:WC` |

```java
public void actionPerformed(ActionEvent event) {
    String command = event.getActionCommand();
    if (command.startsWith("page:")) {
        String selected = command.substring(5);
        showSelectedPage(selected);
    }
}
```

`substring(5)` drops the five characters of `page:`. `selected` is the exact list entry. Pass that string into their system. Do not parse the icon, and do not read the green chip.

---

## 3. Pass the selection to their view switch

`showSelectedPage` belongs on ECS, next to the code that already sets one view visible and the other invisible. The header never calls `setVisible`.

```java
private void showSelectedPage(String selected) {
    if ("WC".equals(selected)) {
        engagementView.setVisible(false);
        wireGuidanceView.setVisible(true);
        footer.setLabels(FunctionKeyBar.KEYS, WC_TEXT);
    } else {
        wireGuidanceView.setVisible(false);
        engagementView.setVisible(true);
        footer.setLabels(FunctionKeyBar.KEYS, ENG_TEXT);
    }
    header.setPageName(selected);
}
```

`setPageName` updates the green chip after the view is showing. The chip is a label. It is not the list.

If their switch already takes a page name, call that method with `selected` and then set the chip:

```java
ecs.showView(selected);
header.setPageName(selected);
```

One name in, one view shown. The other views are set invisible by that same method, not by the home button.

---

## 4. Hover, not a second bar

The menu opens while the pointer is on the home icon. It stays open while the pointer is on the list. It closes when the pointer leaves both.

A click on the house does not choose a page. Only a click on a name does. That click is the `page:` command above.

The list is rebuilt from the last `setPages` each time it opens. Change the names with another `setPages` call. Do not build a second header to change the list.

---

## 5. Notes

- One header, owned by ECS. The home menu is part of that header. A view does not add its own home button.
- The command is the name they supplied. `page:ENGM` means the list entry was `ENGM`.
- Their view switch is the only place that calls `setVisible` on the pages. See `docs/Common components across views.md`.
- The footer labels change in that same switch, on the one footer. The home list does not build a footer.
- ENGM on the green chip is the page that is open. The list under home is how the user picks a different one.

# Common header

The black bar at the top is one `CommonHeader`. Engagement and wire guidance both use it. ECS creates it once and adds it once, to itself, with `BorderLayout.NORTH`.

A child view does not construct a second header, does not `add` this one, and does not call `setVisible` or `setEnabled` on it. Hiding a view hides that view only. The header stays where ECS put it, so its clicks and its icon state survive the page change.

---

## 1. What is on the bar

Left to right:

| Control | Click | PPI |
|---|---|---|
| Home | Hover opens the page list. The chosen name is sent as `page:` plus that name. See `docs/Home button page list.md`. | No |
| ENGM / WC | The green chip. Switches the centre view. ECS sets the word with `setPageName`. | No |
| Label | On, then off. | No |
| Vector | Off, 15, 30, 60, then back to off. | No |
| Trail | Off, then on. | No |
| Trail time | 15, 30, 60, 4H, then back to 15. | No |
| Dial | On (green), then off (red). | Yes |
| Grid | Off, then on. | Yes |
| Baffle | On, then off. | Yes |
| Map | Opens the menu. Map On / Map Off. Map Zoom is disabled. Color Scheme opens Day Bright, Day_Black Back, Dusk_Black Back, Night_Bright, Night_Filter. Display Group is disabled. | Yes |
| RRing | On (blue), then off (red). | Yes |
| North | North, then course. | No |
| 0.125 Nm | Empty slot between North and REL. `getRangeSlot()` is where that button is added later. `setRangeText` only stores the words, starting as `0.125 Nm`. | No |
| REL / TRU | REL, then TRU. | No |
| MONIT | Yellow is on. Click turns it off, and back on. | No |
| Clock | Popup with Mumbai and GMT+2. | No |
| Calculator | A calculator window. | No |
| Stopwatch | A stopwatch window. | No |

The listener command for a cycling icon is the name, the state number, and `:ppi` when that icon changes the PPI. `dial:1:ppi` is Dial off. `label:0` is Label on. Home sends `home`. The chip sends `page`.

State 0 is the first icon in the list in `CommonHeader`. The pictures of each state are the files in `src/main/resources/icons/`.

---

## 2. How ECS shares it

```text
ECSView
    NORTH   CommonHeader     one instance
    CENTER  engagement view
            wire guidance view
    SOUTH   footer           one instance
```

```java
header = new CommonHeader(new ActionListener() {
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if ("home".equals(command)) {
            showEngagement();
        } else if ("page".equals(command)) {
            if (wireGuidanceVisible) {
                showEngagement();
            } else {
                showWireGuidance();
            }
        } else if (command.endsWith(":ppi")) {
            ppi.applyHeader(command);
        }
    }
});
add(header, BorderLayout.NORTH);
```

`showEngagement` and `showWireGuidance` hide one view and show the other. Then they set the chip:

```java
header.setPageName("ENGM");
header.setPageName("WC");
```

The chip does not read the view. ECS writes the word after the view change, so the word and the visible page stay the same object’s decision.

`ppi.applyHeader` is their chart update. It belongs next to the chart, called from ECS. The header does not hold the engagement view or the wire guidance view, and those views do not hold the header.

---

## 3. What must not happen

| In a child view | What breaks |
|---|---|
| `new CommonHeader` | A second bar. Clicks on the one you see are not the clicks ECS listens to. |
| `add(header)` | Swing moves the real bar under that view. `setVisible(false)` on the view then hides it, or leaves it inside the hidden view while another bar is painted. |
| `header.setVisible(false)` or `setEnabled(false)` | The bar is still in the layout and looks normal on GTK, and the icons stop taking clicks. |
| Remembering icon state inside the view | Switching pages builds a new idea of Label or Dial. The bar on screen is still the old one, so the picture and the PPI disagree. |

Icon state lives on the one header. Leave the page and come back: Label, Vector, Dial, and the rest are still where the last click put them. That is the shared behaviour.

---

## 4. Notes

- One header for the life of the window. Page changes call `setPageName` and `setVisible` on the views.
- `:ppi` on the command is the mark for the chart. Label, Vector, Trail, Trail time, North, REL, MONIT, Clock, Calculator, and Stopwatch do not carry it.
- Map Zoom and Display Group are on the menu and do nothing. Zoom is disabled. Display Group is not developed.
- Clock, calculator, and stopwatch are windows owned by the header. They are not panels inside engagement or wire guidance.
- The 0.125 Nm control is not drawn yet. The gap between North and REL is `getRangeSlot()`. Add the button to that panel when it is ready. `setRangeText` stores the caption until then.

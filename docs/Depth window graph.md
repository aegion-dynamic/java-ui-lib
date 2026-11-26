# Depth window graph

The Depth Window on the WC page draws one curve. The axes show when the page opens. The line appears only after data is given. The gold cross follows the mouse along X. Its Y is always the curve at that X. New points do not move it.

For now the sample loads a dummy line in `loadDummyDepth` so there is a curve under the pointer. Delete that method when the real samples are wired. The cross still follows the mouse after the dummy line is gone.

The graph is `DepthGraph`. The wire-guidance view, `WcPage`, holds one of them and forwards three calls: `setDepthBulk`, `addDepthPoint`, and `clearDepth`. Call those on the view. Do not hand the panel to the code that owns the numbers. `setDepthWindow` replaces that whole panel. After that replacement, those three calls still talk to the graph the view built first.

X and Y on the graph are plain `double` values. This sample does not turn them into `Depth` or `Distance`. The caller decides what one unit means.

---

## 1. Two ways to give data

| Call | What it does | Where the cross goes |
|---|---|---|
| `setDepthBulk(double[] x, double[] y)` | Replaces the whole line | Stays with the mouse |
| `addDepthPoint(double x, double y)` | Appends one point | Stays with the mouse |
| `clearDepth()` | Removes the line | Stays with the mouse. The axes stay |

`setBulk`, `addPoint`, and `clear` are the same three operations on `DepthGraph` itself. The WC methods only forward to them.

Bulk is a replacement. A second `setDepthBulk` throws away the first line. It does not append. To keep growing the line, use `addDepthPoint`.

One point draws no line. The line starts at the second point and connects the points in the order they were given. The cross is separate from those points.

```java
double[] x = { 0, 2, 6, 14, 24 };
double[] y = { 0, 0.4, 4.2, 7.8, 8.6 };
wc.setDepthBulk(x, y);

wc.addDepthPoint(28, 8.7);
```

After the bulk call the line is those five pairs. After the point call a new segment is drawn from the previous end to `(28, 8.7)`. The cross does not jump to either place. It moves only while the pointer is over the plot, and it leaves when the pointer leaves the plot.

An empty bulk clears the line. Pass two arrays of length 0. That is the same line result as `clearDepth()`.

Move the pointer across the plot. The vertical line follows the pointer's X. The horizontal line sits on the curve at that X, not at the pointer's Y. If the curve does not pass that X, the cross is not drawn. The number by the horizontal line is the curve's Y. The number by the vertical line is the X.

---

## 2. What the graph stores

Each call copies the numbers into its own lists. Later changes to the arrays you passed in do not move the line. To change the picture, call `setDepthBulk` or `addDepthPoint` again.

The pairs stay in data units. Changing the axis range does not edit them and does not drop them. A point outside the current range is still stored. It is drawn again when the range is widened enough to include it.

The cross is the curve value at the pointer's X. It is not the last sample, and it is not the pointer's Y. A point outside the current range is still stored and is drawn again when the range is widened enough to include it. The cross only shows while the pointer is inside the plot and the line covers that X.

---

## 3. Changing the visible range

Under the plot are four boxes: X min, X max, Y min, Y max. They open at `0`, `40`, `0`, and `10`.

Type a number and press Enter, or leave the box. The line is redrawn on the new axes. The stored points do not change.

From code, the same change is:

```java
graph.setXRange(0, 60);
graph.setYRange(0, 20);
```

The max has to be greater than the min. If it is not, the call does nothing and the boxes keep the previous range. A blank box, or a box that is only `.`, also leaves the range alone.

The boxes accept digits and one decimal point. They use the same number filter as the other number fields.

---

## 4. Calls that do nothing

`setDepthBulk` returns without drawing when either array is `null`, or when the two arrays have different lengths. The line that is already showing stays. Check the lengths before the call.

`addDepthPoint` always appends. It has no empty case.

---

## 5. Notes

- Open the WC page from the top Page drop-down. The Depth Window is the lower left panel. Eng does not show this graph.
- Do not draw the curve in the caller. Pass the numbers. `DepthGraph` paints the line, the axes, the ticks, and the cross.
- Give the pairs in the order they should be joined. The graph does not sort by X.
- Bulk and a point can be mixed. Bulk replaces. Each point after that extends the new line. The cross keeps following the pointer.
- `clearDepth()` before a new run drops the old line so the next point does not join onto it.
- Java 1.6 has no `List<double>`. The graph stores `Double` objects. The methods you call still take primitive `double` and `double[]`.
- The dummy line is only so the cross has a curve under it. Open WC and move the pointer over the plot. The vertical line follows the pointer. The horizontal line stays on the curve.

# If a value will not go on the form

Two methods write a saved weapon configuration onto a form.

| Form | Method |
|---|---|
| Engagement Parameters | `applyWeapon` |
| Create, Modify, and List weapon config | `apply` |

Both methods call getters only. A missing `toInitialSpeed`, `configNameText`, `getKnots`, or `getMeters` is not used. A bad value leaves that one box blank. The rest of the form still fills.

Change the one line for the field that failed. Leave the other lines.

---

## What each getter becomes

| Getter | On the form |
|---|---|
| `getWeapon()` | EHWT WAR or EHWT EXE |
| `getConfigName()` | the name text. It is a `char[]` |
| `getInitialSpeed()` | the Initial Speed box. On Create it is the 24, 30, 35, 40, 45, 50 list |
| `getOrderedSpeed()`, `getSearchSpeed()` | the speed text |
| `getOrderedDepth()` | Ordered Depth |
| `getEnbDist()`, `getUdl()`, `getLdl()`, `getMaxRd()`, `getAttackSonarRange()` | the distance text |
| `getAsh()`, `getAcousticHeadMode()`, `getAttackMode()`, `getWakeGuidance()`, `getGuidanceMode()`, `getHomingMode()` | the matching row in that drop-down |
| `getComments()` | WC Comments |

A `Double` or `Float` is written as the number. A `Speed`, `Depth`, or `Distance` is written from the number in its text, such as `24` or `24 kn`.

---

## When one value errors

| What you see | What to do |
|---|---|
| `configNameText()` is undefined | Use `new String(config.getConfigName())`. That is `nameText`. Do not add `configNameText` to their struct. |
| `getMeters` or `getKnots` is undefined | Do not call it. Pass the getter to `numberText`. The form reads the number from the object text. |
| `toInitialSpeed`, `toEnbDist`, or any other `to…` is undefined | Delete that call. Use the getter: `numberText(config.getInitialSpeed())`. |
| The speed or distance box stays blank and there is no error | Their object text has no number in it. Print `String.valueOf(config.getInitialSpeed())`. If that is not `24` or `24 kn`, take the number with the getter they do have and pass that `Double` or `Float` to `numberText`. |
| Initial Speed list does not select 24 | The list rows are the texts `24`, `30`, `35`, `40`, `45`, `50`. `knotsOf` must return `24`, not a `Speed`. The call is `showChoice(initialSpeed, knotsOf(config.getInitialSpeed()))`. |
| EHWT WAR / EHWT EXE stays empty | `getWeapon()` returned a name the radios do not show. `EHWT_WAR` matches `EHWT WAR`. If their name is different, select the radio with the words on the button. |
| Acoustic, attack, wake, guidance, ASH, or homing stays empty | The drop-down holds words. The getter returned an enum. `choose` matches `PASSIVE` to `Passive`, `BG_RIDER_OUT` to `Bg Rider Out`, and `ON` to `ON`. Do not use `setSelectedItem` with the enum. If their word is different, compare that word. |
| `setSelectedItem` compiles but the box stays blank | The box does not contain that object. Walk the rows with `choose` or `showChoice`. |
| Config name drop-down stays empty on Modify or List | The names come from `getAllWc()` on the model collection, then only the clicked weapon type is kept. A WAR name does not appear after EHWT EXE. See `docs/Weapon type loads the config list.md`. |
| `getAllWc` does not compile | Their method name differs. Call the method that returns every model in the map. If they gave you the `Map` itself, pass `map.values()`. |
| A comment line throws on null | Use `config.getComments()` and write `""` when it is null. |
| One getter does not exist | Delete that one line. Do not invent the method on their struct. |

An empty box is a value their object did not carry. It is not a failure of the other fields.

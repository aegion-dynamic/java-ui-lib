# Remove metadata from git

Do this in the other project, in the folder that contains its `.git` directory.

The files look like this:

```text
./metadata/abc/cdf/cdfg/h.java
```

Everything under `metadata/` should stay on disk and drop out of Git. One command covers the whole tree, including every nested folder and every `.java` file inside it.

---

## 1. See what is tracked

From the other project’s root:

```bash
git ls-files metadata
```

Each line is a file Git is still tracking. If nothing prints, Git is not tracking `metadata/`. Stop there.

---

## 2. Ignore the folder

Open `.gitignore` in that project and add:

```text
metadata/
```

The trailing slash means the folder. Saving this file does not yet untrack files Git already has.

---

## 3. Take the folder out of the index

`--cached` removes `metadata/` from Git and leaves the files on disk. Without `--cached`, `git rm` deletes the `.java` files from disk as well.

```bash
git rm -r --cached metadata
```

`git status` then shows those paths as deleted from Git. The folder is still on disk. New files under `metadata/` stay untracked because `.gitignore` now covers them.

---

## 4. Commit

The removal is only local until you commit it:

```bash
git status
git add .gitignore
git commit -m "Stop tracking the metadata folder."
```

Push that commit when you want the remote to match. Older commits still contain the files. This commit stops tracking them from here forward.

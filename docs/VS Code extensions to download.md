# VS Code extensions to download

Download these on a machine that has internet. They are for Linux x86_64. The browser saves the file without a `.vsix` ending. Rename each one so the name ends in `.vsix`, copy them to the Red Hat machine, and install the Java one first.

The debugger and the project view need VS Code 1.95 or newer. The Java package is a pre-release build and already contains a Linux JRE for its language server.

| Extension | Save as | Download |
|---|---|---|
| Language Support for Java by Red Hat | `redhat.java-linux-x64.vsix` | [linux-x64 file](https://redhat.gallerycdn.vsassets.io/extensions/redhat/java/1.59.2026111308/1794557668376/Microsoft.VisualStudio.Services.VSIXPackage) |
| Debugger for Java | `vscode-java-debug.vsix` | [file](https://vscjava.gallerycdn.vsassets.io/extensions/vscjava/vscode-java-debug/0.60.2026110205/1793598222923/Microsoft.VisualStudio.Services.VSIXPackage) |
| Project Manager for Java | `vscode-java-dependency.vsix` | [file](https://vscjava.gallerycdn.vsassets.io/extensions/vscjava/vscode-java-dependency/0.27.2026092100/1789962226558/Microsoft.VisualStudio.Services.VSIXPackage) |
| Maven for Java | `vscode-maven.vsix` | [file](https://vscjava.gallerycdn.vsassets.io/extensions/vscjava/vscode-maven/0.46.0/1791434800298/Microsoft.VisualStudio.Services.VSIXPackage) |

Pages, if you want the description before the file:

* [Language Support for Java](https://marketplace.visualstudio.com/items?itemName=redhat.java)
* [Debugger for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-debug)
* [Project Manager for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-dependency)
* [Maven for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-maven)

On the Red Hat machine: Extensions, **Install from VSIX…**, Java first, then the other three. Maven is only useful if that project builds with Maven.

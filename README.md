# Eclipse FixCNChar Eclipse Plugin

能够自动将全角中文标点（如中文逗号、分号、句号、引号等）替换为半角标点。该工具帮助开发者在编码时保持标点统一，无需频繁切换输入法。

## Use
In the eclipse java editor window, right click and select Source -> Generate Builder Pattern Code or Ctrl + Alt + P.

![screenshot](https://raw.github.com/tlcsdm/eclipse-fixcnchar/master/plugins/com.tlcsdm.eclipse.fixcnchar/images/usage-context-menu-option.jpg)

Then select which fields you want to expose in the builder.

![screenshot](https://raw.github.com/tlcsdm/eclipse-fixcnchar/master/plugins/com.tlcsdm.eclipse.fixcnchar/images/usage-selection-window.jpg)

## Build

This project uses [Tycho](https://github.com/eclipse-tycho/tycho) with [Maven](https://maven.apache.org/) to build. It requires Maven 3.9.0 or higher version.

Dev build:

```
mvn clean verify
```

Release build:

```
mvn clean org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=2.0.0 verify
```

## Install

1. Add `https://raw.githubusercontent.com/tlcsdm/eclipse-fixcnchar/master/update_site/` as the upgrade location in Eclipse.
2. Download from [Jenkins](https://jenkins.tlcsdm.com/job/eclipse-plugin/job/eclipse-fixcnchar)

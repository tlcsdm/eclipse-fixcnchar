# Eclipse FixCNChar Eclipse Plugin

能够自动将全角中文标点（如中文逗号、分号、句号、引号等）替换为半角标点。该工具帮助开发者在编码时保持标点统一，无需频繁切换输入法。

Now Support Eclipse 2024-06 and later, Mac OS X, Linux (with nautilus) and Windows.

## Features
* 在 Eclipse 编辑器中输入时，自动将中文标点符号（， 。 ； ： “ ” ‘ ’ （ ） 【 】 《 》）替换为英文符号。
* 使用快捷键 `Ctrl+Alt+R`，可将选中内容中的中文标点一次性替换为英文标点。
* 在 Preferences 中配置替换规则（中文 → 英文映射），满足个性化需求。
* 在 Preferences 中可切换是否启用实时替换功能。
* 所有替换操作均支持 Eclipse 的撤销/重做机制。

## Use
Window -> Preferences -> General -> Fix Chinese Characters

![screenshot](https://raw.github.com/tlcsdm/eclipse-fixcnchar/master/plugins/com.tlcsdm.eclipse.fixcnchar/images/preferences.png)

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

1. Add `https://raw.githubusercontent.com/tlcsdm/eclipse-fixcnchar/update_site/` as the upgrade location in Eclipse.
2. Download from [Jenkins](https://jenkins.tlcsdm.com/job/eclipse-plugin/job/eclipse-fixcnchar)
3. <table style="border: none;">
  <tbody>
    <tr style="border:none;">
      <td style="vertical-align: middle; padding-top: 10px; border: none;">
        <a href='http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=7016562' title='Drag and drop into a running Eclipse Indigo workspace to install eclipse-fixcnchar'> 
          <img src='https://marketplace.eclipse.org/modules/custom/eclipsefdn/eclipsefdn_marketplace/images/btn-install.svg'/>
        </a>
      </td>
      <td style="vertical-align: middle; text-align: left; border: none;">
        ← Drag it to your eclipse workbench to install! (I recommand Main Toolbar as Drop Target)
      </td>
    </tr>
  </tbody>
</table>

<img src="https://plugins.jetbrains.com/files/12693/65249/icon/pluginIcon_dark.svg" height="100" alt="Plugin's icon"/>

# Flutter Enhancement Suite

[![Pub](https://img.shields.io/jetbrains/plugin/v/12693-flutter-enhancement-suite.svg?label=Flutter%20Enhancement%20Suite)](https://plugins.jetbrains.com/plugin/12693-flutter-enhancement-suite)

The essential plugin for making working with Flutter easier than ever!
<p>
Tools for managing your pubspec.yaml, snippets & more

## :sparkles:&nbsp;Features

<h4>Autocomplete Pub Packages</h4>
<p>When adding a new package to your pubspec.yaml file, suggestions will show up. After auto-completing the name, the latest version will be added automatically.</p>
<p>
<img src="https://giant.gfycat.com/PastObviousAmericanquarterhorse.gif" width="400" alt="Autocomplete pub packages screenshot"/>
<p>



<h4>Update Pub Packages</h4>
<p>If there's a new version available for a dependency, it will be highlighted. You can update these packages by opening the quick fix menu (Show Intention Actions) and choosing <em>"Update package"</em>.</p>
<p>
<img src="https://i.imgur.com/orIVdDj.png" width="400" alt="Update pub packages screenshot"/>
<p>



<h4>View Pub Package's Documentation</h4>
<p>When auto-completing packages or viewing packages inside your pubspec.yaml, press <code>Ctrl+Q</code> to show additional information for the package such as its documentation or a link which leads to examples showing how to use the plugin. In order to get a larger dialog for reading the documentation, click the settings icon in the documentation dialog and select the option <em>"Open as tool window"</em>.</p>
<p>
<img src="https://i.imgur.com/yTlNDiK.png" width="400" alt="View package's documentation screenshot"/>
<p>



<h4>Dart & Flutter Code Snippets</h4>
A bunch of snippets/LiveTemplates for quicker coding. Use <code>Ctrl+Q</code> when hovering over a snippet to get a preview of it.<br>
<ul>
    <li>Dart snippets (<a href="https://github.com/marius-h/flutter_enhancement_suite/blob/master/SNIPPETS.md#dart-code-snippets">Show snippets</a>)</li>
    <li>Flutter snippets (<a href="https://github.com/marius-h/flutter_enhancement_suite/blob/master/SNIPPETS.md#flutter-widget-snippets">Show snippets</a>)</li>
    <li>Flutter Hooks snippets (<a href="https://github.com/marius-h/flutter_enhancement_suite/blob/master/SNIPPETS.md#flutter-hooks-snippets">Show snippets</a>)</li>
</ul>
<p>



<h4>Generate Widgets</h4>
<p>When creating a new file, choose the <em>"New Flutter Widget"</em> option.
Enter the name for your widget and select its type.</p>
<p>
<img src="https://i.imgur.com/FKQtOVa.png" width="400" alt="Generate widgets screenshot"/>
<p>



<h4>Generate Blocs</h4>
<p>When creating a new file, choose the <em>"New Flutter Bloc"</em> option.
Enter a name for your bloc and it will automatically generate all required classes for you.<br>

><strong>Please note:</strong> This requires you to add the package <a href="https://pub.dev/packages/bloc">bloc</a> to your pub dependencies.
<p>



<h4>Edit Linting Rules</h4>
<p>When viewing the <em>"analysis_options.yaml"</em> file, 
select the "Linter Rules Editor" tab at the bottom to see all linting options available.
</p>
<p>
<img src="https://i.imgur.com/kg9GeiY.png" width="400" alt="Edit linting rules screenshot"/>
<p>



<h4>Icon Previews</h4>
<p>Preview icons from different icon packs in the sidebar.<br>

><strong>Please note:</strong> This requires you to add the package <a href="https://pub.dev/packages/flutter_vector_icons">flutter_vector_icons</a> to your pub dependencies. The only compatible iconpacks for now are FontAwesome, Ionicons and MaterialCommunityIcons.</p>
<p>
<img src="https://i.imgur.com/JB9MkjA.png" width="400" alt="Icon previews screenshot"/>
<p>


## :arrow_down:&nbsp;How to install?

You can install plugin directly from IntelliJ IDEA or Android Studio:
1. Open _Preferences_
2. Choose _Plugins_
3. Select the _Marketplace_ tab
4. Search for **Flutter Enhancement Suite**
5. Click on _install_

## :card_file_box:&nbsp;How can I contribute to this plugin?

* Create a [bug report](https://github.com/marius-h/flutter_enhancement_suite/issues/new?assignees=&labels=bug&template=i-encountered-a-bug-while-using-the-plugin.md&title=) when accounting a bug
* Create a [feature request](https://github.com/marius-h/flutter_enhancement_suite/issues/new?assignees=&labels=enhancement&template=i-d-like-to-request-a-feature.md&title=)
* Open up a [pull request](https://github.com/marius-h/flutter_enhancement_suite/pulls)

## :fire:&nbsp;What's new?

### 1.3
<ul>
    <li>Added GUI for editing linting rules</li>
    <li>Added refactor option to extract widgets to a new file (#13)</li>
    <li>Added snippets for Flutter Hooks (#14)</li>
    <li>Added more dart snippets (#10)</li>
    <li>Fixed PicoPluginExtensionInitializationException (#16)</li>
</ul>

### 1.2
<ul>
    <li>Added documentation preview for pub packages (#6)</li>
    <li>Added feature to automatically run "pub get" when auto-completing or updating a package (#4)</li>
    <li>Added more snippets (#9)</li>
    <li>Added bug report generator</li>
    <li>Fixed fstreambuilder snippet (#7)</li>
    <li>Fixed plugin exception (#5)</li>
</ul>

### 1.1.3
<ul>
    <li>Fixed plugin verification error due to missing Java dependency</li>
</ul>

### 1.1.2
<ul>
    <li>Fixed compatibility issues with Android Studio 3.4.2</li>
</ul>

### 1.1.1
<ul>
    <li>Added icon previews</li>
    <li>Fixed issue caused by autocompleting pub dependencies</li>
</ul>

### 1.1.0
<ul>
    <li>Added "New Flutter Widget" option to the "New File" menu</li>
    <li>Added "New Flutter Bloc" option to the "New File" menu</li>
    <li>Added even more snippets</li>
    <li>Improved the performance of autocompleting pub dependencies</li>
</ul>

### 1.0.0
<ul>
    <li>Added support for automatically checking for updates in pubspec.yaml</li>
    <li>Added support for autocompleting pub.dev dependencies</li>
    <li>Added snippets</li>
</ul>
# :fire:&nbsp;What's new?

## Unreleased

## 1.7.0 - 2023-09-17

### Added
- Variable type hints
- Parameter name hints
- Code vision support for insights of usages, implementations and code contributors

### Changed
- Improve performance and reliability of loading changelogs (#227)
- Improve performance and reliability of loading documentations (#227)
- Include this plugin's version in bug reports

### Fixed
- Fix null exception for PsiElement.getParent() (#254)
- Fix "Make private action doesn't create a proper constructor signature" (#255)
- Fix pub changelog provider enabled for non-Flutter projects (#256)

## 1.6.1 - 2023-07-07

### Added
- Show breadcrumbs to easily navigate through Dart files

### Changed
- Improve version detection for dependency in pubspec.yaml

### Fixed
- Fix `NoSuchElementException`
- Other minor bugs

### Removed
- Lint editor (for now)

## 1.6.0

### Added
- Open a package's pub.dev page directly from your pubspec.yaml (#121)
- Make a variable, function or class private or public via alt+enter (#107)
- Custom file templates for "New Flutter widget" menu
- Run `flutter pub get` from menu by right-clicking a file or a directory (#52)

### Changed
- Const constructor added for StatelessWidget and StatefulWidget templates (#141)

### Fixed
- Fix "Document is locked by write PSI operations" (#124)

### Removed
- Remove coverage feature
- Remove bloc feature

## 1.5.0

### Added
- Display test coverage in IDE

### Changed
- Major performance improvements for package version checking
- Auto-import packages when extracting a widget to a new file

### Deprecated
- Deprecated bloc feature. Please use the official bloc plugin (https://plugins.jetbrains.com/plugin/12129-bloc)

### Fixed
- Fix package update suggestions for pre-release versions (#77)
- Fix adding a dependency to a pubspec file while another project is open cause pub get to run in the other project (#55)

## 1.4.0

### Added
- Display CHANGELOG.md while selecting version of package in pubspec.yaml (#27)

### Fixed
- Fix 'Update package' hint at wrong location when followed by comment (#60)
- Fix Dart Live Templates enabled in comments (#51)
- Fix document locked by PSI write operations (#64)
- Fix plugin interfering with "flutter" in environment namespace (#56)
- Fix displaying README.md of package in pubspec.yaml requires disabling JSON schemes in IDE

### Removed
- Remove Java dependency needed to install plugin

## 1.3.3
- Fixed Error getting Flutter SDK information when auto-completing a package (#44)
- Fixed Error when generating a new bloc (#28,#33)
- Fixed Incompatibility with Kubernetes plugin (#30)

## 1.3.2
- Added option to update a package without running <em>pub get</em> (#21)
- Added icon preview support for <em>material_design_icons</em> (#23)
- Added BehaviorSubject snippet (#24)
- Improved widget-to-file extractor
- Improved performance of documentation provider for pub packages
- Fixed UI freezing when opening pubspec.yaml (#20)
- Fixed error when using the FlutterBlocGenerator plugin in Android Studio (#22)
- Fixed ConcurrentModificationException (#25)
- Fixed UnableToGetLatestVersionException (#26)

## 1.3.1
- Improved pub documentation view
- Fixed error when opening pubspec.yaml (#18)

## 1.3.0
- Added GUI for editing linting rules
- Added refactor option to extract widgets to a new file (#13)
- Added snippets for Flutter Hooks (#14)
- Added more dart snippets (#10)
- Fixed PicoPluginExtensionInitializationException (#16)

## 1.2.0
- Added documentation preview for pub packages (#6)
- Added feature to automatically run "pub get" when auto-completing or updating a package (#4)
- Added more snippets (#9)
- Added bug report generator
- Fixed fstreambuilder snippet (#7)
- Fixed plugin exception (#5)

## 1.1.3
- Fixed plugin verification error due to missing Java dependency

## 1.1.2
- Fixed compatibility issues with Android Studio 3.4.2

## 1.1.1
- Added icon previews
- Fixed issue caused by autocompleting pub dependencies

## 1.1.0
- Added "New Flutter Widget" option to the "New File" menu
- Added "New Flutter Bloc" option to the "New File" menu
- Added even more snippets
- Improved the performance of autocompleting pub dependencies

## 1.0.0
- Added support for automatically checking for updates in pubspec.yaml
- Added support for autocompleting pub.dev dependencies
- Added snippets

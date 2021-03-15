# :fire:&nbsp;What's new?

## [Unreleased]
### Added
- Display test coverage in IDE

## [1.4.0]
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

## [1.3.3]
- Fixed Error getting Flutter SDK information when auto-completing a package (#44)
- Fixed Error when generating a new bloc (#28,#33)
- Fixed Incompatibility with Kubernetes plugin (#30)

## [1.3.2]
- Added option to update a package without running <em>pub get</em> (#21)
- Added icon preview support for <em>material_design_icons</em> (#23)
- Added BehaviorSubject snippet (#24)
- Improved widget-to-file extractor
- Improved performance of documentation provider for pub packages
- Fixed UI freezing when opening pubspec.yaml (#20)
- Fixed error when using the FlutterBlocGenerator plugin in Android Studio (#22)
- Fixed ConcurrentModificationException (#25)
- Fixed UnableToGetLatestVersionException (#26)

## [1.3.1]
- Improved pub documentation view
- Fixed error when opening pubspec.yaml (#18)

## [1.3.0]
- Added GUI for editing linting rules
- Added refactor option to extract widgets to a new file (#13)
- Added snippets for Flutter Hooks (#14)
- Added more dart snippets (#10)
- Fixed PicoPluginExtensionInitializationException (#16)

## [1.2.0]
- Added documentation preview for pub packages (#6)
- Added feature to automatically run "pub get" when auto-completing or updating a package (#4)
- Added more snippets (#9)
- Added bug report generator
- Fixed fstreambuilder snippet (#7)
- Fixed plugin exception (#5)

## [1.1.3]
- Fixed plugin verification error due to missing Java dependency

## [1.1.2]
- Fixed compatibility issues with Android Studio 3.4.2

## [1.1.1]
- Added icon previews
- Fixed issue caused by autocompleting pub dependencies

## [1.1.0]
- Added "New Flutter Widget" option to the "New File" menu
- Added "New Flutter Bloc" option to the "New File" menu
- Added even more snippets
- Improved the performance of autocompleting pub dependencies

## [1.0.0]
- Added support for automatically checking for updates in pubspec.yaml
- Added support for autocompleting pub.dev dependencies
- Added snippets

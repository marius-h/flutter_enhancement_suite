import 'dart:convert';
import 'dart:io';

import 'icon_packs.dart';

final String root =
    'd:/Dokumente/Coding/flutterenhancementsuite/src/main/resources/flutter';

void main() async {
  for (IconPack iconPack in iconPacks) {
    String iconData = await downloadUrl(iconPack.url);

    List<Icon> icons = parseIconData(iconData);

    generateProperties(
      icons,
      '$root/${iconPack.name}_icons.properties',
      iconPack.name,
    );

    generateDart(
      icons,
      '../icon_generator/lib/${iconPack.name}_icons.dart',
      iconPack.prefix,
      iconPack.import,
    );

    await Directory('$root/${iconPack.name}').create();
  }

  // tell the user how to generate the icons
  print('');
  print("In order to re-generate the icons, open the iOS Simulator, and "
      "'flutter run' from the tool/icon_generator/ directory.");
}

Future<String> downloadUrl(String url) async {
  HttpClientRequest request = await HttpClient().getUrl(Uri.parse(url));
  HttpClientResponse response = await request.close();
  List<String> data = await utf8.decoder.bind(response).toList();
  return data.join('');
}

// The pattern below is meant to match lines like:
//   'static const IconData threesixty = IconData(0xe577,'
final RegExp regexp = RegExp(r'static const IconData (\S+) = IconData\((\S+),');

List<Icon> parseIconData(String data) {
  return regexp.allMatches(data).map((Match match) {
    try {
      return Icon(match.group(1), int.parse(match.group(2), radix: 16));
    } catch (e) {
      String hexCode = int.parse(match.group(2)).toRadixString(16);
      return Icon(match.group(1), int.parse(hexCode));
    }
  }).toList();
}

void generateProperties(List<Icon> icons, String filename, String pathSegment) {
  StringBuffer buf = StringBuffer();
  buf.writeln('# Generated file - do not edit.');
  buf.writeln();
  buf.writeln('# suppress inspection "UnusedProperty" for whole file');

  Set<int> set = Set();

  for (Icon icon in icons) {
    buf.writeln();

    if (set.contains(icon.codepoint)) {
      buf.write('# ');
    }

    buf.writeln('${icon.codepoint.toRadixString(16)}.codepoint=${icon.name}');
    buf.writeln('${icon.name}=/flutter/$pathSegment/${icon.name}.png');

    set.add(icon.codepoint);
  }

  File(filename).writeAsStringSync(buf.toString());

  print('wrote $filename');
}

void generateDart(
    List<Icon> icons, String filename, String prefix, String import) {
  StringBuffer buf = StringBuffer();
  buf.writeln('''
// Generated file - do not edit.

import 'package:flutter/widgets.dart';
import 'package:$import.dart';

class IconTuple {
  final IconData data;
  final String name;
  final Key smallKey = UniqueKey();
  final Key largeKey = UniqueKey();
  IconTuple(this.data, this.name);
}

final List<IconTuple> icons = [''');

  for (Icon icon in icons) {
    buf.writeln('  IconTuple($prefix.${icon.name}, \'${icon.name}\'),');
  }

  buf.writeln('];');

  File(filename).writeAsStringSync(buf.toString());

  print('wrote $filename');
}

class Icon {
  final String name;
  final int codepoint;

  Icon(this.name, this.codepoint);

  String toString() => '$name 0x${codepoint.toRadixString(16)}';
}

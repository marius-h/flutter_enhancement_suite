import 'dart:io';
import 'dart:typed_data';
import 'dart:ui' as ui;

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:path_provider/path_provider.dart';

import 'font_awesome_icons.dart' as fontawesome;
import 'ion_icons.dart' as ion;
import 'material_community_icons.dart' as materialcommunity;

String root = '';

Future main() async {
  MyIconApp app = MyIconApp(materialcommunity.icons);
  runApp(app);

  await pumpEventQueue();

  root = await _localPath;

  // TODO(devoncarew): Below, we could queue up some or all findAndSave()
  // operations and then wait for all futures to complete (using a Pool?).
  // Assuming we don't get out of memory issues this might finish much faster as
  // there is a decent amount of delay getting data from the gpu for each icon.

//  await Directory('$root/font_awesome').create();
  await Directory('$root/material_community').create();

  //await saveAllIcons(fontawesome.icons, 'font_awesome');
  //await saveAllIcons(ion.icons, 'ion');
  await saveAllIcons(materialcommunity.icons, 'material_community');
}

saveAllIcons(List icons, String directory) async {
  for (var icon in icons) {
    await findAndSave(
      icon.smallKey,
      '$root/$directory/${icon.name}.png',
      small: true,
    );
    await findAndSave(
      icon.largeKey,
      '$root/$directory/${icon.name}@2x.png',
      small: false,
    );
  }
}

class MyIconApp extends StatelessWidget {
  MyIconApp(this.icons) : super(key: new UniqueKey());

//  final List<material.IconTuple> materialIcons;
  final List icons;

  @override
  Widget build(BuildContext context) {
    // We use this color as it works well in both IntelliJ's light theme and in
    // Darkula.
    const Color color = const Color(0xFF777777);

    Stack iconsSmallStack = new Stack(
      children: icons.map<Widget>((icon) {
        return RepaintBoundary(
          child: Icon(
            icon.data,
            size: 16.0,
            color: color,
            key: icon.smallKey,
          ),
        );
      }).toList(),
    );

    Stack iconsLargeStack = new Stack(
      children: icons.map<Widget>((icon) {
        return RepaintBoundary(
          child: Icon(
            icon.data,
            size: 32.0,
            color: color,
            key: icon.largeKey,
          ),
        );
      }).toList(),
    );

    return MaterialApp(
      title: 'Flutter Demo',
      home: Center(
        child: new Column(
          children: <Widget>[
            new Row(children: <Widget>[
              iconsSmallStack,
            ]),
            new Row(children: <Widget>[
              iconsLargeStack,
            ]),
          ],
        ),
      ),
    );
  }
}

Future get _localPath async {
  final directory = await getExternalStorageDirectory();

  return directory.path;
}

Future findAndSave(Key key, String path, {bool small: true}) async {
  Finder finder = find.byKey(key);

  final Iterable<Element> elements = finder.evaluate();
  Element element = elements.first;

  Future<ui.Image> imageFuture = _captureImage(element);

  final ui.Image image = await imageFuture;
  final ByteData bytes = await image.toByteData(format: ui.ImageByteFormat.png);

  await File(path).writeAsBytes(bytes.buffer.asUint8List());

  print('wrote $path');
}

Future<ui.Image> _captureImage(Element element) {
  RenderObject renderObject = element.renderObject;
  while (!renderObject.isRepaintBoundary) {
    renderObject = renderObject.parent;
    assert(renderObject != null);
  }

  //assert(!renderObject.debugNeedsPaint);

  final OffsetLayer layer = renderObject.layer;
  return layer.toImage(renderObject.paintBounds);
}

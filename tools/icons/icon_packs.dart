final List<IconPack> iconPacks = [
  IconPack(
    'https://github.com/pd4d10/flutter-vector-icons/raw/master/flutter_vector_icons/lib/src/font_awesome.dart',
    'font_awesome',
    'FontAwesome',
  ),
  IconPack(
    'https://github.com/pd4d10/flutter-vector-icons/raw/master/flutter_vector_icons/lib/src/ionicons.dart',
    'ion',
    'Ionicons',
  ),
  IconPack(
    'https://github.com/pd4d10/flutter-vector-icons/raw/master/flutter_vector_icons/lib/src/material_community_icons.dart',
    'material_community',
    'MaterialCommunityIcons',
  ),
];

class IconPack {
  IconPack(
    this.url,
    this.name,
    this.prefix, {
    this.import = 'flutter_vector_icons/flutter_vector_icons',
  });

  final String url;
  final String name;
  final String prefix;
  final String import;
}

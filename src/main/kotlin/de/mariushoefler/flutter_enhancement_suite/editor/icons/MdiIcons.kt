package de.mariushoefler.flutter_enhancement_suite.editor.icons

import de.mariushoefler.flutter_enhancement_suite.editor.FlutterIcons
import de.mariushoefler.flutter_enhancement_suite.utils.toSnakeCase
import javax.swing.Icon

object MdiIcons : FlutterIcons<MdiIcons>("material_community", MdiIcons::class.java) {

	override fun getIcon(name: String?): Icon? {
		println("MdiIcons Name: $name -> ${name?.toSnakeCase()}")
		return super.getIcon(name?.toSnakeCase())
	}
}

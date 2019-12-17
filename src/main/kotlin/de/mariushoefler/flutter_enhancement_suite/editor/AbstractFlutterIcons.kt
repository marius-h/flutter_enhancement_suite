package de.mariushoefler.flutter_enhancement_suite.editor

import com.intellij.openapi.util.IconLoader
import java.util.*
import javax.swing.Icon

abstract class AbstractFlutterIcons<T>(fileName: String, private val iconClass: Class<T>) {

	private val icons: Properties = Properties()

	init {
		try {
			icons.load(this::class.java.classLoader.getResourceAsStream("/flutter/${fileName}_icons.properties"))
		} catch (e: Exception) {
		}
	}

	fun getIconByCode(code: String): Icon? {
		val iconName = icons.getProperty("$code.codepoint")
		return getIcon(iconName)
	}

	open fun getIcon(name: String?): Icon? {
		if (name == null) {
			return null
		}
		val path: String = icons.getProperty(name) ?: return null
		return IconLoader.findIcon(path, iconClass)
	}
}